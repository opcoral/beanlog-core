package io.github.opcoral.oplog.entity.result;

import lombok.Data;

import java.util.List;

/**
 * Bean层级的操作日志结果<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-15 17:12
 */
@Data
public class OpLogResultBean {

    /**
     * 如果Bean没有生成日志时，返回空串
     */
    private String text;

    private String prefix;

    private String suffix;

    private String separator;

    private Class<?> beanClass;

    private List<OpLogResultField> fieldList;
}
