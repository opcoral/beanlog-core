package io.github.opcoral.beanlog.core;

import io.github.opcoral.beanlog.entity.result.BeanLogResult;
import io.github.opcoral.beanlog.entity.parse.BeanLogParseBean;

import java.util.List;

/**
 * OpLog生成器<br>
 *
 * @author GuanZH
 * @since 2023/5/14 16:45
 */
@FunctionalInterface
public interface BeanLogGenerator {

    BeanLogResult generate(List<BeanLogParseBean> parseResultList);
}
