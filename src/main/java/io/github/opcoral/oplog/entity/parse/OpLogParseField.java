package io.github.opcoral.oplog.entity.parse;

import io.github.opcoral.oplog.entity.config.OpLogConfig;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 操作日志解析后的Field信息<br>
 *
 * @author GuanZH
 * @since 2023/5/14 16:54
 */
@Data
public class OpLogParseField {

    /**
     * field的配置信息<br>
     * 这里的配置是已经完成了合并之后的配置
     */
    private OpLogConfig finalConfig;

    /**
     * 是否是内部的Bean<br>
     * 如果是，忽略before和after，遍历insideBean
     * 如果否，忽略insideBean，读取before和after
     */
    private Boolean isInsideBean = false;

    /**
     * 修改前的值
     */
    private Object beforeObj;

    /**
     * 修改后的值
     */
    private Object afterObj;

    /**
     * field的类型
     */
    private Class<?> type;

    /**
     * bean的类型
     */
    private Class<?> beanType;

    /**
     * field/method上修饰的所有注解
     */
    private Annotation[] annotations;

    /**
     * 属性名称<br>
     * 属性会写入属性名称，方法会写入方法名称
     */
    private String fieldName;

    /**
     * 更多属性，继续往内部遍历
     */
    private List<OpLogParseBean> insideBean;
}
