package io.github.opcoral.beanlog.test;

import io.github.opcoral.beanlog.util.SpElUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

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
    public void simpleElTest() {
        String exp = "Hello , #{ #world }";
        // 表达式解析器
        ExpressionParser parser = new SpelExpressionParser();
        // 表达式上下文
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("world", "World");
        // 解析
        Expression expression = parser.parseExpression(exp, new TemplateParserContext());
        // 对比
        String desireResult = "Hello , World";
        String actualResult = expression.getValue(context, String.class);
        Assertions.assertEquals(desireResult, actualResult);
    }

    @Test
    public void expTest() {
        Map<String, Object> param = new HashMap<>();
        param.put("owner", "BaiHu");
        Map<String, Object> subParam = new HashMap<>();
        subParam.put("age", "twelve");
        subParam.put("driveYears", 4);
        param.put("info", subParam);

        String format = "This is #{ #owner }, who is #{ #info[age] } years old and has drove for #{ #info[driveYears] } years";
        String wantedResult = "This is BaiHu, who is twelve years old and has drove for 4 years";
        String result = SpElUtil.format(format, param);
        Assertions.assertEquals(wantedResult, result);
    }
}
