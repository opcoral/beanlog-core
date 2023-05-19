package io.github.opcoral.oplog.entity.result;

import lombok.Data;

import java.util.List;

/**
 * 生成的操作日志实体<br>
 * 不匹配的日志不会出现在实体中
 *
 * @author GuanZH
 * @since 2023/5/14 15:39
 */
@Data
public class OpLogResult {

    /**
     * 生成的日志结果文本
     */
    private String text;

    /**
     * 以bean的维度获取的每个Bean的生成信息<br>
     * 这个List的长度与对应的Bean上的注解数量挂钩
     */
    private List<OpLogResultBean> resultBeanList;
}
