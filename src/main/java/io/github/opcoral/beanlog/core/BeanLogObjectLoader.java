package io.github.opcoral.beanlog.core;

/**
 * 根据类型获取对象的方法<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-16 18:10
 */
@FunctionalInterface
public interface BeanLogObjectLoader {

    Object load(Class<?> clazz);
}
