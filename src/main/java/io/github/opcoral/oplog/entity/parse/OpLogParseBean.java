package io.github.opcoral.oplog.entity.parse;

import io.github.opcoral.oplog.entity.config.OpLogConfig;
import lombok.Data;

import java.util.List;

/**
 * 操作日志的遍历实体信息<br>
 * 包含group不符合的实体或者属性
 *
 * @author GuanZH
 * @since 2023/5/14 16:40
 */
@Data
public class OpLogParseBean {

    /**
     * Bean的配置<br>
     * 这里的配置是已经完成了合并之后的配置
     */
    private OpLogConfig finalConfig;

    /**
     * Bean下包含的所有属性
     */
    private List<OpLogParseField> fields;

    /**
     * 所属的Bean的类
     */
    private Class<?> beanType;
}
