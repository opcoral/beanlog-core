package io.github.opcoral.oplog.impl.comparable;

import io.github.opcoral.oplog.core.OpLogComparable;
import io.github.opcoral.oplog.enums.OpLogChangeType;

import java.util.Objects;

/**
 * 默认的比较器<br>
 * <br>
 *
 * @author 关卓华 Guan Zhuohua
 * @since 2023-5-16 18:33
 */
public class DefaultComparableImpl implements OpLogComparable {
    @Override
    public OpLogChangeType compare(Object before, Object after, Class<?> fieldClass) {

        if(before == null && after == null) {
            return OpLogChangeType.NULL;
        }
        if(Objects.equals(before, after)) {
            return OpLogChangeType.EQUAL;
        }
        if(before == null) {
            return OpLogChangeType.INSERT;
        }
        if(after == null) {
            return OpLogChangeType.DELETE;
        }
        return OpLogChangeType.UPDATE;
    }
}
