package io.github.opcoral.beanlog.util;

import io.github.opcoral.beanlog.core.BeanLogStringConverter;
import io.github.opcoral.beanlog.entity.parse.BeanLogParseField;
import io.github.opcoral.beanlog.entity.util.BeanLogStringConverterInfo;

import java.util.Map;

/**
 * OpLog的转换工具<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-17 11:13
 */
public class BeanLogConvertUtil {

    /**
     * 加载Converter
     * @param converterMap converter的映射关系
     * @param type 要获取converter的类
     * @return 获取到的converter信息，如果无匹配，会获取调用toString的converter。需要保证入参不为null
     */
    private static BeanLogStringConverterInfo loadConverter(Map<Class<?>, BeanLogStringConverter> converterMap, Class<?> type) {
        Class<?> matchClass = null;
        BeanLogStringConverter converter = (obj, subMatchClass, parseField) -> obj.toString();
        for(Map.Entry<Class<?>, BeanLogStringConverter> entry : converterMap.entrySet()) {
            if(entry.getKey().isAssignableFrom(type) && entry.getValue() != null) {
                converter = entry.getValue();
                matchClass = entry.getKey();
                break;
            }
        }
        BeanLogStringConverterInfo info = new BeanLogStringConverterInfo();
        info.setConverter(converter);
        info.setMatchClass(matchClass);
        return info;
    }

    /**
     * 转换Obj
     * @param converterMap 转换的Map
     * @param obj 要转换的对象
     * @param parseField 转换的配置
     * @return 转换后的String
     */
    public static String convertObj(Map<Class<?>, BeanLogStringConverter> converterMap, Object obj, BeanLogParseField parseField) {
        BeanLogStringConverterInfo info = BeanLogConvertUtil.loadConverter(converterMap, obj.getClass());
        return info.getConverter().convert(obj, info.getMatchClass(), parseField);
    }
}
