package io.github.opcoral.oplog.test;

import io.github.opcoral.oplog.core.OpLogBuilder;
import io.github.opcoral.oplog.core.OpLogGenerator;
import io.github.opcoral.oplog.core.OpLogObjectLoader;
import io.github.opcoral.oplog.core.OpLogParser;
import io.github.opcoral.oplog.entity.config.OpLogConfig;
import io.github.opcoral.oplog.entity.result.OpLogResult;
import io.github.opcoral.oplog.exception.OpLogException;
import io.github.opcoral.oplog.impl.builder.OpLogBuilderImpl;
import io.github.opcoral.oplog.impl.comparable.NullComparableImpl;
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
        OpLogBuilderImpl builder = new OpLogBuilderImpl();
        // builderConfig
        OpLogConfig builderConfig = new OpLogConfig();
        builder.setOpLogBuilderConfig(builderConfig);
        Assertions.assertEquals(builderConfig, builder.getBuilderConfig());
        // logParser
        OpLogParser logParser = (before, after, beanClass, beanConfig, parentConfig) -> null;
        builder.setLogParser(logParser);
        Assertions.assertEquals(logParser, builder.getLogParser());
        // logGenerator
        OpLogGenerator logGenerator = parseResultList -> null;
        builder.setLogGenerator(logGenerator);
        Assertions.assertEquals(logGenerator, builder.getLogGenerator());
        // objectLoader
        OpLogObjectLoader objectLoader = clazz -> null;
        builder.setObjectLoader(objectLoader);
        Assertions.assertEquals(objectLoader, builder.getObjectLoader());
    }

    @Test
    public void defaultInitTest() {
        OpLogBuilder logBuilder = new OpLogBuilder() {
            @Override
            public <T> OpLogResult beanBuild(T before, T after, Class<T> beanClass, OpLogConfig beanConfig) throws NullPointerException {return null;}

            @Override
            public OpLogConfig getBuilderConfig() {return null;}

            @Override
            public void setOpLogBuilderConfig(OpLogConfig builderConfig) throws NullPointerException {}

            @Override
            public OpLogObjectLoader getObjectLoader() {return null;}

            @Override
            public void setObjectLoader(OpLogObjectLoader objectLoader) {}
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
        new OpLogException();
        new OpLogException("message");

        RuntimeException re = new RuntimeException();
        OpLogException oe = new OpLogException("message", re);
        Assertions.assertEquals(re, oe.getTargetException());
    }
}
