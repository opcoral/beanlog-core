package io.github.opcoral.beanlog.demo;

import io.github.opcoral.beanlog.annotation.BeanLogBean;
import io.github.opcoral.beanlog.annotation.BeanLogField;
import lombok.Data;

/**
 * 玩家信息<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-18 14:44
 */
@Data
@BeanLogBean(beanPrefix = "用户操作日志：[", beanSuffix = "]", logExp = "#{#name}由#{#before}改为#{#after}", separator = "，")
public class PlayerInfo {

    @BeanLogField(name = "昵称")
    private String nickName;

    @BeanLogField(name = "杀死的史莱姆数量", prefix = "约", suffix = "只")
    private Integer killSlimeNum;

    @BeanLogField(name = "改动备注", logExp = "#{#name}：#{#after}")
    private String remark;
}
