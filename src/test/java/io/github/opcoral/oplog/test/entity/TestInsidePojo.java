package io.github.opcoral.oplog.test.entity;

import io.github.opcoral.oplog.annotation.OpLogField;
import lombok.Data;

/**
 * 内部Pojo<br>
 * <br>
 *
 * @author 关卓华 Guan Zhuohua
 * @since 2023-5-15 10:05
 */
@Data
public class TestInsidePojo {

    @OpLogField
    private String simpleString;

    @OpLogField
    private String noChangeString;
}
