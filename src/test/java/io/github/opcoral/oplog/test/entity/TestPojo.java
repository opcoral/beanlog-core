package io.github.opcoral.oplog.test.entity;

import io.github.opcoral.oplog.annotation.OpLogField;
import io.github.opcoral.oplog.annotation.OpLogInside;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TestPojo {

    @OpLogField
    private String simpleString;

    @OpLogField
    private String noChangeString;

    @OpLogField
    private String nullString;

    @OpLogField
    private String insertString;

    @OpLogField
    private String removeString;

    @OpLogField
    private String blankString;

    @OpLogField
    private String noLimitString;

    @OpLogField
    @OpLogField
    private String moreOperationString;

    private String ignoreString;

    @OpLogField
    private Date date;

    @OpLogField
    private Integer simpleInteger;

    @OpLogField
    private Integer simpleBasicEnum;

    @OpLogField
    private Integer notFoundEnum;

    @OpLogField
    private BigDecimal bigDecimal;

    @OpLogField
    private BigDecimal bigDecimalScale;

    @OpLogField
    private Float aFloat;

    @OpLogField
    private Double aDouble;

    @OpLogInside
    private TestInsidePojo insidePojo;
}