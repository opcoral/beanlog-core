package io.github.opcoral.oplog.util;

import io.github.opcoral.oplog.annotation.OpLogBean;
import io.github.opcoral.oplog.annotation.OpLogField;
import io.github.opcoral.oplog.annotation.OpLogInside;
import io.github.opcoral.oplog.core.OpLogObjectLoader;
import io.github.opcoral.oplog.entity.config.OpLogConfig;
import io.github.opcoral.oplog.enums.OpLogBoolean;
import io.github.opcoral.oplog.exception.OpLogException;

import java.lang.reflect.*;
import java.util.*;

/**
 * OpLog的配置工具<br>
 *
 * @author GuanZH
 * @since 2023/5/14 15:59
 */
public class OpLogConfigEditor {

    private OpLogObjectLoader objectLoader;
    private final Field[] opLogConfigFields;
    private final Map<String, Field> configFieldMap = new HashMap<>();
    private final Map<Method, Field> logBeanConfigMap;
    private final Map<String, Method> logBeanConfigMapIgnoreBlank;
    private final Map<Method, Field> logFieldConfigMap;
    private final Map<String, Method> logFieldConfigMapIgnoreBlank;
    private final Map<Method, Field> logInsideConfigMap;
    private final Map<String, Method> logInsideConfigMapIgnoreBlank;

    {
        // 定义ObjectLoader
        objectLoader = clazz -> {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new OpLogException("Failed to load class " + clazz.getName() + ", the Exception message is: " + e.getMessage(), e);
            }
        };

        // 常量定义
        String IGNORE_BLANK_TAG_ANNOTATION_NAME_SUFFIX = "IgnoreBlank";

        // 初始化configFieldMap 属性名:属性
        opLogConfigFields = OpLogConfig.class.getDeclaredFields();
        for(Field field : opLogConfigFields) {
            field.setAccessible(true);
            configFieldMap.put(field.getName(), field);
        }

        // 初始化logBeanConfigMap OpLogBean的方法:Config的属性
        logBeanConfigMap = new HashMap<>();
        logBeanConfigMapIgnoreBlank = new HashMap<>();
        Method[] opLogBeanMethods = OpLogBean.class.getMethods();

        for(Method method : opLogBeanMethods) {
            String name = method.getName();
            Field field = configFieldMap.get(name);
            if(field != null) {
                logBeanConfigMap.put(method, field);
            }
            // 忽略的字段
            if(name.endsWith(IGNORE_BLANK_TAG_ANNOTATION_NAME_SUFFIX) &&
               method.getReturnType().equals(Boolean.TYPE)) {
                String originName = name.substring(0, name.lastIndexOf(IGNORE_BLANK_TAG_ANNOTATION_NAME_SUFFIX));
                logBeanConfigMapIgnoreBlank.put(originName, method);
            }
        }

        // 初始化logFieldConfigMap OpLogField的方法:Config的属性
        logFieldConfigMap = new HashMap<>();
        logFieldConfigMapIgnoreBlank = new HashMap<>();
        Method[] opLogFieldMethods = OpLogField.class.getMethods();
        for(Method method : opLogFieldMethods) {
            String name = method.getName();
            Field field = configFieldMap.get(name);
            if(field != null) {
                logFieldConfigMap.put(method, field);
            }
            // 忽略的字段
            if(name.endsWith(IGNORE_BLANK_TAG_ANNOTATION_NAME_SUFFIX) &&
                    method.getReturnType().equals(Boolean.TYPE)) {
                String originName = name.substring(0, name.lastIndexOf(IGNORE_BLANK_TAG_ANNOTATION_NAME_SUFFIX));
                logFieldConfigMapIgnoreBlank.put(originName, method);
            }
        }

        // 初始化logInsideConfigMap OpLogField的方法:Config的属性
        logInsideConfigMap = new HashMap<>();
        logInsideConfigMapIgnoreBlank = new HashMap<>();
        Method[] opLogInsideMethods = OpLogInside.class.getMethods();
        for(Method method : opLogInsideMethods) {
            String name = method.getName();
            Field field = configFieldMap.get(name);
            if(field != null) {
                logInsideConfigMap.put(method, field);
            }
            // 忽略的字段
            if(name.endsWith(IGNORE_BLANK_TAG_ANNOTATION_NAME_SUFFIX) &&
                    method.getReturnType().equals(Boolean.TYPE)) {
                String originName = name.substring(0, name.lastIndexOf(IGNORE_BLANK_TAG_ANNOTATION_NAME_SUFFIX));
                logInsideConfigMapIgnoreBlank.put(originName, method);
            }
        }
    }

    /**
     * 配置合并，合并配置中非空的选项，按照优先级<b>从低到高</b>
     * @param configs 要合并的所有配置信息，优先级<b>从低到高</b>排列
     * @return 合并后的配置
     */
    public OpLogConfig merge(OpLogConfig... configs) {
        return this.merge(Arrays.asList(configs));
    }

    /**
     * 配置合并，合并配置中非空的选项，按照优先级<b>从低到高</b>
     * @param configs 要合并的所有配置信息，优先级<b>从低到高</b>排列
     * @return 合并后的配置
     */
    public OpLogConfig merge(List<OpLogConfig> configs) {
        OpLogConfig finalConfig = new OpLogConfig();
        for(OpLogConfig config : configs) {
            if(config != null) {
                for (Field field : opLogConfigFields) {
                    try {
                        Object nextConfigFieldObj = field.get(config);
                        if (nextConfigFieldObj != null) {
                            // 对特殊类型的处理
                            if(nextConfigFieldObj instanceof Map) {
                                // Map
                                Map<?, ?> fieldObjMap = (Map<?, ?>) nextConfigFieldObj;
                                Map<?, ?> finalObjMap = (Map<?, ?>) field.get(finalConfig);
                                Map<Object, Object> finalMap = (finalObjMap==null)? new HashMap<>():new HashMap<>(finalObjMap);
                                finalMap.putAll(fieldObjMap);
                                field.set(finalConfig, finalMap);
                            } else if (nextConfigFieldObj instanceof List) {
                                // List
                                List<?> fieldObjList = (List<?>) nextConfigFieldObj;
                                List<?> finalObjList = (List<?>) field.get(finalConfig);
                                List<Object> finalList = (finalObjList==null)? new ArrayList<>():new ArrayList<>(finalObjList);
                                finalList.addAll(fieldObjList);
                                field.set(finalConfig, finalList);
                            } else {
                                field.set(finalConfig, nextConfigFieldObj);
                            }
                        }
                    } catch (Exception e) {
                        throw new OpLogException(e);
                    }
                }
            }
        }
        return finalConfig;
    }

    /**
     * 根据Bean注解，生成并填充config
     * @param opLogBean logBean注解。如果入参为null，返回new OpLogConfig()
     * @return 获取的配置类。如果入参为null，返回new OpLogConfig()
     */
    public OpLogConfig loadConfig(OpLogBean opLogBean) {
        return loadConfig(opLogBean, logBeanConfigMap, logBeanConfigMapIgnoreBlank);
    }

    /**
     * 根据Field注解，生成并填充config
     * @param opLogField logField。如果入参为null，返回new OpLogConfig()
     * @return 获取的配置类。如果入参为null，返回new OpLogConfig()
     */
    public OpLogConfig loadConfig(OpLogField opLogField) {
        return loadConfig(opLogField, logFieldConfigMap, logFieldConfigMapIgnoreBlank);
    }

    /**
     * 根据Inside注解，生成并填充config
     * @param opLogInside opLogInside。如果入参为null，返回new OpLogConfig()
     * @return 获取的配置类。如果入参为null，返回new OpLogConfig()
     */
    public OpLogConfig loadConfig(OpLogInside opLogInside) {
        return loadConfig(opLogInside, logInsideConfigMap, logInsideConfigMapIgnoreBlank);
    }

    private OpLogConfig loadConfig(Object annotation, Map<Method, Field> methodFieldMap, Map<String, Method> methodFieldMapIgnoreBlank) {
        OpLogConfig opLogConfig = new OpLogConfig();
        if(annotation != null) {
            for (Map.Entry<Method, Field> entry : methodFieldMap.entrySet()) {
                Method annoationMethod = entry.getKey();
                String annotationMethodName = annoationMethod.getName();
                Field configField = entry.getValue();
                Object annotationValue = null;
                // 获取注解值
                try {
                    annotationValue = annoationMethod.invoke(annotation);
                } catch (IllegalAccessException | InvocationTargetException ignored) {}
                // 特殊处理视为空的值
                if(annotationValue != null && methodFieldMapIgnoreBlank.containsKey(annotationMethodName)) {
                    try {
                        Method ignoredMethod = methodFieldMapIgnoreBlank.get(annotationMethodName);
                        boolean ignored = (boolean) ignoredMethod.invoke(annotation);
                        // 如果忽略且注解值为""或0或空数组，将查到的注解值标为null
                        if(ignored && (
                                annotationValue.equals("") ||
                                annotationValue.equals(0) ||
                                (annotationValue.getClass().isArray() && Array.getLength(annotationValue) <= 0)
                        )) {
                            annotationValue = null;
                        }
                    } catch (IllegalAccessException | InvocationTargetException ignored) {}
                }
                // 布尔值处理
                if(annotationValue instanceof OpLogBoolean) {
                    annotationValue = ((OpLogBoolean) annotationValue).getTruthBoolean();
                }
                // 注解是Class，config不是Class的时候，转换为加载的方法
                // 使得注解能够配置方法，兼容Spring的情况
                if(annotationValue instanceof Class && !Class.class.isAssignableFrom(configField.getType())) {
                    Class<?> clazz = (Class<?>) annotationValue;
                    // 跳过空
                    if(INullableClassInterface.class.isAssignableFrom(clazz)) {
                        annotationValue = null;
                    } else {
                        annotationValue = this.objectLoader.load(clazz);
                    }
                }
                // 注解是Class[]，config是List的时候，转换为加载的方法
                // 使得注解能够配置方法，兼容Spring的情况
                if(annotationValue instanceof Class[] && List.class.isAssignableFrom(configField.getType())) {
                    Class<?>[] classes = (Class<?>[]) annotationValue;
                    List<Object> list = new ArrayList<>(classes.length);
                    for(Class<?> c : classes) {
                        list.add(this.objectLoader.load(c));
                    }
                    annotationValue = list;
                }
                // 赋值
                try {
                    configField.set(opLogConfig, annotationValue);
                } catch (IllegalAccessException ignored) {}
            }
        }
        return opLogConfig;
    }

    public List<OpLogField> loadAllFieldAnnotation(Field field) {
        return this.loadAllFieldAnnotation(field.getAnnotation(OpLogField.class), field.getAnnotation(OpLogField.List.class));
    }

    public List<OpLogField> loadAllFieldAnnotation(Method method) {
        return this.loadAllFieldAnnotation(method.getAnnotation(OpLogField.class), method.getAnnotation(OpLogField.List.class));
    }

    private List<OpLogField> loadAllFieldAnnotation(OpLogField one, OpLogField.List list) {
        List<OpLogField> result = new ArrayList<>();
        // 单个注解的情况
        if(one != null) {
            result.add(one);
        }
        // 多个注解情况
        if(list != null) {
            result.addAll(Arrays.asList(list.value()));
        }
        return result;
    }

    public OpLogObjectLoader getObjectLoader() {
        return objectLoader;
    }

    public void setObjectLoader(OpLogObjectLoader objectLoader) {
        this.objectLoader = objectLoader;
    }

    /**
     * 注解中的Class类型的配置如果实现了这个接口，视为null
     */
    public interface INullableClassInterface {}
}
