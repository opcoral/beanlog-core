package io.github.opcoral.oplog.impl.stringConverter;

import io.github.opcoral.oplog.core.OpLogStringConverter;
import io.github.opcoral.oplog.entity.config.OpLogConfig;
import io.github.opcoral.oplog.entity.parse.OpLogParseField;
import io.github.opcoral.oplog.util.OpLogConvertUtil;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 集合的Converter<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-16 19:36
 */
public class CollectionConverterImpl implements OpLogStringConverter {
    @Override
    public String convert(Object obj, Class<?> matchClass, OpLogParseField parseField) {
        OpLogConfig config = parseField.getFinalConfig();
        if(obj instanceof Collection) {
            Collection<?> collection = (Collection<?>) obj;
            String body = collection.stream().map(c -> {
                if(c == null) {
                    // 列表中的空值，返回空
                    return config.getCollectionLogForNull();
                } else {
                    return OpLogConvertUtil.convertObj(config.getConverterMap(), c, parseField);
                }
            }).collect(Collectors.joining(config.getCollectionSeparator()));
            return config.getCollectionPrefix() + body + config.getCollectionSuffix();
        } else {
            return obj.toString();
        }
    }
}
