package io.github.opcoral.oplog.test;

import io.github.opcoral.oplog.annotation.OpLogBean;
import io.github.opcoral.oplog.annotation.OpLogField;
import io.github.opcoral.oplog.core.OpLogBuilder;
import io.github.opcoral.oplog.entity.result.OpLogResult;
import io.github.opcoral.oplog.entity.result.OpLogResultBean;
import io.github.opcoral.oplog.entity.result.OpLogResultField;
import io.github.opcoral.oplog.enums.OpLogChangeType;
import io.github.opcoral.oplog.impl.builder.OpLogBuilderImpl;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class SimpleTest {

    private final OpLogBuilder logBuilder = new OpLogBuilderImpl();

    /**
     * HelloWorld示例
     */
    @Test
    public void helloWorldTest() {

        // 1. 定义Bean对象，要比较是否变化的字段标上@OpLogField注解
        class HelloWorldBean {
            @OpLogField
            String label;
        }

        // 2. 新建LogBuilder对象，如果没有init()，beanBuild的时候会自动init()
        OpLogBuilder logBuilder = new OpLogBuilderImpl();
        logBuilder.init();

        // 3. 准备变化前/变化后的实体
        HelloWorldBean before = new HelloWorldBean();
        before.label = "Hello";
        HelloWorldBean after = new HelloWorldBean();
        after.label = "World!";

        // 4. 调用并取得日志
        String result = logBuilder.beanBuild(before, after, HelloWorldBean.class).getText();
        // System.out.println(result);

        // 5. 单元测试校验
        String desireResult = "label: Hello->World!";
        Assertions.assertEquals(desireResult, result);
    }

    @Data
    @OpLogBean(beanPrefix = "更新日志：", beanSuffix = "。", separator = "，")
    static class SimpleBean {

        @OpLogField
        private Long uid;

        @OpLogField(name = "昵称")
        private String nickName;

        @OpLogField(name = "生日", dateFormat = "yyyy-MM-dd")
        private Date birthday;

        @OpLogField(name = "次数", prefix = "第", suffix = "次")
        private Integer times;

        @OpLogField(name = "等级")
        private Long level;

        @OpLogField(name = "武器")
        private List<String> weapons;

        @OpLogField(name = "装备")
        private List<String> equipments;

        @OpLogField(name = "备注")
        private String remark;
    }

    /**
     * 简单使用测试
     */
    @Test
    public void simpleUsageTest() throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // before
        SimpleBean before = new SimpleBean();
        before.setUid(10001L);
        before.setNickName("张三");
        before.setBirthday(df.parse("2022-01-01 00:12:34"));
        before.setTimes(1);
        before.setLevel(50L);
        before.setWeapons(Arrays.asList("镰刀", "斧头", null));
        before.setEquipments(Arrays.asList("疼甲", "人亡盾"));
        before.setRemark(null);
        // after
        SimpleBean after = new SimpleBean();
        after.setUid(10002L);
        after.setNickName("李四");
        after.setBirthday(df.parse("2022-01-02 00:15:34"));
        after.setTimes(2);
        after.setLevel(50L);
        after.setWeapons(Arrays.asList("斧头", "锤子", "物理学圣剑"));
        after.setEquipments(Arrays.asList("疼甲", "人亡盾"));
        after.setRemark("加个备注");
        // log
        OpLogResult result = logBuilder.beanBuild(before, after, SimpleBean.class);

        // 比较
        String desireResult = "更新日志：uid: 10001->10002，昵称: 张三->李四，生日: 2022-01-01->2022-01-02，次数: 第1次->第2次，武器: [镰刀,斧头,null]->[斧头,锤子,物理学圣剑]，备注: null->加个备注。";
        String actualResult = result.getText();
        Assertions.assertEquals(desireResult, actualResult);
    }

    @Test
    public void resultFieldCallTest() {
        // before
        SimpleBean before = new SimpleBean();
        before.setUid(null);
        before.setNickName("张三");
        before.setTimes(1);
        // after
        SimpleBean after = new SimpleBean();
        after.setUid(10002L);
        after.setNickName("李四");
        after.setTimes(null);
        // log
        OpLogResult result = logBuilder.beanBuild(before, after, SimpleBean.class);

        // resultBeanList长度检查
        Assertions.assertEquals(result.getResultBeanList().size(), 1);

        // bean检查
        OpLogResultBean logResultBean = result.getResultBeanList().get(0);
        Assertions.assertEquals(logResultBean.getBeanClass(), SimpleBean.class);
        Assertions.assertEquals(logResultBean.getPrefix(), "更新日志：");
        Assertions.assertEquals(logResultBean.getSuffix(), "。");
        Assertions.assertEquals(logResultBean.getSeparator(), "，");
        Assertions.assertEquals(logResultBean.getFieldList().size(), 3);

        // field检查
        // field[0] - INSERT
        OpLogResultField field0 = logResultBean.getFieldList().get(0);
        Assertions.assertEquals(field0.getName(), "uid");
        Assertions.assertEquals(field0.getChangeType(), OpLogChangeType.INSERT);
        Assertions.assertNull(field0.getBeforeObj());
        Assertions.assertEquals(field0.getAfterObj(), 10002L);
        Assertions.assertEquals(field0.getBefore(), "null");
        Assertions.assertEquals(field0.getAfter(), "10002");
        Assertions.assertEquals(field0.getType(), Long.class);

        // field[1] - UPDATE
        OpLogResultField field1 = logResultBean.getFieldList().get(1);
        Assertions.assertEquals(field1.getChangeType(), OpLogChangeType.UPDATE);

        // field[2] - DELETE
        OpLogResultField field2 = logResultBean.getFieldList().get(2);
        Assertions.assertEquals(field2.getChangeType(), OpLogChangeType.DELETE);
    }
}
