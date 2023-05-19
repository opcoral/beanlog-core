package io.github.opcoral.oplog.core;

import io.github.opcoral.oplog.entity.result.OpLogResult;
import io.github.opcoral.oplog.entity.parse.OpLogParseBean;

import java.util.List;

/**
 * OpLog生成器<br>
 *
 * @author GuanZH
 * @since 2023/5/14 16:45
 */
@FunctionalInterface
public interface OpLogGenerator {

    OpLogResult generate(List<OpLogParseBean> parseResultList);
}
