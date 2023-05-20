package io.github.opcoral.beanlog.core;

import io.github.opcoral.beanlog.entity.config.BeanLogConfig;
import io.github.opcoral.beanlog.entity.result.BeanLogResult;

/**
 * 操作日志创建器<br>
 *
 * @author GuanZH
 * @since 2023/5/14 15:30
 */
public interface BeanLogBuilder {

    /**
     * 生成操作日志
     * @param before 改变前的实体
     * @param after 改变后的实体
     * @param beanClass bean所属的类
     * @return 生成的操作日志
     * @param <T> bean所属的类的泛型
     * @exception NullPointerException 如果入参中存在为null的参数
     */
    default <T> BeanLogResult beanBuild(T before, T after, Class<T> beanClass) throws NullPointerException {
        return this.beanBuild(before, after, beanClass, new BeanLogConfig());
    }

    /**
     * 生成操作日志
     * @param before 改变前的实体
     * @param after 改变后的实体
     * @param beanClass bean所属的类
     * @param beanConfig 追加的bean全局配置
     * @return 生成的操作日志
     * @param <T> bean所属的类的泛型
     * @exception NullPointerException 如果入参中存在为null的参数
     */
    <T> BeanLogResult beanBuild(T before, T after, Class<T> beanClass, BeanLogConfig beanConfig) throws NullPointerException;

    /**
     * 初始化方法<br>
     */
    default void init() {}

    /**
     * 获取builder的配置信息
     * @return builder的配置
     */
    BeanLogConfig getBuilderConfig();

    /**
     * 设置builder的配置信息
     * @param builderConfig 要设置的builder的配置信息。不允许为null
     * @exception NullPointerException 如果设置的builderConfig为null
     */
    void setOpLogBuilderConfig(BeanLogConfig builderConfig) throws NullPointerException;

    /**
     * 获取ObjectLoader
     * @return objectLoader
     */
    BeanLogObjectLoader getObjectLoader();

    /**
     * 设置ObjectLoader
     * @param objectLoader objectLoader，注解上的Class类型的配置会根据这个loader配置加载类
     */
    void setObjectLoader(BeanLogObjectLoader objectLoader);
}
