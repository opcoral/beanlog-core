package io.github.opcoral.beanlog.test.entity;

import io.github.opcoral.beanlog.annotation.BeanLogField;
import io.github.opcoral.beanlog.annotation.BeanLogInside;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TestPojo {

    @BeanLogField
    private String simpleString;

    @BeanLogField
    private String noChangeString;

    @BeanLogField
    private String nullString;

    @BeanLogField
    private String insertString;

    @BeanLogField
    private String removeString;

    @BeanLogField
    private String blankString;

    @BeanLogField
    private String noLimitString;

    @BeanLogField
    @BeanLogField
    private String moreOperationString;

    private String ignoreString;

    @BeanLogField
    private Date date;

    @BeanLogField
    private Integer simpleInteger;

    @BeanLogField
    private Integer simpleBasicEnum;

    @BeanLogField
    private Integer notFoundEnum;

    @BeanLogField
    private BigDecimal bigDecimal;

    @BeanLogField
    private BigDecimal bigDecimalScale;

    @BeanLogField
    private Float aFloat;

    @BeanLogField
    private Double aDouble;

    @BeanLogInside
    private TestInsidePojo insidePojo;
}