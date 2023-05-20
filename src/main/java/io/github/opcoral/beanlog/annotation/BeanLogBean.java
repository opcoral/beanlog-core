package io.github.opcoral.beanlog.annotation;

import io.github.opcoral.beanlog.core.BeanLogComparable;
import io.github.opcoral.beanlog.core.BeanLogParamEditor;
import io.github.opcoral.beanlog.enums.BeanLogBoolean;
import io.github.opcoral.beanlog.enums.BeanLogChangeType;
import io.github.opcoral.beanlog.impl.comparable.NullComparableImpl;

import java.lang.annotation.*;

/**
 * 标识是操作日志Bean的注解<br>
 *
 * @author GuanZH
 * @since 2023/5/14 17:01
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(BeanLogBean.List.class)
public @interface BeanLogBean {

    String logExp() default "";

    boolean logExpIgnoreBlank() default true;

    BeanLogChangeType[] effectiveType() default {};

    boolean effectiveTypeIgnoreBlank() default true;

    String prefix() default "";

    boolean prefixIgnoreBlank() default true;

    String suffix() default "";

    boolean suffixIgnoreBlank() default true;

    String logForNull() default "";

    boolean logForNullIgnoreBlank() default true;

    BeanLogBoolean treatBlankStringAsNull() default BeanLogBoolean.DEFAULT;

    String beanPrefix() default "";

    boolean beanPrefixIgnoreBlank() default true;

    BeanLogBoolean beanPrefixEnableSpEl() default BeanLogBoolean.DEFAULT;

    String beanSuffix() default "";

    boolean beanSuffixIgnoreBlank() default true;

    BeanLogBoolean beanSuffixEnableSpEl() default BeanLogBoolean.DEFAULT;

    BeanLogBoolean createBeanPrefixWhenLogIsEmpty() default BeanLogBoolean.DEFAULT;

    BeanLogBoolean createBeanSuffixWhenLogIsEmpty() default BeanLogBoolean.DEFAULT;

    String separator() default "";

    boolean separatorIgnoreBlank() default true;

    BeanLogBoolean separatorEnableSpEl() default BeanLogBoolean.DEFAULT;

    Class<? extends BeanLogParamEditor>[] paramEditors() default {};

    Class<? extends BeanLogComparable> comparable() default NullComparableImpl.class;

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

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        BeanLogBean[] value();
    }
}
