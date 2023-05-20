package io.github.opcoral.oplog.test;

import io.github.opcoral.oplog.annotation.OpLogBean;
import io.github.opcoral.oplog.annotation.OpLogField;
import io.github.opcoral.oplog.annotation.OpLogInside;
import io.github.opcoral.oplog.core.OpLogBuilder;
import io.github.opcoral.oplog.core.OpLogComparable;
import io.github.opcoral.oplog.entity.config.OpLogConfig;
import io.github.opcoral.oplog.enums.OpLogBoolean;
import io.github.opcoral.oplog.enums.OpLogChangeType;
import io.github.opcoral.oplog.impl.builder.OpLogBuilderImpl;
import io.github.opcoral.oplog.impl.editor.CollectionItemChangeEditorImpl;
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
 * OpLogBean的单元测试<br>
 * <br>
 *
 * @author GuanZH
 * @since 2023-5-17 15:16
 */
public class OpLogBeanTest {

    private final OpLogBuilder logBuilder = new OpLogBuilderImpl();

    /**
     * 统一日志表达式的替换测试
     */
    @Test
    public void logExpTest() {
        @OpLogBean(logExp = "log变量值：name:#{#name}, logExp:#{#logExp}, before:#{#before}, after:#{#after}, beforeObj:#{#beforeObj}, after:#{#afterObj}, type:#{#type.getSimpleName()}, beanType:#{#beanType.getSimpleName()}, fieldName:#{#fieldName}, changeType:#{#changeType.isChanged()}, prefix:#{#prefix}, suffix:#{#suffix}, createLog:#{#createLog}, pValue:#{#p[value]}", logExpIgnoreBlank = false)
        class LogExpTestPojo {
            @OpLogField
            String defaultLogExp;
            @OpLogField
            String emptyLogExp;
            @OpLogField(name = "自定义填充式", logExp = "log名称：name:#{#name}")
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
        OpLogConfig config = new OpLogConfig();
        config.setParam(param);

        String desireResult = "log变量值：name:defaultLogExp, logExp:log变量值：name:#{#name}, logExp:#{#logExp}, before:#{#before}, after:#{#after}, beforeObj:#{#beforeObj}, after:#{#afterObj}, type:#{#type.getSimpleName()}, beanType:#{#beanType.getSimpleName()}, fieldName:#{#fieldName}, changeType:#{#changeType.isChanged()}, prefix:#{#prefix}, suffix:#{#suffix}, createLog:#{#createLog}, pValue:#{#p[value]}, before:logBefore, after:logAfter, beforeObj:logBefore, after:logAfter, type:String, beanType:LogExpTestPojo, fieldName:defaultLogExp, changeType:true, prefix:, suffix:, createLog:true, pValue:value, log变量值：name:emptyLogExp, logExp:log变量值：name:#{#name}, logExp:#{#logExp}, before:#{#before}, after:#{#after}, beforeObj:#{#beforeObj}, after:#{#afterObj}, type:#{#type.getSimpleName()}, beanType:#{#beanType.getSimpleName()}, fieldName:#{#fieldName}, changeType:#{#changeType.isChanged()}, prefix:#{#prefix}, suffix:#{#suffix}, createLog:#{#createLog}, pValue:#{#p[value]}, before:logBefore, after:logAfter, beforeObj:logBefore, after:logAfter, type:String, beanType:LogExpTestPojo, fieldName:emptyLogExp, changeType:true, prefix:, suffix:, createLog:true, pValue:value, log名称：name:自定义填充式";
        String actualResult = logBuilder.beanBuild(before, after, LogExpTestPojo.class, config).getText();
        Assertions.assertEquals(desireResult, actualResult);
    }

    /**
     * 前后缀测试
     */
    @Test
    public void beanPrefixSuffixTest() {
        @OpLogBean(beanPrefix = "#{#p[userName]}操作了记录：", beanPrefixEnableSpEl = OpLogBoolean.TRUE, beanPrefixIgnoreBlank = false,
                   beanSuffix = "操作完成#{#p[userName]}", beanSuffixEnableSpEl = OpLogBoolean.FALSE, beanSuffixIgnoreBlank = false,
                   separator = "#{#p[s]}", separatorIgnoreBlank = false, separatorEnableSpEl = OpLogBoolean.TRUE,
                   createBeanPrefixWhenLogIsEmpty = OpLogBoolean.TRUE, createBeanSuffixWhenLogIsEmpty = OpLogBoolean.TRUE)
        class LogClassPojo {
            @OpLogField
            String param1;
            @OpLogField
            String param2;
        }

        OpLogConfig config = new OpLogConfig();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userName", "王小美");
        paramMap.put("s", ", ");
        config.setParam(paramMap);

        String desireResult1 = "王小美操作了记录：操作完成#{#p[userName]}";
        String actualResult1 = logBuilder.beanBuild(new LogClassPojo(), new LogClassPojo(), LogClassPojo.class, config).getText();
        Assertions.assertEquals(desireResult1, actualResult1);

        LogClassPojo before = new LogClassPojo();
        before.param1 = "before";
        before.param2 = "before";
        String desireResult2 = "王小美操作了记录：param1: before->null, param2: before->null操作完成#{#p[userName]}";
        String actualResult2 = logBuilder.beanBuild(before, new LogClassPojo(), LogClassPojo.class, config).getText();
        Assertions.assertEquals(desireResult2, actualResult2);
    }

    @Test
    public void editorAndListTest() {
        @OpLogBean(logExp = "#{#name} #{#before}->#{#after} 新增项：#{#p[addItem]} 删除项：#{#p[removeItem]} 共同项：#{#p[commonItem]}", paramEditors = CollectionItemChangeEditorImpl.class,
                   collectionPrefix = "<", collectionPrefixIgnoreBlank = false,
                   collectionSuffix = ">", collectionSuffixIgnoreBlank = false,
                   collectionSeparator = "/", collectionSeparatorIgnoreBlank = false,
                   collectionLogForNull = "方块0", collectionLogForNullIgnoreBlank = false)
        class ListChangePojo {
            @OpLogField
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

    static class AbnormalComparator implements OpLogComparable {
        public AbnormalComparator() {}
        @Override
        public OpLogChangeType compare(Object before, Object after, Class<?> fieldClass) {
            return OpLogChangeType.NULL;
        }
    }

    @Test
    public void comparableTest() {

        @OpLogBean(logExp = "#{T(java.lang.String).valueOf(#changeType.isChanged())}", comparable = OpLogFieldTest.AbnormalComparator.class, effectiveType = OpLogChangeType.NULL)
        class CBean {
            @OpLogField
            int a;
        }
        CBean before = new CBean();
        before.a = 1;
        CBean after = new CBean();
        after.a = 2;

        String desireResult = String.valueOf(OpLogChangeType.NULL.isChanged());
        String actualResult = logBuilder.beanBuild(before, after, CBean.class).getText();
        Assertions.assertEquals(desireResult, actualResult);
    }

    /**
     * 自带的类型转换测试
     */
    @Test
    public void transferTest() throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @OpLogBean(dateFormat = "yyyy-MM-dd HH:mm:ss", dateFormatIgnoreBlank = false, decimalFormat = "0.00", decimalFormatIgnoreBlank = false)
        class TransferTestPojo {
            @OpLogField
            Date date1;
            @OpLogField
            LocalDate date2;
            @OpLogField
            Long num1;
            @OpLogField
            Double num2;
            @OpLogField
            BigInteger num3;
            @OpLogField
            BigDecimal num4;
            @OpLogField
            double num5;
            @OpLogField
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
        after.num6 = 7.005f;

        // 注: date2是LocalDate只有日期，所以相等
        String desireResult = "date1: 2023-04-26 10:00:00->2023-05-03 10:00:00, num1: 114514.00->1919810.00, num2: 520.08->520.13, num3: 39.00->40.00, num4: 445.00->253.00, num6: 7.00->7.01";
        String actualResult = logBuilder.beanBuild(before, after, TransferTestPojo.class).getText();
        Assertions.assertEquals(desireResult, actualResult);
    }

    /**
     * 内嵌Bean测试
     */
    @Test
    public void insideBeanTest() {
        class Parts {
            @OpLogField
            String mainEntry;
            @OpLogField
            List<String> secondaryEntry;
        }

        @OpLogBean
        class Remains {
            @OpLogInside
            Parts head;
            @OpLogInside(concatWhenSubLogIsBlank = OpLogBoolean.FALSE)
            Parts hand;
            @OpLogInside(concatWhenSubLogIsBlank = OpLogBoolean.TRUE)
            Parts body;
        }

        Remains before = new Remains();
        Parts head = new Parts();
        head.mainEntry = "main";
        head.secondaryEntry = Arrays.asList("second", "third");
        before.head = head;

        String desireResult = "mainEntry: main->null, secondaryEntry: [second,third]->null, ";
        String actualResult = logBuilder.beanBuild(before, new Remains(), Remains.class).getText();
        Assertions.assertEquals(desireResult, actualResult);
    }
}
