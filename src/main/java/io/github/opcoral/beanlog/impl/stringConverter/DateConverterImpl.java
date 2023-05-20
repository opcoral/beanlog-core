package io.github.opcoral.beanlog.impl.stringConverter;

import io.github.opcoral.beanlog.core.BeanLogStringConverter;
import io.github.opcoral.beanlog.entity.parse.BeanLogParseField;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Date类型的Converter<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-16 19:34
 */
public class DateConverterImpl implements BeanLogStringConverter {
    @Override
    public String convert(Object obj, Class<?> matchClass, BeanLogParseField parseField) {
        String dateFormatStr = parseField.getFinalConfig().getDateFormat();
        DateFormat df = new SimpleDateFormat(dateFormatStr);
        return df.format(obj);
    }
}
