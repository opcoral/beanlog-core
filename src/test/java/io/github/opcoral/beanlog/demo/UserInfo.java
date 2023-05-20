package io.github.opcoral.beanlog.demo;

import io.github.opcoral.beanlog.annotation.BeanLogField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    @BeanLogField(name = "昵称")
    private String nickName;
    @BeanLogField(name = "年龄")
    private Integer age;
    @BeanLogField(name = "电话")
    private String phone;
}