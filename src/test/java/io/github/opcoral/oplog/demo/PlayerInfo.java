package io.github.opcoral.oplog.demo;

import io.github.opcoral.oplog.annotation.OpLogBean;
import io.github.opcoral.oplog.annotation.OpLogField;
import lombok.Data;

/**
 * 玩家信息<br>
 * <br>
 *
 * @author 关卓华 Guan Zhuohua
 * @since 2023-5-18 14:44
 */
@Data
@OpLogBean(beanPrefix = "用户操作日志：[", beanSuffix = "]", logExp = "#{#name}由#{#before}改为#{#after}", separator = "，")
public class PlayerInfo {

    @OpLogField(name = "昵称")
    private String nickName;

    @OpLogField(name = "杀死的史莱姆数量", prefix = "约", suffix = "只")
    private Integer killSlimeNum;

    @OpLogField(name = "改动备注", logExp = "#{#name}：#{#after}")
    private String remark;
}
