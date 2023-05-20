package io.github.opcoral.beanlog.impl.builder;

import io.github.opcoral.beanlog.annotation.BeanLogBean;
import io.github.opcoral.beanlog.annotation.BeanLogField;
import io.github.opcoral.beanlog.annotation.BeanLogInside;
import io.github.opcoral.beanlog.core.*;
import io.github.opcoral.beanlog.entity.config.BeanLogConfig;
import io.github.opcoral.beanlog.entity.param.BeanLogParam;
import io.github.opcoral.beanlog.entity.parse.BeanLogParseBean;
import io.github.opcoral.beanlog.entity.parse.BeanLogParseField;
import io.github.opcoral.beanlog.entity.result.BeanLogResult;
import io.github.opcoral.beanlog.entity.result.BeanLogResultBean;
import io.github.opcoral.beanlog.entity.result.BeanLogResultField;
import io.github.opcoral.beanlog.enums.BeanLogChangeType;
import io.github.opcoral.beanlog.exception.BeanLogException;
import io.github.opcoral.beanlog.impl.comparable.DefaultComparableImpl;
import io.github.opcoral.beanlog.impl.stringConverter.CollectionConverterImpl;
import io.github.opcoral.beanlog.impl.stringConverter.DateConverterImpl;
import io.github.opcoral.beanlog.impl.stringConverter.DecimalConverterImpl;
import io.github.opcoral.beanlog.util.BeanLogConfigEditor;
import io.github.opcoral.beanlog.util.BeanLogConvertUtil;
import io.github.opcoral.beanlog.util.SpElUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 操作日志创建器的实现类<br>
 *
 * @author GuanZH
 * @since 2023/5/14 15:31
 */
public class BeanLogBuilderImpl implements BeanLogBuilder {

    private static BeanLogConfig defaultConfig;

    private BeanLogConfig builderConfig;

    private BeanLogParser logParser;

    private BeanLogGenerator logGenerator;

    private BeanLogConfigEditor logConfigEditor;

    private Field[] opLogParamFields;

    @Override
    public <T> BeanLogResult beanBuild(T before, T after, Class<T> beanClass, BeanLogConfig beanConfig) throws NullPointerException {
        // 判空
        Objects.requireNonNull(before);
        Objects.requireNonNull(after);
        Objects.requireNonNull(beanClass);
        Objects.requireNonNull(beanConfig);

        // 0. 使用懒加载初始化
        init();
        // 1. 调用类解析器，解析类为一个包含类和注解信息的实体
        BeanLogConfig parentConfig = logConfigEditor.merge(defaultConfig, builderConfig);
        List<BeanLogParseBean> parseResultList = logParser.parse(before, after, beanClass, beanConfig, parentConfig);
        // 2. 根据上文生成的类和注解信息的实体，生成解析结果的日志
        return logGenerator.generate(parseResultList);
    }

    /**
     * 初始化默认的parser和generator<br>
     * 如果没有初始化，生成日志之前会自动初始化
     */
    @Override
    public void init() {
        if(opLogParamFields == null) {
            opLogParamFields = BeanLogParam.class.getDeclaredFields();
        }
        if(defaultConfig == null) {
            // 定义一个默认的config
            staticInitDefaultConfig();
        }
        if(builderConfig == null) {
            // 新建builderConfig
            builderConfig = new BeanLogConfig();
        }
        if(logConfigEditor == null) {
            // 初始化ConfigEditor
            logConfigEditor = new BeanLogConfigEditor();
        }
        if(logParser == null) {
            // 填充默认的解析器
            logParser = new BeanLogParser() {
                @Override
                public List<BeanLogParseBean> parse(Object before, Object after, Class<?> beanClass, BeanLogConfig beanConfig, BeanLogConfig parentConfig) {
                    List<BeanLogParseBean> parseBeanList = new ArrayList<>();
                    // 读取bean注解
                    List<BeanLogBean> beanLogBeanList = new ArrayList<>();
                    {
                        BeanLogBean one = beanClass.getAnnotation(BeanLogBean.class);
                        if(one != null) {
                            beanLogBeanList.add(one);
                        }
                        BeanLogBean.List list = beanClass.getAnnotation(BeanLogBean.List.class);
                        if(list != null) {
                            beanLogBeanList.addAll(Arrays.asList(list.value()));
                        }
                    }
                    // 生成config列表
                    List<BeanLogConfig> configList = new ArrayList<>();
                    if(beanLogBeanList.isEmpty()) {
                        // 没有注解，添加默认的配置
                        configList.add(logConfigEditor.merge(parentConfig, beanConfig));
                    } else {
                        // 有注解，遍历注解
                        for(BeanLogBean beanLogBean : beanLogBeanList) {
                            BeanLogConfig beanAnnotationConfig = logConfigEditor.loadConfig(beanLogBean);
                            BeanLogConfig subConfig = logConfigEditor.merge(parentConfig, beanAnnotationConfig, beanConfig);
                            configList.add(subConfig);
                        }
                    }
                    // 遍历config列表
                    for(BeanLogConfig config : configList) {
                        BeanLogParseBean parseBean = new BeanLogParseBean();
                        parseBean.setBeanType(beanClass);
                        // 1. 读取bean的配置
                        parseBean.setFinalConfig(config);
                        // 2. 遍历field和method
                        List<BeanLogParseField> fieldList = new ArrayList<>();
                        // 2-1 遍历field
                        for (Field field : beanClass.getDeclaredFields()) {
                            // BeanLogField
                            field.setAccessible(true);
                            List<BeanLogField> fAs = logConfigEditor.loadAllFieldAnnotation(field);
                            BeanLogInside beanLogInside = field.getAnnotation(BeanLogInside.class);
                            if (fAs.size() > 0 || beanLogInside != null) {
                                try {
                                    Object beforeObj = (before == null) ? null : field.get(before);
                                    Object afterObj = (after == null) ? null : field.get(after);
                                    Class<?> type = field.getType();
                                    String fieldName = field.getName();
                                    Annotation[] annotations = field.getAnnotations();
                                    fillFieldList(beforeObj, afterObj, fieldList, fAs, beanLogInside, config, type, fieldName, beanClass, annotations);
                                } catch (IllegalAccessException e) {
                                    throw new BeanLogException(e);
                                }
                            }
                        }
                        // 2-2 遍历method
                        for (Method method : beanClass.getDeclaredMethods()) {
                            // BeanLogField
                            method.setAccessible(true);
                            List<BeanLogField> fAs = logConfigEditor.loadAllFieldAnnotation(method);
                            BeanLogInside beanLogInside = method.getAnnotation(BeanLogInside.class);
                            if (fAs.size() > 0 || beanLogInside != null) {
                                try {
                                    Class<?> type = method.getReturnType();
                                    if (type.equals(Void.TYPE) || method.getParameterCount() > 0) {
                                        throw new BeanLogException("Method {} is not supported. The return type of a method cannot be void and cannot contain input parameters", method.getName());
                                    }
                                    Object beforeObj = (before == null) ? null : method.invoke(before);
                                    Object afterObj = (after == null) ? null : method.invoke(after);
                                    String fieldName = method.getName();
                                    Annotation[] annotations = method.getAnnotations();
                                    fillFieldList(beforeObj, afterObj, fieldList, fAs, beanLogInside, config, type, fieldName, beanClass, annotations);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    throw new BeanLogException(e);
                                }
                            }
                        }
                        parseBean.setFields(fieldList);
                        // 3. 添加
                        parseBeanList.add(parseBean);
                    }
                    // 返回
                    return parseBeanList;
                }

                private void fillFieldList(Object beforeObj, Object afterObj, List<BeanLogParseField> fieldList, List<BeanLogField> beanLogFields, BeanLogInside beanLogInside, BeanLogConfig parentConfig, Class<?> fieldType, String fieldName, Class<?> beanType, Annotation[] annotations) {
                    for(BeanLogField annotation : beanLogFields) {
                        // 遍历注解 BeanLogField
                        BeanLogConfig opFieldConfig = logConfigEditor.loadConfig(annotation);
                        BeanLogConfig finalConfig = logConfigEditor.merge(parentConfig, opFieldConfig);
                        // 填充
                        BeanLogParseField parseField = new BeanLogParseField();
                        parseField.setIsInsideBean(false);
                        parseField.setFinalConfig(finalConfig);
                        parseField.setBeforeObj(beforeObj);
                        parseField.setAfterObj(afterObj);
                        parseField.setType(fieldType);
                        parseField.setFieldName(fieldName);
                        parseField.setInsideBean(null);
                        parseField.setBeanType(beanType);
                        parseField.setAnnotations(annotations);
                        fieldList.add(parseField);
                    }
                    if(beanLogInside != null) {
                        // 合并配置
                        BeanLogConfig insideConfig = logConfigEditor.loadConfig(beanLogInside);
                        BeanLogConfig insideFinalConfig = logConfigEditor.merge(parentConfig, insideConfig);
                        // 运行一次递归
                        List<BeanLogParseBean> insideBeanList = this.parse(beforeObj, afterObj, fieldType, new BeanLogConfig(), insideFinalConfig);
                        // BeanLogInside 注解不为空
                        BeanLogParseField parseField = new BeanLogParseField();
                        parseField.setIsInsideBean(true);
                        parseField.setFinalConfig(insideFinalConfig);
                        parseField.setBeforeObj(beforeObj);
                        parseField.setAfterObj(afterObj);
                        parseField.setType(fieldType);
                        parseField.setFieldName(fieldName);
                        parseField.setInsideBean(insideBeanList);
                        parseField.setBeanType(beanType);
                        parseField.setAnnotations(annotations);
                        fieldList.add(parseField);
                    }
                }
            };
        }
        if(logGenerator == null) {
            // 填充默认的生成器
            logGenerator = new BeanLogGenerator() {
                @Override
                public BeanLogResult generate(List<BeanLogParseBean> parseResultList) {
                    // 日志组成：([全局前缀] [属性1],[属性2],...,[属性n] [全局后缀])x权限组匹配的Bean注解数量
                    // 生成结果实体
                    BeanLogResult result = new BeanLogResult();
                    List<BeanLogResultBean> resultBeanList = new ArrayList<>();
                    // 遍历Bean
                    for (BeanLogParseBean parseBean : parseResultList) {
                        // 1. 检查权限组，如果权限组不符合，跳过
                        BeanLogConfig beanConfig = parseBean.getFinalConfig();
                        if (beanConfig.getGroups() != null && beanConfig.getCurrentGroup() != null &&
                                !Arrays.asList(beanConfig.getGroups()).contains(beanConfig.getCurrentGroup())) {
                            continue;
                        }
                        // 2. 获取前后缀和分隔符
                        BeanLogResultBean resultBean = new BeanLogResultBean();
                        String beanPrefix = beanConfig.getBeanPrefix();
                        String beanSuffix = beanConfig.getBeanSuffix();
                        String separator = beanConfig.getSeparator();
                        resultBean.setBeanClass(parseBean.getBeanType());
                        // 3. 拼接正文内容
                        List<BeanLogResultField> resultFieldList = new ArrayList<>();
                        for (BeanLogParseField parseField : parseBean.getFields()) {
                            // 1) 检查权限组，如果权限组不符合，跳过
                            BeanLogConfig fieldConfig = parseField.getFinalConfig();
                            if (fieldConfig.getGroups() != null && fieldConfig.getCurrentGroup() != null &&
                                    !Arrays.asList(fieldConfig.getGroups()).contains(fieldConfig.getCurrentGroup())) {
                                continue;
                            }
                            // 2) 必要的字段先赋值
                            String name = (fieldConfig.getName() == null) ? parseField.getFieldName() : fieldConfig.getName();
                            BeanLogResultField resultField = new BeanLogResultField();
                            resultField.setBeforeObj(parseField.getBeforeObj());
                            resultField.setAfterObj(parseField.getAfterObj());
                            resultField.setName(name);
                            resultField.setIsInsideBean(parseField.getIsInsideBean());
                            resultField.setType(parseField.getType());
                            // 3) 根据是否为insideBean确定转换逻辑
                            if (resultField.getIsInsideBean()) {
                                // 如果是insideBean
                                BeanLogResult subInsideBeanResult = this.generate(parseField.getInsideBean());
                                // 检查是否空的时候忽略
                                if(fieldConfig.getConcatWhenSubLogIsBlank() || !isBlank(subInsideBeanResult.getText())) {
                                    resultField.setInsideBeanList(subInsideBeanResult.getResultBeanList());
                                    resultField.setText(subInsideBeanResult.getText());
                                    resultFieldList.add(resultField);
                                }
                            } else {
                                // [1] 确定changeType
                                BeanLogChangeType changeType;
                                Object beforeObj = parseField.getBeforeObj();
                                Object afterObj = parseField.getAfterObj();
                                Object beforeDealObj, afterDealObj;
                                {
                                    beforeDealObj = beforeObj;
                                    afterDealObj = afterObj;
                                    // 1-1] 空串转null
                                    if (fieldConfig.getTreatBlankStringAsNull() && CharSequence.class.isAssignableFrom(resultField.getType())) {
                                        if (beforeDealObj != null && isBlank((CharSequence) beforeDealObj)) {
                                            beforeDealObj = null;
                                        }
                                        if (afterDealObj != null && isBlank((CharSequence) afterDealObj)) {
                                            afterDealObj = null;
                                        }
                                    }
                                    // [1-2] 比较对象是否相等
                                    changeType = fieldConfig.getComparable().compare(beforeDealObj, afterDealObj, parseField.getType());
                                }
                                // [2] 如果changeType无需记录日志，跳过
                                if (!Arrays.asList(fieldConfig.getEffectiveType()).contains(changeType)) {
                                    continue;
                                }
                                resultField.setChangeType(changeType);
                                // [3] 推算出before和after
                                String before = objToStr(beforeDealObj, parseField, fieldConfig);
                                String after = objToStr(afterDealObj, parseField, fieldConfig);
                                resultField.setBefore(before);
                                resultField.setAfter(after);
                                // [4] 定义Param
                                BeanLogParam logParam = new BeanLogParam();
                                logParam.setName(name);
                                logParam.setLogExp(fieldConfig.getLogExp());
                                logParam.setBefore(before);
                                logParam.setAfter(after);
                                logParam.setBeforeObj(beforeObj);
                                logParam.setAfterObj(afterObj);
                                logParam.setBeanType(parseBean.getBeanType());
                                logParam.setType(parseField.getType());
                                logParam.setFieldName(parseField.getFieldName());
                                logParam.setChangeType(changeType);
                                logParam.setPrefix(fieldConfig.getPrefix());
                                logParam.setSuffix(fieldConfig.getSuffix());
                                logParam.setP(new HashMap<>(fieldConfig.getParam()));
                                logParam.setConfig(fieldConfig);
                                logParam.setParseField(parseField);
                                logParam.setCreateLog(true);
                                // [5] 按顺序执行Editor
                                for(BeanLogParamEditor editor : fieldConfig.getParamEditors()) {
                                    editor.edit(logParam);
                                }
                                if(!logParam.isCreateLog()) {
                                    continue;
                                }
                                // [6] 解析log
                                String text = SpElUtil.format(logParam.getLogExp(), opLogParamFields, logParam);
                                // [7] 写入返回值中
                                resultField.setText(text);
                                resultField.setName(logParam.getName());
                                resultField.setChangeType(logParam.getChangeType());
                                resultField.setBeforeObj(logParam.getBeforeObj());
                                resultField.setAfterObj(logParam.getAfterObj());
                                resultField.setBefore(logParam.getBefore());
                                resultField.setAfter(logParam.getAfter());
                                resultField.setType(logParam.getType());
                                // [8] 添加赋值
                                resultFieldList.add(resultField);
                            }
                        }
                        resultBean.setFieldList(resultFieldList);
                        // 4. 如果Bean需要SpEl表达式填充，完成这步操作
                        BeanLogParam beanLogParam = new BeanLogParam();
                        beanLogParam.setBeanType(parseBean.getBeanType());
                        beanLogParam.setP(new HashMap<>(beanConfig.getParam()));
                        beanLogParam.setConfig(beanConfig);
                        if(beanConfig.getBeanPrefixEnableSpEl()) {
                            beanPrefix = SpElUtil.format(beanPrefix, opLogParamFields, beanLogParam);
                        }
                        if(beanConfig.getBeanSuffixEnableSpEl()) {
                            beanSuffix = SpElUtil.format(beanSuffix, opLogParamFields, beanLogParam);
                        }
                        if(beanConfig.getSeparatorEnableSpEl()) {
                            separator = SpElUtil.format(separator, opLogParamFields, beanLogParam);
                        }
                        // 5. 补充Bean的正文
                        String beanBodyText = resultFieldList.stream().map(BeanLogResultField::getText).collect(Collectors.joining(separator));
                        String text =
                                ((!isBlank(beanBodyText) || beanConfig.getCreateBeanPrefixWhenLogIsEmpty())? beanPrefix:"") +
                                beanBodyText +
                                ((!isBlank(beanBodyText) || beanConfig.getCreateBeanSuffixWhenLogIsEmpty())? beanSuffix:"");
                        // 6. 组装resultBean
                        resultBean.setText(text);
                        resultBean.setPrefix(beanPrefix);
                        resultBean.setSuffix(beanSuffix);
                        resultBean.setSeparator(separator);
                        // 7. 添加到resultBeanList
                        resultBeanList.add(resultBean);
                    }
                    // 组装结果
                    result.setText(resultBeanList.stream().map(BeanLogResultBean::getText).collect(Collectors.joining("")));
                    result.setResultBeanList(resultBeanList);
                    // 返回
                    return result;
                }

                private String objToStr(Object obj, BeanLogParseField parseField, BeanLogConfig fieldConfig) {
                    // 1. null，返回空值占位符
                    if(obj == null) {
                        return fieldConfig.getLogForNull();
                    }
                    // 2. 转换
                    String str = BeanLogConvertUtil.convertObj(fieldConfig.getConverterMap(), obj, parseField);
                    // 3. 加上前后缀并返回
                    return fieldConfig.getPrefix().concat(str).concat(fieldConfig.getSuffix());
                }
            };
        }
    }

    private static void staticInitDefaultConfig() {
        defaultConfig = new BeanLogConfig();
        defaultConfig.setName(null);
        defaultConfig.setLogExp("#{#name}: #{#before}->#{#after}");
        defaultConfig.setEffectiveType(new BeanLogChangeType[]{BeanLogChangeType.UPDATE, BeanLogChangeType.INSERT, BeanLogChangeType.DELETE});
        defaultConfig.setPrefix("");
        defaultConfig.setSuffix("");
        defaultConfig.setLogForNull("null");
        defaultConfig.setTreatBlankStringAsNull(false);
        defaultConfig.setParam(new HashMap<>());
        defaultConfig.setBeanPrefix("");
        defaultConfig.setBeanPrefixEnableSpEl(false);
        defaultConfig.setBeanSuffix("");
        defaultConfig.setBeanSuffixEnableSpEl(false);
        defaultConfig.setCreateBeanPrefixWhenLogIsEmpty(false);
        defaultConfig.setCreateBeanSuffixWhenLogIsEmpty(false);
        defaultConfig.setConcatWhenSubLogIsBlank(false);
        defaultConfig.setSeparator(", ");
        defaultConfig.setSeparatorEnableSpEl(false);
        defaultConfig.setParamEditors(new ArrayList<>());
        defaultConfig.setCurrentGroup(null);
        defaultConfig.setGroups(new String[]{});
        defaultConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        defaultConfig.setDecimalFormat(null);
        defaultConfig.setCollectionPrefix("[");
        defaultConfig.setCollectionSuffix("]");
        defaultConfig.setCollectionSeparator(",");
        defaultConfig.setCollectionLogForNull("null");

        // 定义默认的ConverterMap
        Map<Class<?>, BeanLogStringConverter> converterMap = new HashMap<>();
        // 日期类型的converter
        BeanLogStringConverter dateConverter = new DateConverterImpl();
        converterMap.put(Date.class, dateConverter);
        converterMap.put(Calendar.class, dateConverter);
        converterMap.put(LocalDate.class, dateConverter);
        converterMap.put(LocalDateTime.class, dateConverter);
        converterMap.put(ZonedDateTime.class, dateConverter);
        converterMap.put(Instant.class, dateConverter);
        // 数字类型的converter
        BeanLogStringConverter decimalConverter = new DecimalConverterImpl();
        converterMap.put(Number.class, decimalConverter);
        converterMap.put(byte.class, decimalConverter);
        converterMap.put(short.class, decimalConverter);
        converterMap.put(int.class, decimalConverter);
        converterMap.put(long.class, decimalConverter);
        converterMap.put(float.class, decimalConverter);
        converterMap.put(double.class, decimalConverter);
        // Collection类型的Converter
        BeanLogStringConverter collectionConverter = new CollectionConverterImpl();
        converterMap.put(Collection.class, collectionConverter);
        // 定义converterMap
        defaultConfig.setConverterMap(converterMap);

        // 定义comparable
        BeanLogComparable comparable = new DefaultComparableImpl();
        defaultConfig.setComparable(comparable);
    }

    @Override
    public BeanLogConfig getBuilderConfig() {
        return builderConfig;
    }

    @Override
    public void setOpLogBuilderConfig(BeanLogConfig builderConfig) throws NullPointerException {
        Objects.requireNonNull(builderConfig);
        this.builderConfig = builderConfig;
    }

    public BeanLogParser getLogParser() {
        return logParser;
    }

    public void setLogParser(BeanLogParser logParser) {
        this.logParser = logParser;
    }

    public BeanLogGenerator getLogGenerator() {
        return logGenerator;
    }

    public void setLogGenerator(BeanLogGenerator logGenerator) {
        this.logGenerator = logGenerator;
    }

    @Override
    public BeanLogObjectLoader getObjectLoader() {
        return this.logConfigEditor.getObjectLoader();
    }

    @Override
    public void setObjectLoader(BeanLogObjectLoader objectLoader) {
        init();
        this.logConfigEditor.setObjectLoader(objectLoader);
    }

    /**
     * 判断是否为空串<br>
     * 代码来源：HuTool，做了优化
     * @param str 要判空的字符串
     * @return 是否为空串
     */
    private static boolean isBlank(CharSequence str) {
        int length;
        if (str != null && (length = str.length()) != 0) {
            for(int i = 0; i < length; ++i) {
                if (!isBlankChar(str.charAt(i))) {
                    return false;
                }
            }

        }
        return true;
    }
    private static boolean isBlankChar(int c) {
        return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == 65279 || c == 8234 || c == 0 || c == 12644 || c == 10240;
    }
}
