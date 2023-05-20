package io.github.opcoral.beanlog.entity.result;

import io.github.opcoral.beanlog.enums.BeanLogChangeType;
import lombok.Data;

import java.util.List;

/**
 * OpLog生成后的Field<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-15 11:43
 */
@Data
public class BeanLogResultField {

    private String text;

    /**
     * 生成的日志中最后的名称
     */
    private String name;

    /**
     * 是否是内嵌的Bean<br>
     * 如果是，insideBeanList有值，before/after/changeType为null<br>
     * 如果否，insideBeanList为null
     */
    private Boolean isInsideBean = false;

    private BeanLogChangeType changeType;

    private Object beforeObj;

    private Object afterObj;

    private String before;

    private String after;

    private Class<?> type;

    private List<BeanLogResultBean> insideBeanList;
}
