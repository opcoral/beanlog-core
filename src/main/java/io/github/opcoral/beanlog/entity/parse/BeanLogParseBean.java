package io.github.opcoral.beanlog.entity.parse;

import io.github.opcoral.beanlog.entity.config.BeanLogConfig;
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
public class BeanLogParseBean {

    /**
     * Bean的配置<br>
     * 这里的配置是已经完成了合并之后的配置
     */
    private BeanLogConfig finalConfig;

    /**
     * Bean下包含的所有属性
     */
    private List<BeanLogParseField> fields;

    /**
     * 所属的Bean的类
     */
    private Class<?> beanType;
}
