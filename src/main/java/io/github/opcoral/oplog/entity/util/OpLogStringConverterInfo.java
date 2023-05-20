package io.github.opcoral.oplog.entity.util;

import io.github.opcoral.oplog.core.OpLogStringConverter;
import lombok.Data;

/**
 * 获取StringConverter的info<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-17 11:20
 */
@Data
public class OpLogStringConverterInfo {

    OpLogStringConverter converter;

    Class<?> matchClass;
}
