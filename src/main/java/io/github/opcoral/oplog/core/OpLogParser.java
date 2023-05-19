package io.github.opcoral.oplog.core;

import io.github.opcoral.oplog.entity.config.OpLogConfig;
import io.github.opcoral.oplog.entity.parse.OpLogParseBean;

import java.util.List;

/**
 * OpLog类解析器<br>
 *
 * @author GuanZH
 * @since 2023/5/14 16:43
 */
@FunctionalInterface
public interface OpLogParser {

    List<OpLogParseBean> parse(Object before, Object after, Class<?> beanClass, OpLogConfig beanConfig, OpLogConfig parentConfig);
}
