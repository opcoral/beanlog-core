package io.github.opcoral.oplog.util;

import io.github.opcoral.oplog.exception.OpLogException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * EL表达式解析工具<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-12 14:09
 */
public class SpElUtil {

    /**
     * 表达式解析器
     */
    private static final ExpressionParser parser = new SpelExpressionParser();

    public static String format(String format, Field[] fields, Object objIncludeFields) {
        Map<String, Object> param = new HashMap<>();
        for(Field field : fields) {
            field.setAccessible(true);
            Object fieldObj;
            try {
                fieldObj = field.get(objIncludeFields);
            } catch (Exception e) {
                throw new OpLogException(e);
            }
            param.put(field.getName(), fieldObj);
        }
        return format(format, param);
    }

    public static String format(String format, Map<String, Object> param) {
        // 表达式上下文
        EvaluationContext context = new StandardEvaluationContext();
        // 为表达式赋值
        for(Map.Entry<String, Object> entry : param.entrySet()) {
            // 添加variable
            context.setVariable(entry.getKey(), entry.getValue());
        }
        // 解析
        Expression expression = parser.parseExpression(format, new TemplateParserContext());
        return expression.getValue(context, String.class);
    }
}
