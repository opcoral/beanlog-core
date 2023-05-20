package io.github.opcoral.oplog.entity.param;

import io.github.opcoral.oplog.entity.config.OpLogConfig;
import io.github.opcoral.oplog.entity.parse.OpLogParseField;
import io.github.opcoral.oplog.enums.OpLogChangeType;
import lombok.Data;

import java.util.Map;

/**
 * 解析日志过程中的SpEL表达式的各项参数值<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-15 10:26
 */
@Data
public class OpLogParam {

    /**
     * 日志字段名称<br>
     * 如果配置中为空，会用fieldName填充
     */
    private String name;

    /**
     * 日志的SpEl表达式
     */
    private String logExp;

    /**
     * 更新前的对象解析的字符串
     */
    private String before;

    /**
     * 更新后的对象解析的字符串
     */
    private String after;

    /**
     * 更新前的对象
     */
    private Object beforeObj;

    /**
     * 更新后的对象
     */
    private Object afterObj;

    /**
     * 属性类型
     */
    private Class<?> type;

    /**
     * Bean的类型
     */
    private Class<?> beanType;

    /**
     * 属性名称
     */
    private String fieldName;

    /**
     * 变化类型
     */
    private OpLogChangeType changeType;

    /**
     * 前缀
     */
    private String prefix;

    /**
     * 后缀
     */
    private String suffix;

    /**
     * 自定义参数<br>
     * 做为入参时不会为空
     */
    private Map<String, Object> p;

    /**
     * 配置信息
     */
    private OpLogConfig config;

    /**
     * 转换的Field
     */
    private OpLogParseField parseField;

    /**
     * 是否生成日志，默认为是，如果在editor改成否，跳过日志生成
     */
    private boolean createLog = true;
}
