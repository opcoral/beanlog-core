package io.github.opcoral.oplog.core;

/**
 * 根据类型获取对象的方法<br>
 * <br>
 *
 * @author 关卓华 Guan Zhuohua
 * @since 2023-5-16 18:10
 */
@FunctionalInterface
public interface OpLogObjectLoader {

    Object load(Class<?> clazz);
}
