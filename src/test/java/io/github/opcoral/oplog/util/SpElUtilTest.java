package io.github.opcoral.oplog.util;

import io.github.opcoral.oplog.annotation.OpLogBean;
import io.github.opcoral.oplog.annotation.OpLogField;
import io.github.opcoral.oplog.exception.OpLogException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * EL表达式Test<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-12 14:25
 */
@Slf4j
public class SpElUtilTest {

    @Test
    public void originToolTest() {
        String exp = "Hello , #{ #world }";
        // 表达式解析器
        ExpressionParser parser = new SpelExpressionParser();
        // 表达式上下文
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("world", "World");
        // 解析
        Expression expression = parser.parseExpression(exp, new TemplateParserContext());
        // 结果
        String desiredResult = "Hello , World";
        String actualResult = expression.getValue(context, String.class);
        Assertions.assertEquals(desiredResult, actualResult);
    }

    @Test
    public void utilUsageTest() {
        Map<String, Object> param = new HashMap<>();
        param.put("owner", "BaiHu");
        Map<String, Object> subParam = new HashMap<>();
        subParam.put("age", "twelve");
        subParam.put("driveYears", 4);
        param.put("info", subParam);

        String format = "This is #{ #owner }, who is #{ #info[age] } years old and has drove for #{ #info[driveYears] } years";

        String desiredResult = "This is BaiHu, who is twelve years old and has drove for 4 years";
        String actualResult = SpElUtil.format(format, param);
        Assertions.assertEquals(desiredResult, actualResult);
    }

    @Test
    public void utilFieldUsageTest() throws ParseException {
        // pojo
        SimpleBean pojo = new SimpleBean();
        pojo.setUid(10001L);
        pojo.setNickName("张三");
        pojo.setTimes(1);
        pojo.setLevel(50L);
        // invoke
        String format = "uid:#{#uid}, nickName:#{#nickName}, times:#{#times}";
        String desiredResult = "uid:10001, nickName:张三, times:1";
        String actualResult = SpElUtil.format(format, pojo.getClass().getDeclaredFields(), pojo);
        Assertions.assertEquals(desiredResult, actualResult);
    }

    @Test
    public void exceptionTest() {
        // pojo
        SimpleBean pojo = new SimpleBean();
        String format = "uid:#{#uid}, nickName:#{#nickName}, times:#{#times}";
        Assertions.assertThrows(OpLogException.class, () -> SpElUtil.format(format, pojo.getClass().getDeclaredFields(), ""));
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
}
