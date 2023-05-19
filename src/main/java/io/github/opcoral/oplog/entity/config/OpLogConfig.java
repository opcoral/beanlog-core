package io.github.opcoral.oplog.entity.config;

import io.github.opcoral.oplog.core.OpLogComparable;
import io.github.opcoral.oplog.core.OpLogParamEditor;
import io.github.opcoral.oplog.core.OpLogStringConverter;
import io.github.opcoral.oplog.enums.OpLogChangeType;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * OpLog的配置信息<br>
 * 其他所有配置信息会合并到这个Config中<br>
 *
 * @author GuanZH
 * @since 2023/5/14 15:47
 */
@Data
public class OpLogConfig {

    /**
     * 记录到操作日志中的属性名称<br>
     * 若为null则使用变量的field名称<br>
     * 默认：null
     */
    private String name;

    /**
     * 单个field生成的提示文本<br>
     * SpEl表达式填充<br>
     * 全局默认：#{#name}: #{#before}->#{#after}
     */
    private String logExp;

    /**
     * 日志生效的变更类型<br>
     * 未指定的变更类型不会记录日志<br>
     * 全局默认：new OpLogChangeType[]{OpLogChangeType.UPDATE, OpLogChangeType.INSERT, OpLogChangeType.DELETE}
     */
    private OpLogChangeType[] effectiveType;

    /**
     * 为before和after添加的前缀<br>
     * 不支持SpEL表达式<br>
     * 全局默认：""
     */
    private String prefix;

    /**
     * 为before和after添加的后缀<br>
     * 不支持SpEL表达式<br>
     * 全局默认：""
     */
    private String suffix;

    /**
     * 如果before/after为null，日志中显示的字符串<br>
     * 忽略前后缀的配置<br>
     * 这个变量只会影响生成的before/after，不会影响SpEl中其他的param参数<br>
     * 全局默认："null"
     */
    private String logForNull;

    /**
     * 是否将空串视为null处理<br>
     * 如果是，比较是否相等时如果before或after为空串，则会将其显示为null，并视为null进行比较<br>
     * 非CharSequence/String类型生成的toString()如果是空串，不会认为是空串<br>
     * 注：beforeObj和afterObj会保持原值<br>
     * 全局默认：false
     */
    private Boolean treatBlankStringAsNull;

    /**
     * 自定义参数<br>
     * 全局默认：new HashMap<>()
     */
    private Map<String, Object> param;

    /**
     * 生成的日志的前缀<br>
     * 对于最外层的大Bean，这即为全局前缀<br>
     * 对于内嵌的Bean，会生成局部的前缀<br>
     * 若启用SpEl表达式，作用域为bean作用域<br>
     * 全局默认：""
     */
    private String beanPrefix;

    /**
     * 全局前缀是否启用SpEl表达式<br>
     * 全局默认：false
     */
    private Boolean beanPrefixEnableSpEl;

    /**
     * 生成的日志的后缀<br>
     * 对于最外层的大Bean，这即为全局后缀<br>
     * 对于内嵌的Bean，会生成局部的后缀<br>
     * 若启用SpEl表达式，作用域为bean作用域，仅支持P和beanType两个参数<br>
     * 全局默认：""
     */
    private String beanSuffix;

    /**
     * 全局后缀是否启用SpEl表达式<br>
     * 全局默认：false
     */
    private Boolean beanSuffixEnableSpEl;

    /**
     * 在log内部的内容为空的情况下，是否生成前缀<br>
     * 全局默认：false
     */
    private Boolean createBeanPrefixWhenLogIsEmpty;

    /**
     * 在log内部的内容为空的情况下，是否生成后缀<br>
     * 全局默认：false
     */
    private Boolean createBeanSuffixWhenLogIsEmpty;

    /**
     * 内嵌的Bean生成的日志为空串时，主bean是否拼接日志<br>
     * 全局默认：false
     */
    private Boolean concatWhenSubLogIsBlank;

    /**
     * 分隔符<br>
     * 若启用SpEl表达式，作用域为bean作用域<br>
     * 全局默认：", "
     */
    private String separator;

    /**
     * 分隔符是否启用SpEl表达式<br>
     * 全局默认：false
     */
    private Boolean separatorEnableSpEl;

    /**
     * 转换器Map<br>
     * 合并配置时会合并map，键相同时，后者会覆盖前者<br>
     * 未在这里定义转换器的类型，会调用toString()方法生成字符串<br>
     * 全局默认：包含解析一些常见类型的Map
     */
    private Map<Class<?>, OpLogStringConverter> converterMap;

    /**
     * 参数编辑信息列表<br>
     * 合并配置时会合并List，以配置的优先级从低到高按顺序执行<br>
     * 全局默认：new ArrayList<>()
     */
    private List<OpLogParamEditor> paramEditors;

    /**
     * 比较器<br>
     * 全局默认：调用equal的方法
     */
    private OpLogComparable comparable;

    /**
     * 组别标识<br>
     * 标识这个配置所属的组别<br>
     * 若未包含组别，则会出现在所有的组别中<br>
     * 全局默认：{}
     */
    private String[] groups;

    /**
     * 当前组别，配合groups一起使用<br>
     * 如果不为空，最后生成的日志中如果currentGroup 不在 groups中，则不会生成日志<br>
     * 全局默认：null
     */
    private String currentGroup;

    /*--   以下是一些常见类型的处理参数   --*/

    /**
     * 日期类型的格式化输出格式<br>
     * 全局默认："yyyy-MM-dd HH:mm:ss"
     */
    private String dateFormat;

    /**
     * 数字类型的小数位数保留<br>
     * 如果为null，不限制小数位数的保留<br>
     * 全局默认：null
     */
    private String decimalFormat;

    /**
     * Collection前缀<br>
     * 全局默认："["
     */
    private String collectionPrefix;

    /**
     * Collection后缀<br>
     * 全局默认："]"
     */
    private String collectionSuffix;

    /**
     * Collection分隔符<br>
     * 全局默认：","
     */
    private String collectionSeparator;

    /**
     * Collection中null值的替换<br>
     * 全局默认："null"
     */
    private String collectionLogForNull;
}
