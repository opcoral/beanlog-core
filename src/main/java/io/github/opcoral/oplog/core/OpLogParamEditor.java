package io.github.opcoral.oplog.core;

import io.github.opcoral.oplog.entity.param.OpLogParam;

/**
 * OpLogParam编辑器<br>
 * 在每次生成日志之前调用<br>
 * 可以编辑param，比如自定义参数添加运行时时间戳等
 *
 * @author 关卓华 Guan Zhuohua
 * @since 2023-5-15 14:24
 */
@FunctionalInterface
public interface OpLogParamEditor {

    void edit(OpLogParam opLogParam);
}
