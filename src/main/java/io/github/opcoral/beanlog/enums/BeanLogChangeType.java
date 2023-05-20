package io.github.opcoral.beanlog.enums;

/**
 * 字段变化类型<br>
 * 表示日志的变化操作是NULL/相等/新增/更新/删除<br>
 *
 * @author GuanZH
 * @since 2023-5-15 10:39
 */
public enum BeanLogChangeType {

    /**
     * null to null
     */
    NULL(false),
    /**
     * A to A
     */
    EQUAL(false),
    /**
     * A to B
     */
    UPDATE(true),
    /**
     * null to A
     */
    INSERT(true),
    /**
     * A to null
     */
    DELETE(true),
    ;

    private final boolean changed;

    BeanLogChangeType(boolean changed) {
        this.changed = changed;
    }

    public boolean isChanged() {
        return changed;
    }
}
