package io.github.opcoral.beanlog.core;

import io.github.opcoral.beanlog.entity.config.BeanLogConfig;
import io.github.opcoral.beanlog.entity.parse.BeanLogParseBean;

import java.util.List;

/**
 * OpLog类解析器<br>
 *
 * @author GuanZH
 * @since 2023/5/14 16:43
 */
@FunctionalInterface
public interface BeanLogParser {

    List<BeanLogParseBean> parse(Object before, Object after, Class<?> beanClass, BeanLogConfig beanConfig, BeanLogConfig parentConfig);
}
