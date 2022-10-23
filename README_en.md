# **Tara**

[![Build Status](https://travis-ci.org/sondertara/tara.svg?branch=master)](https://travis-ci.org/sondertara/tara)
![Java](https://img.shields.io/badge/Java-%5E1.8-brightgreen)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/sondertara/tara)
![Maven Central](https://img.shields.io/maven-central/v/com.sondertara/tara)

English | [中文](README.md)

Tara is a pure Java library that provides convenient kit and all-in-one Excel kit.

> **System Requirements:** Language: Java 8+

## 💎**common-tara**

Commons utils for Java which supports High performance Bean Copier,crypto,collections,system-command and reflection
library,etc.

### 🍵**Import to project**

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
implementation 'com.sondertara:common-tara:1.0.2'
```

### :eight_spoked_asterisk:**Features Induction**

- [X] Lightweight but high-performance Bean Copier.
- [x] Frequently used collections,IO,reflection utils.
- [x] ID generators.
- [X] Crypto library.
- [X] Useful regex pool.

#### :triangular_flag_on_post:***BeanUtils***

`BeanUtils` is a high performance JavaBean copier that supports automatic copying between different property and nested property.

##### 1.Consistent property type copy

When the property type is same, it is even a little faster than `Spring BeanUtils`.Here is loops copy benchmark test results:

![""](example/result/same-benchmark.png)

##### 2.Different property type copy

When the property type is different,some frameworks do not support this feature,but `Tara BeanUtils` does,and it is in the same order of magnitude as native operations.
Here is the benchmark results:

![""](example/result/differ-benchmark.png)

With the above test,we get the following conclusions

**Apache BeanUtils**: Get exception when running.

**Spring BeanUtils**: Values are lost when the property type is different,and when accessing nested properties throws `ClassCaseException`.

**Hutool**: Worst performance.

**Dozer**: Slightly better performance.

**Tara BeanUtils**: Close to native operation performance.

All benchmark test source code are here [JMH Test](example/src/main/java/benchmark),the results are here [JMH Result](example/result),too.

## 💎**excel-tara**

Flexible Excel library based on `Apache POI`,provides convenient Excel reading-writing capabilities.

### 🍵Import to project

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

### :eight_spoked_asterisk:**Features Induction**

- [X] Export with three ways: Annotation writer,Simple-Direct writer and Template-Based writer.
- [x] Export supports automatic sheet splitting and column width adaptation.
- [x] Annotation writer supports complex header,custom cell styles,row gap stripes and every column customization.
- [X] Import with two ways: Annotation reader and Simple-Direct reader.
- [X] Annotation writer supports value conversion and data validation.
- [X] Simple-Direct reader supports streaming reads.

All example test can find at here [Excel-Test example](example/src/main/java/com/sondertara/excel).

#### :triangular_flag_on_post:**Excel Writer**

`Tara Excel` support three export features: Annotation Writer,Simple-Direct Writer and Template-Based Writer.

##### **1.Annotation Writer(ExcelBeanWriter)**

Annotation Writer provides two ways: directly passing in the export object List and implementing the export object paged query interface.

- `@ExcelExport`: Corresponding to the Sheet in Excel, it supports exporting multiple Sheets with different data.

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

- `@ExcelExportField`: Corresponding to the columns in the Sheet, it supports flexible definition of each column.

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

:balloon:**Usage example**

Add `@ExportField` annotation to the object,then add `@ExcelExportField` to the export columns. Here's an example of exporting vacation data and users data:

- Vacation JavaBean:

```java

/**
 * The colindex of the export column is not effective because the bindType()  default is the order of properties definition,
 */
@ExcelExport(sheetName = "节假日")
public class HolidayCfg {
    /**
     * ExcelDataFormat will display the value with the specified format.
     */
    @ExcelExportField(colName = "节假日日期", colIndex = 1, dataFormat = @ExcelDataFormat("yyyy-MM-dd HH:mm:ss"))
    private Date holidayDate;

    @ExcelExportField(colName = "节假日名称", colIndex = 2)
    private String holidayName;

    /**
     * ExcelKVConvert will convert the the property value to the map value when this property value equals the map key
     */
    @ExcelKVConvert(kvMap = {"0=是", "1=否"})
    @ExcelExportField(colName = "是否上班", colIndex = 3)
    private String isWork;

    @ExcelExportField(colName = "备注", colIndex = 4)
    private String remark;
}
```

- User JavaBean:

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

`ExcelBeanWriter` to export Excel.

```java
import java.util.ArrayList;

public class ExcelBeanWriteTest {
    /**
     * 通过查询的list导出
     * Export by list directly
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
        HttpServletResponse response = null;
        // Export to HttpServletResponse
        ExcelBeanWriter.fromData().addData(holidayCfgList).addData(users).then().to(response, "Export_data");
    }

    /**
     * 通过分页查询导出
     * Export by pagination query function which is based on  Producer-Consumer design pattern.
     */
    @Test
    public void testSheetByQuery() {

        // Export to OutputStream
        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "export_multiple_sheet_data.xlsx"))) {
            ExcelBeanWriter.fromData().addData(index -> {
                // query data start index 0,page size is 1000,total number is 10000
                Lis<HolidayCfg> holidayCfgList = new ArrayList<>();
                return PageResult.of(holidayCfgList).pagination(index, 1000).total(10000L);
            }).then().to(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpServletResponse response = null;
        // Export to HttpServletResponse
        ExcelBeanWriter.fromData().addData(holidayCfgList).addData(users).then().to(response, "Export_data");
    }
}
```

Please refer to it for details [ExcelBeanWriteTest](example/src/main/java/com/sondertara/excel/ExcelBeanWriteTest.java)

##### **2.Simple-Direct Writer(ExcelSimpleWriter)**

Simple-Direct Writer also provides two ways: directly passing in the export object List and implementing the export object paged query interface.
Usage example:

```java
ExcelSimpleWriter.create().sheetName("Sheet").header(titles).addData(List<Object[]>dataList).to();
        ExcelSimpleWriter.create().sheetName("Sheet").header(titles).addData(ExportFunction function).to();
```

Please refer to it for details [ExcelSimpleWriteTest](example/src/main/java/com/sondertara/excel/ExcelSimpleWriteTest.java)

#### 🚩**Excel Reader**

`Tara Excel` supports tws features: Annotation Reader and Simple-Direct Reader.

##### **1.Annotation Reader(ExcelBeanReader)**

- `@ExcelImport`: Corresponding to the Sheet in Excel,it can read the specified Sheet page.

```java
/**
 * @author huangxiaohu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelImport {

    /**
     * bind the sheet index of Excel,begin is  1
     * 绑定的sheet页（可多个, 从1开始）
     *
     * @return sheets
     */
    int[] sheetIndex() default {1};

    /**
     * point the data row num start,begin is 1
     * 起始数据行(从1开始)
     *
     * @return the data row index
     */
    int firstDataRow() default 2;


    /**
     * 数据绑定类型
     * data bind type,default order is the field definition order is class
     * If {@link ExcelColBindType#COL_INDEX} the value {@link ExcelImportField#colIndex()} must be set.
     * If {@link ExcelColBindType#ORDER} the colIndex is the order field definition order.
     * If {@link ExcelColBindType#TITLE} the value {@link ExcelImportField#title()} must be set,and colIndex will calculate by the title in Excel
     *
     * @return the type of data bind
     * @see ExcelColBindType
     */
    ExcelColBindType bindType() default ExcelColBindType.ORDER;

}
```

- `@ExcelImportField`: Corresponding to the columns in the Sheet,it provides column binding and data validation.

```java
/**
 * @author huangxiaohu
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelImportField {

    /**
     * the col index,begin is 1
     * it takes effect only when {@link ExcelImport#bindType()} is {@link com.sondertara.excel.enums.ExcelColBindType#COL_INDEX}
     * 列索引(从1开始)
     *
     * @return the bind col index
     */
    int colIndex() default -1;

    /**
     * all empty cell
     * 是否允许空值
     *
     * @return allow empty cell value
     */
    boolean allowBlank() default true;

    /**
     * date format
     * 日期格式
     *
     * @return the data format pattern
     */
    String dateFormat() default DatePattern.NORM_DATETIME_PATTERN;

    /**
     * the column title
     * 列标题
     * if {@link ExcelImport#bindType()} is {@link com.sondertara.excel.enums.ExcelColBindType#TITLE} this value must be set to the Excel title row cell
     *
     * @return the title
     */
    String title() default "";
}
```

:balloon:**Usage example**

Add `@ExcelImport` annotation to object,then add`@ExcelImportField` annotation to bind column of sheet.Here`s an example of reading the vacation data from Excel.

- Vacation JavaBean:

```java
import com.sondertara.excel.enums.ExcelColBindType;
/**
 * bindType is title means auto-association column of sheet with title,the colIndex is not effective unless set bindType to {@link ExcelColBindType#COL_INDEX}
 */
@Data
@ExcelImport(sheetIndex = 1, firstDataRow = 2, bindType = ExcelColBindType.TITLE)
public class HolidayCfg {

    @ExcelImportField(colIndex = 1, dateFormat = "yyyy-MM-dd", allowBlank = false, title = "节假日日期")
    private Date holidayDate;

    @ExcelImportField(colIndex = 2, allowBlank = false, title = "节假日名称")
    private String holidayName;

    @ExcelKVConvert(kvMap = {"是=0", "否=1"})
    @ExcelImportField(colIndex = 3, allowBlank = false, title = "是否上班")
    private String isWork;

    @ExcelImportField(colIndex = 4, title = "备注")
    private String remark;
}
```

`ExcelBeanReader`to read data in Excel :

```java
public class ExcelReaderTest {

    private static final String EXCEL_TEMPLATE_DIR = "excel-template/";// "excel-template/";

    /**
     * test the import annotation {@link com.sondertara.excel.meta.annotation.ExcelImport}
     *
     * @see ExcelKVConvert
     */
    @Test
    public void testAnnotation() {
        final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(EXCEL_TEMPLATE_DIR + "multi_sheet_data.xlsx");

        List<HolidayCfg> list = ExcelBeanReader.load(is).read(HolidayCfg.class);

        Assertions.assertEquals(1000, list.size());
        //test the ExcelKVConvert.
        boolean isConvert = "0".equals(list.get(0).getIsWork()) || "1".equals(list.get(0).getIsWork());
        Assertions.assertTrue(isConvert);
    }
}
```

##### **2.Simple-Direct(ExcelSimpleReader)**

Implement an XML parser that supports streaming reading of data in Excel.

:balloon:**Usage example**

```java
public class ExcelReaderTest {
    /**
     * Raw Excel parser, this is very faster
     *
     * @see ExcelSimpleReader
     */
    @Test
    public void testRaw() {
        final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(EXCEL_TEMPLATE_DIR + "duty_vacation.xlsx");
        try (ReadableWorkbook read = ExcelSimpleReader.load(is).read()) {
            //can use stream api too
            read.getSheets().forEach(sheet -> {
                try {
                    List<Row> rows = sheet.read();

                    for (int i = 1; i < rows.size(); i++) {

                        Row cells = rows.get(i);
                        Cell cell = cells.getCell(2);
                        if (i == 1) {
                            Assertions.assertEquals("2019-10-10", LocalDateTimeUtils.format(cell.asDate(), DatePattern.NORM_DATE_PATTERN));
                        }
                    }
                    for (Row row : rows) {
                        System.out.println(row);
                    }
                    Assertions.assertEquals(10, rows.size());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
```

Please refer to it for details [ExcelReaderTest](example/src/main/java/com/sondertara/excel/ExcelReaderTest.java)

## ☎️**Contact**

My email :814494432@qq.com / xhhuangchn@outlook.com

## 💓**Thanks**

### *JetBrains Support*

We graciously acknowledge the support of [JetBrains](https://www.jetbrains.com/community/opensource/#support?from=tara)
which enables us to use the professional version
of IntelliJ IDEA for developing **Friendly**.

<a href='https://www.jetbrains.com/community/opensource/#support?from=tara'>
   <img alt='' src='https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.png' width=150 height=150 />
</a>

### *Users*
- [dhatim](https://github.com/dhatim): Simple-Direct Reader is based on his awesome project <a href="https://github.com/dhatim/fastexcel">fastexcel</a>.

