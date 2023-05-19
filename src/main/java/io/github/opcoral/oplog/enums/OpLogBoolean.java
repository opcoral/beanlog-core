package io.github.opcoral.oplog.enums;

/**
 * OpLog注解使用的Boolean类型<br>
 * OpLog注解使用的Boolean类型<br>
 *
 * @author 关卓华 Guan Zhuohua
 * @since 2023-5-15 14:50
 */
public enum OpLogBoolean {

    DEFAULT(null),
    TRUE(true),
    FALSE(false),
    ;

    OpLogBoolean(Boolean truthBoolean) {
        this.truthBoolean = truthBoolean;
    }

    private final Boolean truthBoolean;

    public Boolean getTruthBoolean() {
        return truthBoolean;
    }
}
