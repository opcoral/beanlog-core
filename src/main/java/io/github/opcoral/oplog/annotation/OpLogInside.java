package io.github.opcoral.oplog.annotation;

import io.github.opcoral.oplog.enums.OpLogBoolean;

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
public @interface OpLogInside {

    OpLogBoolean concatWhenSubLogIsBlank() default OpLogBoolean.DEFAULT;
}
