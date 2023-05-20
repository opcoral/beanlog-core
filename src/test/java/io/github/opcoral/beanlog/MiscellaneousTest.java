package io.github.opcoral.beanlog;

import io.github.opcoral.beanlog.core.BeanLogBuilder;
import io.github.opcoral.beanlog.core.BeanLogGenerator;
import io.github.opcoral.beanlog.core.BeanLogObjectLoader;
import io.github.opcoral.beanlog.core.BeanLogParser;
import io.github.opcoral.beanlog.entity.config.BeanLogConfig;
import io.github.opcoral.beanlog.entity.result.BeanLogResult;
import io.github.opcoral.beanlog.exception.BeanLogException;
import io.github.opcoral.beanlog.impl.builder.BeanLogBuilderImpl;
import io.github.opcoral.beanlog.impl.comparable.NullComparableImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 一些杂项测试<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-17 16:58
 */
public class MiscellaneousTest {

    @Test
    public void OpLogBuilderImplGetterAndSetter() {
        BeanLogBuilderImpl builder = new BeanLogBuilderImpl();
        // builderConfig
        BeanLogConfig builderConfig = new BeanLogConfig();
        builder.setOpLogBuilderConfig(builderConfig);
        Assertions.assertEquals(builderConfig, builder.getBuilderConfig());
        // logParser
        BeanLogParser logParser = (before, after, beanClass, beanConfig, parentConfig) -> null;
        builder.setLogParser(logParser);
        Assertions.assertEquals(logParser, builder.getLogParser());
        // logGenerator
        BeanLogGenerator logGenerator = parseResultList -> null;
        builder.setLogGenerator(logGenerator);
        Assertions.assertEquals(logGenerator, builder.getLogGenerator());
        // objectLoader
        BeanLogObjectLoader objectLoader = clazz -> null;
        builder.setObjectLoader(objectLoader);
        Assertions.assertEquals(objectLoader, builder.getObjectLoader());
    }

    @Test
    public void defaultInitTest() {
        BeanLogBuilder logBuilder = new BeanLogBuilder() {
            @Override
            public <T> BeanLogResult beanBuild(T before, T after, Class<T> beanClass, BeanLogConfig beanConfig) throws NullPointerException {return null;}

            @Override
            public BeanLogConfig getBuilderConfig() {return null;}

            @Override
            public void setOpLogBuilderConfig(BeanLogConfig builderConfig) throws NullPointerException {}

            @Override
            public BeanLogObjectLoader getObjectLoader() {return null;}

            @Override
            public void setObjectLoader(BeanLogObjectLoader objectLoader) {}
        };
        logBuilder.init();
    }

    @Test
    public void nullComparableTest() {
        NullComparableImpl impl = new NullComparableImpl();
        Assertions.assertThrows(UnsupportedOperationException.class, ()-> impl.compare(null, null, null));
    }

    @Test
    public void opLogExceptionTest() {
        new BeanLogException();
        new BeanLogException("message");

        RuntimeException re = new RuntimeException();
        BeanLogException oe = new BeanLogException("message", re);
        Assertions.assertEquals(re, oe.getTargetException());
    }
}
