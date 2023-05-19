package io.github.opcoral.oplog.enums;

/**
 * 字段变化类型<br>
 * 表示日志的变化操作是NULL/相等/新增/更新/删除<br>
 *
 * @author 关卓华 Guan Zhuohua
 * @since 2023-5-15 10:39
 */
public enum OpLogChangeType {

    /**
     * null -> null
     */
    NULL(false),
    /**
     * A -> A
     */
    EQUAL(false),
    /**
     * A -> B
     */
    UPDATE(true),
    /**
     * null -> A
     */
    INSERT(true),
    /**
     * A -> null
     */
    DELETE(true),
    ;

    private final boolean changed;

    OpLogChangeType(boolean changed) {
        this.changed = changed;
    }

    public boolean isChanged() {
        return changed;
    }
}
