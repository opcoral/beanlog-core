package io.github.opcoral.beanlog.test.entity;

import io.github.opcoral.beanlog.annotation.BeanLogField;
import lombok.Data;

/**
 * 内部Pojo<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-15 10:05
 */
@Data
public class TestInsidePojo {

    @BeanLogField
    private String simpleString;

    @BeanLogField
    private String noChangeString;
}
