package io.github.opcoral.beanlog.impl.stringConverter;

import io.github.opcoral.beanlog.core.BeanLogStringConverter;
import io.github.opcoral.beanlog.entity.config.BeanLogConfig;
import io.github.opcoral.beanlog.entity.parse.BeanLogParseField;
import io.github.opcoral.beanlog.util.BeanLogConvertUtil;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 集合的Converter<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-16 19:36
 */
public class CollectionConverterImpl implements BeanLogStringConverter {
    @Override
    public String convert(Object obj, Class<?> matchClass, BeanLogParseField parseField) {
        BeanLogConfig config = parseField.getFinalConfig();
        if(obj instanceof Collection) {
            Collection<?> collection = (Collection<?>) obj;
            String body = collection.stream().map(c -> {
                if(c == null) {
                    // 列表中的空值，返回空
                    return config.getCollectionLogForNull();
                } else {
                    return BeanLogConvertUtil.convertObj(config.getConverterMap(), c, parseField);
                }
            }).collect(Collectors.joining(config.getCollectionSeparator()));
            return config.getCollectionPrefix() + body + config.getCollectionSuffix();
        } else {
            return obj.toString();
        }
    }
}
