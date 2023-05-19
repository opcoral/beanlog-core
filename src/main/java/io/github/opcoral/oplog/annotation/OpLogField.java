package io.github.opcoral.oplog.annotation;

import io.github.opcoral.oplog.core.OpLogComparable;
import io.github.opcoral.oplog.core.OpLogParamEditor;
import io.github.opcoral.oplog.enums.OpLogBoolean;
import io.github.opcoral.oplog.enums.OpLogChangeType;
import io.github.opcoral.oplog.impl.comparable.NullComparableImpl;

import java.lang.annotation.*;

/**
 * 标识是操作日志属性的注解<br>
 *
 * @author GuanZH
 * @since 2023/5/14 17:01
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(OpLogField.List.class)
public @interface OpLogField {

    String name() default "";

    boolean nameIgnoreBlank() default true;

    String logExp() default "";

    boolean logExpIgnoreBlank() default true;

    OpLogChangeType[] effectiveType() default {};

    boolean effectiveTypeIgnoreBlank() default true;

    String prefix() default "";

    boolean prefixIgnoreBlank() default true;

    String suffix() default "";

    boolean suffixIgnoreBlank() default true;

    String logForNull() default "";

    boolean logForNullIgnoreBlank() default true;

    OpLogBoolean treatBlankStringAsNull() default OpLogBoolean.DEFAULT;

    Class<? extends OpLogParamEditor>[] paramEditors() default {};

    Class<? extends OpLogComparable> comparable() default NullComparableImpl.class;

    String[] groups() default {};

    boolean groupsIgnoreBlank() default true;

    String dateFormat() default "";

    boolean dateFormatIgnoreBlank() default true;

    String decimalFormat() default "";

    boolean decimalFormatIgnoreBlank() default true;

    String collectionPrefix() default "";

    boolean collectionPrefixIgnoreBlank() default true;

    String collectionSuffix() default "";

    boolean collectionSuffixIgnoreBlank() default true;

    String collectionSeparator() default "";

    boolean collectionSeparatorIgnoreBlank() default true;

    String collectionLogForNull() default "";

    boolean collectionLogForNullIgnoreBlank() default true;

    /**
     * 可重复注解的添加
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        OpLogField[] value();
    }
}
