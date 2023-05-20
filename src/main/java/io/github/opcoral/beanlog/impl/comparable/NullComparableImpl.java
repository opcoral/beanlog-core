package io.github.opcoral.beanlog.impl.comparable;

import io.github.opcoral.beanlog.core.BeanLogComparable;
import io.github.opcoral.beanlog.enums.BeanLogChangeType;
import io.github.opcoral.beanlog.util.BeanLogConfigEditor;

/**
 * config的空白比较器
 *
 * @author GuanZH
 * @since 2023-5-16 18:29
 */
public class NullComparableImpl implements BeanLogComparable, BeanLogConfigEditor.INullableClassInterface {
    @Override
    public BeanLogChangeType compare(Object before, Object after, Class<?> fieldClass) {
        throw new UnsupportedOperationException();
    }
}