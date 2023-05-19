package io.github.opcoral.oplog.impl.comparable;

import io.github.opcoral.oplog.core.OpLogComparable;
import io.github.opcoral.oplog.enums.OpLogChangeType;
import io.github.opcoral.oplog.util.OpLogConfigEditor;

/**
 * config的空白比较器
 *
 * @author 关卓华 Guan Zhuohua
 * @since 2023-5-16 18:29
 */
public class NullComparableImpl implements OpLogComparable, OpLogConfigEditor.INullableClassInterface {
    @Override
    public OpLogChangeType compare(Object before, Object after, Class<?> fieldClass) {
        throw new UnsupportedOperationException();
    }
}