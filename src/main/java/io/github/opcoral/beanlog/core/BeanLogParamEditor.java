package io.github.opcoral.beanlog.core;

import io.github.opcoral.beanlog.entity.param.BeanLogParam;

/**
 * OpLogParam编辑器<br>
 * 在每次生成日志之前调用<br>
 * 可以编辑param，比如自定义参数添加运行时时间戳等
 *
 * @author GuanZH
 * @since 2023-5-15 14:24
 */
@FunctionalInterface
public interface BeanLogParamEditor {

    void edit(BeanLogParam beanLogParam);
}
