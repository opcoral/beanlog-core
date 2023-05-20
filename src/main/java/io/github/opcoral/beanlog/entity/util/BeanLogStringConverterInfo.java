package io.github.opcoral.beanlog.entity.util;

import io.github.opcoral.beanlog.core.BeanLogStringConverter;
import lombok.Data;

/**
 * 获取StringConverter的info<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-17 11:20
 */
@Data
public class BeanLogStringConverterInfo {

    BeanLogStringConverter converter;

    Class<?> matchClass;
}
