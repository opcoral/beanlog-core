package io.github.opcoral.beanlog.impl.comparable;

import io.github.opcoral.beanlog.core.BeanLogComparable;
import io.github.opcoral.beanlog.enums.BeanLogChangeType;

import java.util.Objects;

/**
 * 默认的比较器<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-16 18:33
 */
public class DefaultComparableImpl implements BeanLogComparable {
    @Override
    public BeanLogChangeType compare(Object before, Object after, Class<?> fieldClass) {

        if(before == null && after == null) {
            return BeanLogChangeType.NULL;
        }
        if(Objects.equals(before, after)) {
            return BeanLogChangeType.EQUAL;
        }
        if(before == null) {
            return BeanLogChangeType.INSERT;
        }
        if(after == null) {
            return BeanLogChangeType.DELETE;
        }
        return BeanLogChangeType.UPDATE;
    }
}
