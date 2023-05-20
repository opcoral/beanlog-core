package io.github.opcoral.beanlog.impl.stringConverter;

import io.github.opcoral.beanlog.core.BeanLogStringConverter;
import io.github.opcoral.beanlog.entity.parse.BeanLogParseField;

import java.text.DecimalFormat;

/**
 * 数字类型的Converter<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-16 19:35
 */
public class DecimalConverterImpl implements BeanLogStringConverter {
    @Override
    public String convert(Object obj, Class<?> matchClass, BeanLogParseField parseField) {
        String decimalFormatStr = parseField.getFinalConfig().getDecimalFormat();
        String result = obj.toString();
        if(decimalFormatStr != null) {
            DecimalFormat df = new DecimalFormat(decimalFormatStr);
            result = df.format(obj);
        }
        return result;
    }
}
