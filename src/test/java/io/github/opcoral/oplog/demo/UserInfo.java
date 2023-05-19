package io.github.opcoral.oplog.demo;

import io.github.opcoral.oplog.annotation.OpLogField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    @OpLogField(name = "昵称")
    private String nickName;
    @OpLogField(name = "年龄")
    private Integer age;
    @OpLogField(name = "电话")
    private String phone;
}