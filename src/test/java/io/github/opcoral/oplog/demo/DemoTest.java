package io.github.opcoral.oplog.demo;

import io.github.opcoral.oplog.entity.result.OpLogResult;
import io.github.opcoral.oplog.impl.builder.OpLogBuilderImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 示例用法测试<br>
 * 如需使用打上Test注解即可<br>
 *
 * @author GuanZH
 * @since 2023-5-18 14:37
 */
public class DemoTest {

    /**
     * 快速使用测试
     */
    // @Test
    public void quickUseTest() {
        UserInfo before = new UserInfo("小王", 8, "18888888888");
        UserInfo after = new UserInfo("王总", 8, "19999999999");

        OpLogResult logResult = new OpLogBuilderImpl().beanBuild(before, after, UserInfo.class);
        System.out.println(logResult.getText());
    }

    // @Test
    public void logStructTest() {
        // 修改前
        PlayerInfo before = new PlayerInfo();
        before.setNickName("小王");
        before.setKillSlimeNum(0);
        before.setRemark("改之前的备注");
        // 修改后
        PlayerInfo after = new PlayerInfo();
        after.setNickName("老王");
        after.setKillSlimeNum(300);
        after.setRemark("不知不觉就升到了满级");
        // 生成更新日志
        OpLogResult result = new OpLogBuilderImpl().beanBuild(before, after, PlayerInfo.class);
        System.out.println(result.getText());
    }
}
