package io.github.opcoral.beanlog;

import io.github.opcoral.beanlog.annotation.BeanLogBean;
import io.github.opcoral.beanlog.annotation.BeanLogField;
import io.github.opcoral.beanlog.core.BeanLogBuilder;
import io.github.opcoral.beanlog.core.BeanLogComparable;
import io.github.opcoral.beanlog.entity.config.BeanLogConfig;
import io.github.opcoral.beanlog.entity.result.BeanLogResult;
import io.github.opcoral.beanlog.entity.result.BeanLogResultField;
import io.github.opcoral.beanlog.enums.BeanLogBoolean;
import io.github.opcoral.beanlog.enums.BeanLogChangeType;
import io.github.opcoral.beanlog.exception.BeanLogException;
import io.github.opcoral.beanlog.impl.builder.BeanLogBuilderImpl;
import io.github.opcoral.beanlog.impl.editor.CollectionItemChangeEditorImpl;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * OpLogField注解的测试用例<br>
 * <br>
 * @since 2023-5-16 20:25
 * @author GuanZH
 */
public class BeanLogFieldTest {

    private final BeanLogBuilder logBuilder = new BeanLogBuilderImpl();

    /**
     * 字段name测试
     */
    @Test
    public void nameTest() {
        // 定义内部类
        @Data
        class NameTestPojo {
            @BeanLogField(name = "带别名的名字")
            private String nameAlias = null;
            @BeanLogField
            private String nameDefault = null;
            private String nameIgnore = null;
            @BeanLogField(nameIgnoreBlank = false)
            private String nameBlank = null;
            @BeanLogField(name = "nameIgnoreBlankNotBlank", nameIgnoreBlank = false)
            private String nameIgnoreBlankNotBlank = null;
        }
        // 定义pojo
        NameTestPojo before = new NameTestPojo();
        NameTestPojo after = new NameTestPojo();
        after.setNameAlias("name");
        after.setNameDefault("name");
        after.setNameIgnore("name");
        after.setNameBlank("name");
        after.setNameIgnoreBlankNotBlank("name");
        // test
        String desireResult = "带别名的名字: null->name, nameDefault: null->name, : null->name, nameIgnoreBlankNotBlank: null->name";
        String actualResult = logBuilder.beanBuild(before, after, NameTestPojo.class).getText();
        Assertions.assertEquals(desireResult, actualResult);
    }

    @Test
    public void logExpTest() {
        class LogExpTestPojo {
            @BeanLogField
            String defaultLogExp;
            @BeanLogField(logExpIgnoreBlank = false)
            String emptyLogExp;
            @BeanLogField(name = "自定义填充式", logExp = "log变量值：name:#{#name}, logExp:#{#logExp}, before:#{#before}, after:#{#after}, beforeObj:#{#beforeObj}, after:#{#afterObj}, type:#{#type.getSimpleName()}, beanType:#{#beanType.getSimpleName()}, fieldName:#{#fieldName}, changeType:#{#changeType.isChanged()}, prefix:#{#prefix}, suffix:#{#suffix}, createLog:#{#createLog}, pValue:#{#p[value]}")
            String paramLogExp;
        }

        LogExpTestPojo before = new LogExpTestPojo();
        before.defaultLogExp = "logBefore";
        before.emptyLogExp = "logBefore";
        before.paramLogExp = "logBefore";
        LogExpTestPojo after = new LogExpTestPojo();
        after.defaultLogExp = "logAfter";
        after.emptyLogExp = "logAfter";
        after.paramLogExp = "logAfter";

        Map<String, Object> param = new HashMap<>();
        param.put("value", "value");
        BeanLogConfig config = new BeanLogConfig();
        config.setParam(param);

        String desireResult = "defaultLogExp: logBefore->logAfter, , log变量值：name:自定义填充式, logExp:log变量值：name:#{#name}, logExp:#{#logExp}, before:#{#before}, after:#{#after}, beforeObj:#{#beforeObj}, after:#{#afterObj}, type:#{#type.getSimpleName()}, beanType:#{#beanType.getSimpleName()}, fieldName:#{#fieldName}, changeType:#{#changeType.isChanged()}, prefix:#{#prefix}, suffix:#{#suffix}, createLog:#{#createLog}, pValue:#{#p[value]}, before:logBefore, after:logAfter, beforeObj:logBefore, after:logAfter, type:String, beanType:LogExpTestPojo, fieldName:paramLogExp, changeType:true, prefix:, suffix:, createLog:true, pValue:value";
        String actualResult = logBuilder.beanBuild(before, after, LogExpTestPojo.class, config).getText();
        Assertions.assertEquals(desireResult, actualResult);
    }

    /**
     * 日志注解生效类型测试
     */
    @Test
    public void effectiveTypeTest() {
        class EffectiveTypeTestPojo {
            @BeanLogField
            String defaultEffectiveType;
            @BeanLogField(logExp = "#{#name} INSERT: #{#after}", effectiveType = BeanLogChangeType.INSERT)
            @BeanLogField(logExp = "#{#name} UPDATE: #{#before}->#{#after}", effectiveType = BeanLogChangeType.UPDATE)
            @BeanLogField(logExp = "#{#name} DELETE: #{#before}", effectiveType = BeanLogChangeType.DELETE)
            @BeanLogField(logExp = "#{#name} #{#changeType.name()}", effectiveType = {BeanLogChangeType.EQUAL, BeanLogChangeType.NULL})
            @BeanLogField
            @BeanLogField(effectiveTypeIgnoreBlank = false)
            String effectiveTypeDifferentLog;
        }

        EffectiveTypeTestPojo before = new EffectiveTypeTestPojo();
        before.defaultEffectiveType = "before";
        before.effectiveTypeDifferentLog = "before";
        EffectiveTypeTestPojo after = new EffectiveTypeTestPojo();
        after.defaultEffectiveType = "after";
        after.effectiveTypeDifferentLog = "after";

        String desireResult1 = "defaultEffectiveType: before->after, effectiveTypeDifferentLog UPDATE: before->after, effectiveTypeDifferentLog: before->after";
        String actualResult1 = logBuilder.beanBuild(before, after, EffectiveTypeTestPojo.class).getText();
        Assertions.assertEquals(desireResult1, actualResult1);

        after.effectiveTypeDifferentLog = null;
        String desireResult2 = "defaultEffectiveType: before->after, effectiveTypeDifferentLog DELETE: before, effectiveTypeDifferentLog: before->null";
        String actualResult2 = logBuilder.beanBuild(before, after, EffectiveTypeTestPojo.class).getText();
        Assertions.assertEquals(desireResult2, actualResult2);

        before.effectiveTypeDifferentLog = null;
        after.effectiveTypeDifferentLog = "after";
        String desireResult3 = "defaultEffectiveType: before->after, effectiveTypeDifferentLog INSERT: after, effectiveTypeDifferentLog: null->after";
        String actualResult3 = logBuilder.beanBuild(before, after, EffectiveTypeTestPojo.class).getText();
        Assertions.assertEquals(desireResult3, actualResult3);

        after.effectiveTypeDifferentLog = null;
        String desireResult4 = "defaultEffectiveType: before->after, effectiveTypeDifferentLog NULL";
        String actualResult4 = logBuilder.beanBuild(before, after, EffectiveTypeTestPojo.class).getText();
        Assertions.assertEquals(desireResult4, actualResult4);

        before.effectiveTypeDifferentLog = "equal";
        after.effectiveTypeDifferentLog = "equal";
        String desireResult5 = "defaultEffectiveType: before->after, effectiveTypeDifferentLog EQUAL";
        String actualResult5 = logBuilder.beanBuild(before, after, EffectiveTypeTestPojo.class).getText();
        Assertions.assertEquals(desireResult5, actualResult5);
    }

    @Test
    public void prefixAndSuffixTest() {
        @BeanLogBean(prefix = "{", suffix = "}", prefixIgnoreBlank = false, suffixIgnoreBlank = false, logForNull = " ", logForNullIgnoreBlank = false)
        class PrefixAndSuffixTestPojo {
            @BeanLogField
            @BeanLogField(prefix = ">")
            @BeanLogField(suffix = "<")
            @BeanLogField(prefixIgnoreBlank = false)
            @BeanLogField(suffixIgnoreBlank = false)
            String param;
        }

        PrefixAndSuffixTestPojo pojo = new PrefixAndSuffixTestPojo();
        pojo.param = "p";

        BeanLogResult result = logBuilder.beanBuild(pojo, new PrefixAndSuffixTestPojo(), PrefixAndSuffixTestPojo.class);

        String desireResult = "param: {p}-> , param: >p}-> , param: {p<-> , param: p}-> , param: {p-> ";
        String actualResult = result.getText();
        Assertions.assertEquals(desireResult, actualResult);

        List<BeanLogResultField> resultFieldList = result.getResultBeanList().get(0).getFieldList();
        Assertions.assertEquals(resultFieldList.size(), 5);

        String[] desireBeforeStr = {"{p}", ">p}", "{p<", "p}", "{p"};
        for(int i = 0; i < 5; i++) {
            Assertions.assertEquals(resultFieldList.get(i).getBefore(), desireBeforeStr[i]);
        }
    }

    @Test
    public void logForNullOrBlankTest() {
        @BeanLogBean(effectiveType = {BeanLogChangeType.EQUAL, BeanLogChangeType.NULL, BeanLogChangeType.INSERT, BeanLogChangeType.UPDATE, BeanLogChangeType.DELETE}, effectiveTypeIgnoreBlank = false, treatBlankStringAsNull = BeanLogBoolean.FALSE)
        class LogForNullOrBlankPojo {
            @BeanLogField
            @BeanLogField(logForNull = "(空)")
            @BeanLogField(logForNullIgnoreBlank = false)
            @BeanLogField(logForNull = "(空)", treatBlankStringAsNull = BeanLogBoolean.TRUE)
            String param;
        }

        LogForNullOrBlankPojo before = new LogForNullOrBlankPojo();
        before.param = null;
        LogForNullOrBlankPojo after = new LogForNullOrBlankPojo();
        after.param = "";

        String desireResult = "param: null->, param: (空)->, param: ->, param: (空)->(空)";
        String actualResult = logBuilder.beanBuild(before, after, LogForNullOrBlankPojo.class).getText();
        Assertions.assertEquals(desireResult, actualResult);
    }

    /**
     * editor和List变化的变更测试用例
     */
    @Test
    public void editorAndListTest() {
        class ListChangePojo {
            @BeanLogField(logExp = "#{#name} #{#before}->#{#after} 新增项：#{#p[addItem]} 删除项：#{#p[removeItem]} 共同项：#{#p[commonItem]}", paramEditors = CollectionItemChangeEditorImpl.class,
                        collectionPrefix = "<", collectionPrefixIgnoreBlank = false,
                        collectionSuffix = ">", collectionSuffixIgnoreBlank = false,
                        collectionSeparator = "/", collectionSeparatorIgnoreBlank = false,
                        collectionLogForNull = "方块0", collectionLogForNullIgnoreBlank = false)
            List<String> weapons;
        }

        ListChangePojo before = new ListChangePojo();
        before.weapons = Arrays.asList("物理学圣剑", "以理服人", null);
        ListChangePojo after = new ListChangePojo();
        after.weapons = Arrays.asList("以理服人", null, "有话直说");

        String desireResult = "weapons <物理学圣剑/以理服人/方块0>-><以理服人/方块0/有话直说> 新增项：<有话直说> 删除项：<物理学圣剑> 共同项：<方块0/以理服人>";
        String actualResult = logBuilder.beanBuild(before, after, ListChangePojo.class).getText();
        Assertions.assertEquals(desireResult, actualResult);
    }

    static class AbnormalComparator implements BeanLogComparable {
        public AbnormalComparator() {}
        @Override
        public BeanLogChangeType compare(Object before, Object after, Class<?> fieldClass) {
            return BeanLogChangeType.NULL;
        }
    }

    @Test
    public void comparableTest() {

        class CBean {
            @BeanLogField(logExp = "#{T(java.lang.String).valueOf(#changeType.isChanged())}", comparable = AbnormalComparator.class, effectiveType = BeanLogChangeType.NULL)
            int a;
        }
        CBean before = new CBean();
        before.a = 1;
        CBean after = new CBean();
        after.a = 2;

        String desireResult = String.valueOf(BeanLogChangeType.NULL.isChanged());
        String actualResult = logBuilder.beanBuild(before, after, CBean.class).getText();
        Assertions.assertEquals(desireResult, actualResult);
    }

    /**
     * 分组日志测试
     */
    @Test
    public void groupTest() {
        @BeanLogBean(groups = "insert", beanPrefix = "插入日志：", groupsIgnoreBlank = false)
        @BeanLogBean(groups = "update", beanPrefix = "更新日志：", groupsIgnoreBlank = false)
        class Bean {
            @BeanLogField(groups = "insert", logExp = "#{#name} 参数添加：#{#after}")
            @BeanLogField(groups = "update", logExp = "#{#name} 参数更新：由#{#before}变为#{#after}")
            @BeanLogField(groups = {"insert", "update"}, logExp = "目前所在域：#{#config.getCurrentGroup()}")
            @BeanLogField(groupsIgnoreBlank = false)
            String param;
        }

        Bean before = new Bean();
        before.param = null;
        Bean after = new Bean();
        after.param = "param";

        BeanLogConfig config1 = new BeanLogConfig();
        config1.setCurrentGroup("insert");
        String desireResult1 = "插入日志：param 参数添加：param, 目前所在域：insert";
        String actualResult1 = logBuilder.beanBuild(before, after, Bean.class, config1).getText();
        Assertions.assertEquals(desireResult1, actualResult1);

        BeanLogConfig config2 = new BeanLogConfig();
        config2.setCurrentGroup("update");
        String desireResult2 = "更新日志：param 参数更新：由null变为param, 目前所在域：update";
        String actualResult2 = logBuilder.beanBuild(before, after, Bean.class, config2).getText();
        Assertions.assertEquals(desireResult2, actualResult2);

        String desireResult3 = "插入日志：param 参数添加：param, param 参数更新：由null变为param, 目前所在域：, param: null->param更新日志：param 参数添加：param, param 参数更新：由null变为param, 目前所在域：, param: null->param";
        String actualResult3 = logBuilder.beanBuild(before, after, Bean.class, new BeanLogConfig()).getText();
        Assertions.assertEquals(desireResult3, actualResult3);
    }

    /**
     * 自带的类型转换测试
     */
    @Test
    public void transferTest() throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        class TransferTestPojo {
            @BeanLogField(dateFormat = "yyyy-MM-dd HH:mm:ss")
            Date date1;
            @BeanLogField(dateFormat = "yyyy-MM-dd HH:mm:ss", dateFormatIgnoreBlank = false)
            LocalDate date2;
            @BeanLogField(decimalFormat = "0.00")
            Long num1;
            @BeanLogField(decimalFormat = "0")
            Double num2;
            @BeanLogField(decimalFormat = "0.00")
            BigInteger num3;
            @BeanLogField(decimalFormat = "0.00")
            BigDecimal num4;
            @BeanLogField(decimalFormat = "0.000", decimalFormatIgnoreBlank = false)
            double num5;
            @BeanLogField
            float num6;
        }

        TransferTestPojo before = new TransferTestPojo();
        before.date1 = df.parse("2023-04-26 10:00:00");
        before.date2 = LocalDate.parse("2023-05-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        before.num1 = 114514L;
        before.num2 = 520.077;
        before.num3 = new BigInteger("39");
        before.num4 = new BigDecimal("445");
        before.num5 = 250.0;
        before.num6 = 7.0f;
        TransferTestPojo after = new TransferTestPojo();
        after.date1 = df.parse("2023-05-03 10:00:00");
        after.date2 = LocalDate.parse("2023-05-01 11:45:14", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        after.num1 = 1919810L;
        after.num2 = 520.1314;
        after.num3 = new BigInteger("40");
        after.num4 = new BigDecimal("253");
        after.num5 = 250.0;
        after.num6 = 7.0001f;

        // 注: date2是LocalDate只有日期，所以相等
        String desireResult = "date1: 2023-04-26 10:00:00->2023-05-03 10:00:00, num1: 114514.00->1919810.00, num2: 520->520, num3: 39.00->40.00, num4: 445.00->253.00, num6: 7.0->7.0001";
        String actualResult = logBuilder.beanBuild(before, after, TransferTestPojo.class).getText();
        Assertions.assertEquals(desireResult, actualResult);
    }

    /**
     * 修饰无参带返回值方法
     */
    @Test
    public void modifiedMethodWithReturnValueAndNoParameters() {
        class Bean {
            String returnValue;
            @BeanLogField
            public String method() {
                return returnValue;
            }
        }

        Bean before = new Bean();
        before.returnValue = "before";
        Bean after = new Bean();
        after.returnValue = "after";

        String desireResult = "method: " + before.method() + "->" + after.method();
        String actualResult = logBuilder.beanBuild(before, after, Bean.class).getText();
        Assertions.assertEquals(desireResult, actualResult);
    }

    /**
     * 修饰空返回值无参方法
     */
    @Test
    public void modifiedMethodWithEmptyReturnValueAndNoParameters() {
        class Bean {
            @BeanLogField
            public void method() {}
        }

        new Bean().method();
        Assertions.assertThrows(BeanLogException.class, ()->logBuilder.beanBuild(new Bean(), new Bean(), Bean.class));
    }

    /**
     * 修饰有返回值带参方法
     */
    @Test
    public void modifiedMethodWithReturnValueAndParameter() {
        class Bean {
            @BeanLogField
            public String method(String input) {
                return input;
            }
        }

        Bean before = new Bean();
        String s = before.method("method");
        Bean after = new Bean();
        after.method(s);

        Assertions.assertThrows(BeanLogException.class, ()->logBuilder.beanBuild(before, after, Bean.class));
    }
}
