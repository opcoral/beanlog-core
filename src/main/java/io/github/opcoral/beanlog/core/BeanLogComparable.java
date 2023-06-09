package io.github.opcoral.beanlog.core;

import io.github.opcoral.beanlog.enums.BeanLogChangeType;

/**
 * OpLog比较器<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-16 10:49
 */
@FunctionalInterface
public interface BeanLogComparable {

    /**
     * 比较两个对象的相等关系
     * @param before 改变前的对象
     * @param after 改变后的对象
     * @param fieldClass 属性的类型
     * @return 比较关系
     */
    BeanLogChangeType compare(Object before, Object after, Class<?> fieldClass);
}
