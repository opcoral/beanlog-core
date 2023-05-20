package io.github.opcoral.oplog.core;

import io.github.opcoral.oplog.entity.parse.OpLogParseField;

/**
 * OpLog自定义转换器<br>
 * 自定义根据类型转化成String的方法<br>
 * 解决的粒度：beforeObj -> before(无前后缀), afterObj -> after(无前后缀)<br>
 * 对于OpLog内部的调用，可以保证入参不为null
 *
 * @author GuanZH
 * @since 2023-5-15 14:14
 */
public interface OpLogStringConverter {

    /**
     * 自定义转换器的转换方法
     * @param obj 要转换的obj。对于OpLog内部的调用，可以保证入参不为null
     * @param matchClass 转换的obj匹配的类
     * @param parseField 要转换的对象解析出来的field
     * @return 转换后的日志
     */
    String convert(Object obj, Class<?> matchClass, OpLogParseField parseField);
}
