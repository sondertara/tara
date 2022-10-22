[![Build Status](https://travis-ci.org/sondertara/tara.svg?branch=master)](https://travis-ci.org/sondertara/tara)
![Java](https://img.shields.io/badge/Java-%5E1.8-brightgreen)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/sondertara/tara)
![Maven Central](https://img.shields.io/maven-central/v/com.sondertara/tara)

中文 | [English](README_en.md)

Tara是一个纯java项目,包括常用util工具类和excel处理两个模块。

> **System Requirements:** Language: Java 8+

# **common-tara**

通用工具包,包括常用的工具类,比如Bean 拷贝、时间处理、集合处理、IO操作、反射处理等常用工具

## **Import to project**

- Maven Project

```xml

<dependency>
    <groupId>com.sondertara</groupId>
    <artifactId>common-tara</artifactId>
    <version>1.0.2</version>
</dependency>
```

- Gradle project

```groovy

```

## **Features Induction**

### ***BeanUtils***

`BeanUtils` 是一个轻量级且高性能的JavaBean复制框架，支持拷贝不同类型和嵌套属性的自动拷贝

#### 1.属性类型一致对象拷贝

属性类型对象间拷贝,性能和`Spring BeanUtils`相当(稍微快一点点~),循环多次拷贝，同其他框架对比的基准测试如下:

![](example/result/same-benchmark.png)

#### 2.属性类型不同嵌套对象拷贝

属性类型不一致时,有些框架不支持该特性，但是`Tara BeanUtils`完全支持，并且有较好的性能表现。 基准测试如下:

![](example/result/differ-benchmark.png)

**Apache BeanUtils**: 运行异常

**Spring BeanUtils**: 属性类型不同时值会丢失,当访问嵌套属性时,抛出`ClassCaseException`

**Hutool**: 性能较弱

**Dozer**: 性能稍好

**Tara BeanUtils**: 和原生操作同一个量级

所有基准测试源码存放于[JMH Test](example/src/main/java/benchmark),测试结果存放于[JMH Result](example/result),

# ***excel-tara***

灵活且高性能Excel处理框架,支多种方式导入和导出Excel

## ***Import to project***

- Maven Project

```xml

<dependency>
    <groupId>com.sondertara</groupId>
    <artifactId>excel-tara</artifactId>
    <version>1.0.2</version>
</dependency>
```

- Gradle project

```groovy

```

## ***Features Induction***

- [X] 导出支持注解导出、简易导出和读取模板导出
- [x] 导出支持自动分Sheet,列宽自适应
- [x] 注解导出支持自定义样式,间隙条纹,自定义宽高
- [X] 导入支持注解导入和直接读取Excel中的数据
- [X] 注解导入支持值转换和数据校验
- [X] 直接导入支持流式读取

### ***Excel Writer***

`Tara Excel` 支持注解导出、快捷导出和模板导出三种方式

#### **1.注解导出**

注解导出支持直接传入导出对象List和实现导出对象分页查询接口两个方式

- `@ExcelExport`: 对应Excel中的Sheet,支持导出多个不同数据的Sheet

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelExport {

    /**
     * the multiple sheet order, smaller is parsed earlier
     * 顺序（值越小，越靠前）
     *
     * @return order
     */
    int order() default 0;

    /**
     * The sheet name
     * Sheet名称
     *
     * @return sheet name
     */
    String sheetName() default "数据";

    /**
     * the max row of one sheet,excluding the title row
     * 每个Sheet页允许的最大条数（用于分页）
     *
     * @return the max row of one sheet
     */
    int maxRowsPerSheet() default 60000;

    /**
     * is open the row strip
     * 是否开启条纹
     *
     * @return is open the row strip
     */
    boolean rowStriped() default true;

    /**
     * the row strip color
     * 条纹颜色
     *
     * @return the color
     */
    String rowStripeColor() default "E2EFDA";

    /**
     * the title row height
     * 标题行高度
     *
     * @return the title row height
     */
    int titleRowHeight() default 20;

    /**
     * the data row height
     * 数据行高度
     *
     * @return the data row height
     */
    int dataRowHeight() default 20;

    /**
     * the bind type
     * If {@link ExcelColBindType#COL_INDEX} the value {@link ExcelExportField#colIndex()} must be set.
     * If {@link ExcelColBindType#ORDER} the colIndex is the order field definition order.
     *
     * @return whether enable colIndex
     * @see ExcelColBindType
     */
    ExcelColBindType bindType() default ExcelColBindType.ORDER;

    /**
     * is open column auto width
     * this is higher priority than {@link ExcelExportField#autoWidth()}
     * 是否自动调整宽度
     *
     * @return is open all column auto width
     */
    boolean autoWidth() default false;
    
}
```

- `@ExcelExportField`: 对应Sheet中的列,支持每一列灵活定义

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelExportField {
    /**
     * column name  alias
     *
     * @return column name
     */
    @AliasFor("colName")
    String value() default "";

    /**
     * 标题
     * column name
     *
     * @return column name
     */
    @AliasFor("value")
    String colName() default "";

    /**
     * the colIndex ,begin is 1
     * 列索引（从1开始）
     *
     * @return the colIndex
     */
    int colIndex() default -1;

    /**
     * default cell value
     * 默认单元格值
     */
    String defaultCellValue() default "";

    /**
     * 列类型
     * the cell type
     *
     * @return the CellType
     * @see com.sondertara.excel.utils.ExcelFieldUtils#setCellValue(Cell, Object, Field, ExcelExportField, ExcelDefaultWriterResolver)
     */
    CellType cellType() default CellType.STRING;

    /**
     * custom data format
     * 数据格式
     * <p>
     * eg: @ExcelDataFormat("yyyy/MM/dd")
     *
     * @return the data format
     */
    ExcelDataFormat dataFormat() default @ExcelDataFormat;

    /**
     * data cell style
     * 数据样式
     *
     * @return the style class {@link CellStyleBuilder} subclass
     */
    Class<?> dataCellStyleBuilder() default DefaultDataCellStyleBuilder.class;

    /**
     * the title cell style
     * 标题样式
     *
     * @return the style class {@link CellStyleBuilder} subclass
     */
    Class<?> titleCellStyleBuilder() default DefaultTitleCellStyleBuilder.class;

    /**
     * is open auto width
     * 是否自动调整宽度
     *
     * @return
     */
    boolean autoWidth() default false;

    /**
     * the custom column width,default is 16
     * 自定义cell宽度
     *
     * @return the custom column width
     */
    int colWidth() default Constants.DEFAULT_COL_WIDTH;
}
```

1) 使用样例

为对象添加`ExportField`注解，导出列添加`ExcelExportField`注解,例如导出假期和用户数据到同一个Excel

- 假期数据对应的JavaBean:
```java
@ExcelExport(sheetName = "节假日")
public class HolidayCfg {

    @ExcelExportField(colName = "节假日日期", colIndex = 1, dataFormat = @ExcelDataFormat("yyyy-MM-dd HH:mm:ss"))
    private Date holidayDate;

    @ExcelExportField(colName = "节假日名称", colIndex = 2)
    private String holidayName;

    @ExcelKVConvert(kvMap = {"是=0", "否=1"})
    @ExcelExportField(colName = "是否上班", colIndex = 3)
    private String isWork;

    @ExcelExportField(colName = "备注", colIndex = 4)
    private String remark;
}
```
- 用户数据对应的JavaBean:

```java

@ExcelExport(sheetName = "用户数据")
public class User {
    @ExcelExportField(colIndex = 2, colName = "年龄")
    private Integer age;
    
    @ExcelExportField(colIndex = 1, colName = "姓名")
    private String name;
    
    @ExcelExportField(colIndex = 3, colName = "生日", dataFormat = @ExcelDataFormat("yyyy-MM-dd"))
    private Date birth;
    
    @ExcelExportField(colIndex = 4, colName = "体重", dataFormat = @ExcelDataFormat("0.00"))
    private Double height;
}
```
使用`ExcelBeanWriter`导出Excel文件

```java
import java.util.ArrayList;

public class ExcelBeanWriteTest {
    /**
     * 通过查询的list导出
     * Export by list
     */
    @Test
    public void testWriteMultipleSheetByData() {
        //Query data to list
        List<HolidayCfg> holidayCfgList = new ArrayList<>();
        List<User> users = new ArrayList<>();
        // Export to OutputStream
        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "export_multiple_sheet_data.xlsx"))) {
            ExcelBeanWriter.fromData().addData(holidayCfgList).addData(users).then().to(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpServletResponse response=null;
        // Export to HttpServletResponse
        ExcelBeanWriter.fromData().addData(holidayCfgList).addData(users).then().to(response,"Export_data");
    }

    /**
     * 通过分页查询导出
     * Export by pagination query function
     */
    @Test
    public void testSheetByQuery() {
      
        // Export to OutputStream
        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "export_multiple_sheet_data.xlsx"))) {
            ExcelBeanWriter.fromData().addData(index->{
                // query data start index 0,page size is 1000,total number is 10000
                Lis<HolidayCfg> holidayCfgList=new ArrayList<>();
                return PageResult.of(holidayCfgList).pagination(index,1000).total(10000L);
            }).then().to(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpServletResponse response=null;
        // Export to HttpServletResponse
        ExcelBeanWriter.fromData().addData(holidayCfgList).addData(users).then().to(response,"Export_data");
    }
}

```
详情请参考[ExcelBeanWriteTest](example/src/main/java/com/sondertara/excel/ExcelBeanWriteTest.java)

#### **2.快捷导出**

快捷导出支持传入List对象和分页查询接口导出,样例

```java
ExcelSimpleWriter.create().sheetName("Sheet").header(titles).addData(List<Object[]> dataList).to();
ExcelSimpleWriter.create().sheetName("Sheet").header(titles).addData(ExportFunction function).to();
```

详情请参考[ExcelSimpleWriteTest](example/src/main/java/com/sondertara/excel/ExcelSimpleWriteTest.java)



# ***Contact***

My email :814494432@qq.com / xhhuangchn@outlook.com

# ***JetBrains Support***

We graciously acknowledge the support of [JetBrains](https://www.jetbrains.com/community/opensource/#support?from=tara)
which enables us to use the professional version
of IntelliJ IDEA for developing **Friendly**.

<a href='https://www.jetbrains.com/community/opensource/#support?from=tara'>
   <img alt='' src='https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.png' width=200 height=200 />
</a>
