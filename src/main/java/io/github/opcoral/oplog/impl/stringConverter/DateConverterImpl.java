package io.github.opcoral.oplog.impl.stringConverter;

import io.github.opcoral.oplog.core.OpLogStringConverter;
import io.github.opcoral.oplog.entity.parse.OpLogParseField;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Date类型的Converter<br>
 * <br>
 *
 * @author 关卓华 Guan Zhuohua
 * @since 2023-5-16 19:34
 */
public class DateConverterImpl implements OpLogStringConverter {
    @Override
    public String convert(Object obj, Class<?> matchClass, OpLogParseField parseField) {
        String dateFormatStr = parseField.getFinalConfig().getDateFormat();
        DateFormat df = new SimpleDateFormat(dateFormatStr);
        return df.format(obj);
    }
}
