# BeanLog使用文档

本工具适用于java，能够基于注解根据Bean的属性值的变化一键生成字段变化日志，以便于生成操作日志并管理

适用版本：0.1.0
> 文档编辑中

## 痛点

我们假定一个场景：某网站存放了一些用户的个人信息，每个用户的个人信息存放在下面的实体中：

```java
@Data
public class UserInfo {
    private String nickName;
    private Integer age;
    private String phone;
}
```

若该网站需要记录用户每次更新的操作日志，那么则需要这么写代码：

```java
public String createChangeLog(UserInfo before, UserInfo after) {
    StringBuilder sb = new StringBuilder();
    // 记录nickName的变化操作
    if (!Objects.equals(before.getNickName(), after.getNickName())) {
        sb.append("昵称由").append(before.getNickName()).append("变为").append(after.getNickName()).append(" ");
    }
    // 记录age的变化操作
    if (!Objects.equals(before.getAge(), after.getAge())) {
        sb.append("年龄由").append(before.getAge()).append("变为").append(after.getAge()).append(" ");
    }
    // 记录phone的变化
    if (!Objects.equals(before.getPhone(), after.getPhone())) {
        sb.append("电话号码由").append(before.getPhone()).append("变为").append(after.getPhone()).append(" ");
    }
    return sb.toString();
}
```

这样的写法不仅啰嗦，维护也相当不便。即便有反射遍历、模板文件填充等其他方式，也存在学习成本过高的问题。

## 快速开始

### 在java中使用

引入maven依赖
```xml
<dependency>
    <groupId>io.github.opcoral</groupId>
    <artifactId>beanlog-core</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

定义实体

```java
import io.github.opcoral.beanlog.annotation.BeanLogField;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    @BeanLogField(name = "昵称")
    private String nickName;
    @BeanLogField(name = "年龄")
    private Integer age;
    @BeanLogField(name = "电话")
    private String phone;
}
```

生成日志
```java
import io.github.opcoral.beanlog.entity.result.BeanLogResult;
import io.github.opcoral.beanlog.impl.builder.BeanLogBuilder;
import io.github.opcoral.beanlog.impl.builder.BeanLogBuilderImpl;

// ...

UserInfo before = new UserInfo("小王", 8, "18888888888");
UserInfo after = new UserInfo("王总", 8, "19999999999");

BeanLogResult logResult = new BeanLogBuilderImpl().beanBuild(before, after, UserInfo.class);
System.out.println(logResult.getText());
```

控制台输出结果

```text
昵称: 小王->王总, 电话: 18888888888->19999999999
```

因为年龄是相等的，因此日志中不会包含年龄的变化信息。

### Spring Boot项目引入BeanLogBuilder

```java
import io.github.opcoral.beanlog.core.BeanLogBuilder;
import io.github.opcoral.beanlog.impl.builder.BeanLogBuilderImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class BeanLogConfig {

    @Resource
    private ApplicationContext context;

    @Bean
    public BeanLogBuilder BeanLogBuilder() {
        // 定义builder
        BeanLogBuilder builder = new BeanLogBuilderImpl();

        // 初始化
        builder.init();

        // 定义objectLoader
        builder.setObjectLoader(aClass -> context.getBean(aClass));

        // 注册
        return builder;
    }
}
```

## 进阶使用

### 日志的组成结构

日志的结构如下:

> [bean前缀] [属性1日志], [属性2日志], ... , [属性n日志] [bean后缀]

其中每个属性的内部日志，由logExp属性的SpEl表达式生成。

举个例子，定义一个实体 PlayerInfo，结构如下：
```java
@Data
@BeanLogBean(beanPrefix = "用户操作日志：[", beanSuffix = "]", logExp = "#{#name}由#{#before}改为#{#after}", separator = "，")
public class PlayerInfo {

    @BeanLogField(name = "昵称")
    private String nickName;

    @BeanLogField(name = "杀死的史莱姆数量", prefix = "约", suffix = "只")
    private Integer killSlimeNum;

    @BeanLogField(name = "改动备注", logExp = "#{#name}：#{#after}")
    private String remark;
}
```

写一段示例代码表示属性的变化。这段代码表示用户将昵称“小王”改成了“老王”，杀死的史莱姆数量从0改为300，添加了改动备注“不知不觉就升到了满级”
```java
public void logStructTest() {
    // 修改前
    PlayerInfo before = new PlayerInfo();
    before.setNickName("小王");
    before.setKillSlimeNum(0);
    before.setRemark("改之前的备注");
    // 修改后
    PlayerInfo after = new PlayerInfo();
    after.setNickName("老王");
    after.setKillSlimeNum(300);
    after.setRemark("不知不觉就升到了满级");
    // 生成更新日志
    BeanLogResult result = new BeanLogBuilderImpl().beanBuild(before, after, PlayerInfo.class);
    System.out.println(result.getText());
}
```
输出结果
```text
用户操作日志：[昵称由小王改为老王，杀死的史莱姆数量由约0只改为约300只，改动备注：不知不觉就升到了满级]
```
将日志进行拆分如下：

|日志内容|属性/配置|SpEl表达式|名称|改动前|改动后|成员前缀|成员后缀|
|:---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
|用户操作日志：[|日志前缀(beanPrefix)|-|-|-|-|-|-|
|昵称由小王改为老王|nickName|#{#name}由#{#before}改为#{#after}|昵称|小王|老王|-|-|
|杀死的史莱姆数量由约0只改为约300只|killSlimeNum|由#{#before}改为#{#after}|杀死的史莱姆数量|0|300|约|只|
|改动备注：不知不觉就升到了满级|remark|#{#name}：#{#after}|改动备注|改之前的备注|不知不觉就升到了满级|-|-|
|]|日志后缀(beanSuffix)|-|-|-|-|-|-|

### `BeanLogConfig`详解（含`@BeanLogBean`, `@BeanLogField`, `@BeanLogBeanInside`）

#### 1. `BeanLogConfig`各个字段的含义

`BeanLogConfig`顾名思义是日志生成时的配置信息类。整个日志生成过程中，几乎所有的配置信息都存放在`BeanLogConfig`中。

`BeanLogConfig`每个字段的含义如下表。

|字段名|字段说明|全局默认|设置类型|
|:---:|:---------|:---|:---:|
|name|记录到操作日志中的属性名称<br>若为null则使用变量的field名称<br>|`null`|`String`|
|logExp|单个field生成的提示文本<br>使用SpEl表达式填充<br>|`{#name}: #{#before}->#{#after}`|`String`|
|effectiveType|日志生效的变更类型<br>在这个集合中未包含的日志不会记录|`{BeanLogChangeType.UPDATE, BeanLogChangeType.INSERT, BeanLogChangeType.DELETE}`|`BeanLogChangeType[]`|
|before|为before和after添加的前缀<br>不支持SpEl表达式<br>这个值会填充到SpEl表达式的#{#before}变量中|`""`|`String`|
|after|为before和after添加的后缀<br>不支持SpEl表达式<br>这个值会填充到SpEl表达式的#{#after}变量中|`""`|`String`|
|logForNull|如果before/after为null，日志中显示的字符串<br>这个值会忽略prefix和suffix配置的前后缀信息|`"null"`|`String`|
|treatBlankStringAsNull|如果CharSequence/String类型的属性/方法是空串，则在生成字符串时将值视为null<br>空串指没有字符或仅包含空白字符(含空格、换行符等)<br>在SpEl表达式的解析时，beforeObj/afterObj会保持原值，不受这个配置影响|`false`|`Boolean`|
|param|自定义参数<br>解析SpEl表达式时的参数`p`|`new HashMap<>()`|`Map<String, Object>`|
|beanPrefix|生成的日志的前缀<br>对于最外层的大Bean，这即为全局前缀<br>对于内嵌的Bean，会生成局部的前缀<br>若启用SpEl表达式，作用域为bean作用域|`""`|`String`|
|beanPrefixEnableSpEl|全局前缀是否启用SpEl表达式|`false`|`Boolean`|
|beanSuffix|生成的日志的后缀<br>对于最外层的大Bean，这即为全局后缀<br>对于内嵌的Bean，会生成局部的后缀<br>若启用SpEl表达式，作用域为bean作用域|`""`|`String`|
|beanSuffixEnableSpEl|全局后缀是否启用SpEl表达式|`false`|`Boolean`|
|createBeanPrefixWhenLogIsEmpty|在Bean的log内部的内容为空的情况下，是否生成前缀|`false`|`Boolean`|
|createBeanSuffixWhenLogIsEmpty|在Bean的log内部的内容为空的情况下，是否生成后缀|`false`|`Boolean`|
|concatWhenSubLogIsBlank|内嵌的Bean生成的日志为空串时，主bean是否拼接日志|`false`|`Boolean`|
|separator|分隔符<br>若启用SpEl表达式，作用域为bean作用域|`", "`|`String`|
|separatorEnableSpEl|分隔符是否启用SpEl表达式|`false`|`Boolean`|
|converterMap|转换器Map,提供每种类型的转换方法<br>合并配置时会合并map，键相同时，后者会覆盖前者<br>未在这里定义转换器的类型，会调用toString()方法生成字符串|`Date : dateConverter`<br>`Calendar : dateConverter`<br>`LocalDate : dateConverter`<br>`LocalDateTime : dateConverter`<br>`ZonedDateTime : dateConverter`<br>`Instant : dateConverter`<br><br>`Number : decimalConverter`<br>`byte : decimalConverter`<br>`short : decimalConverter`<br>`int : decimalConverter`<br>`long : decimalConverter`<br>`float : decimalConverter`<br>`double : decimalConverter`<br><br>`Collection : collectionConverter`|`Map<Class<?>, BeanLogStringConverter>`|
|paramEditors|参数编辑信息列表<br>其中BeanLogParamEditor是函数式接口<br>在生成配置之前，会依序调用List中的所有对象的接口执行<br>合并配置时会合并List，以配置的优先级从低到高按顺序执行|`new ArrayList<>()`|`List<BeanLogParamEditor>`|
|comparable|比较器|`{DefaultComparableImpl.class}`|`BeanLogComparable`|
|groups|组别标识<br>标识这个配置所属的组别<br>若未包含组别，则会出现在所有的组别中|`{}`|`String[]`|
|currentGroup|当前组别，配合groups一起使用<br>如果不为空，最后生成的日志中如果currentGroup 不在 groups中，则不会生成日志|`null`|`String`|
|dateFormat|日期类型的格式化输出格式<br>在转换步骤中会被`dateConverter`使用<br>与`DateFormat`的表达式规则相同|`"yyyy-MM-dd HH:mm:ss"`|`String`|
|decimalFormat|数字类型的小数位数保留<br>如果为null，不限制小数位数的保留<br>在转换步骤中会被`decimalConverter`使用<br>与`DecimalFormat`的表达式规则相同|`null`|`String`|
|collectionPrefix|`Collection`转为字符串的前缀<br>转换步骤会被`collectionConverter`使用|`"["`|`String`|
|collectionSuffix|`Collection`转为字符串的后缀<br>转换步骤会被`collectionConverter`使用|`"]"`|`String`|
|collectionSeparator|`Collection`转为字符串的分隔符<br>转换步骤会被`collectionConverter`使用|`","`|`String`|
|collectionLogForNull|`Collection`中null值的替换<br>如果是整个列表对象为null，则使用上文的`logForNull`属性|`"null"`|`String`|

#### 2. `@BeanLogBean`, `@BeanLogField`, `@BeanLogBeanInside`与`BeanLogConfig`的对应关系

`@BeanLogBean`,`@BeanLogField`,`@BeanLogBeanInside`会在生成日志的时候转换为对应的`BeanLogConfig`类对象，

|`BeanLogConfig`<br>包含属性|`@BeanLogBean`<br>是否支持|`@BeanLogField`<br>是否支持|`@BeanLogBeanInside`<br>是否支持|
|:---:|:---:|:---:|:---:|

> 文档编辑中