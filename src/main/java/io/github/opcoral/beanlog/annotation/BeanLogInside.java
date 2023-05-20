package io.github.opcoral.beanlog.annotation;

import io.github.opcoral.beanlog.enums.BeanLogBoolean;

import java.lang.annotation.*;

/**
 * 内部bean日志<br>
 *
 * @author GuanZH
 * @since 2023/5/14 17:09
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeanLogInside {

    BeanLogBoolean concatWhenSubLogIsBlank() default BeanLogBoolean.DEFAULT;
}
