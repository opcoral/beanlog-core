package io.github.opcoral.oplog.impl.stringConverter;

import io.github.opcoral.oplog.core.OpLogStringConverter;
import io.github.opcoral.oplog.entity.parse.OpLogParseField;

import java.text.DecimalFormat;

/**
 * 数字类型的Converter<br>
 * <br>
 *
 * @author 关卓华 Guan Zhuohua
 * @since 2023-5-16 19:35
 */
public class DecimalConverterImpl implements OpLogStringConverter {
    @Override
    public String convert(Object obj, Class<?> matchClass, OpLogParseField parseField) {
        String decimalFormatStr = parseField.getFinalConfig().getDecimalFormat();
        String result = obj.toString();
        if(decimalFormatStr != null) {
            DecimalFormat df = new DecimalFormat(decimalFormatStr);
            result = df.format(obj);
        }
        return result;
    }
}
