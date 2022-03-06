[![Build Status](https://travis-ci.org/sondertara/tara.svg?branch=master)](https://travis-ci.org/sondertara/tara)
![Java](https://sondertara.github.io/assets/java8.svg)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/sondertara/tara)
![Maven Central](https://img.shields.io/maven-central/v/com.sondertara/tara)

## Tara

Tara是一个纯java项目,包括常用util工具类和excel处理两个模块

## System Requirements

- Language: Java 8
- Environment: MacOS, Windows,Linux
-

## Quick Start

### [excel-tara]

高性能excel处理工具

- 支持导入大批量数据处理
- 异步多线程导出数据
- 生成导入模板

#### 引入maven依赖,version为上方maven仓库中版本

```xml

<dependency>
    <groupId>com.sondertara</groupId>
    <artifactId>excel-tara</artifactId>
    <version>${version}</version>
</dependency>
```

#### 1.导出示例

##### 1)添加导出注解 `@ExportField`

```java

@Data
public class ExportVO {

    @ExcelExportField(columnName = "姓名")
    private String name;
    @ExcelExportField(columnName = "年龄")
    private Integer age;
    @ExcelExportField(columnName = "住址")
    private String address;
}
```

##### 2)同步导出

🌈🌈当数据量过大时，会长时间阻塞,推荐使用异步导出方案

``` java
 /**
     * 导出Demo
     *
     * @ExportField写在 ExportVO的属性字段上
     * <p>
     * <p>
     * ExportVO是标注注解的类,Excel映射的导出类，需要自己定义
     * ParamEntity是查询的参数对象，继承PageQueryParam 需要自己定义,设置起止页和分页大小
     * ResultEntity是分页查询到的结果List内部元素，需要自己定义
     * <p>
     * ExportVO可以和ResultEntity使用同一个对象,即直接在查询的结果对象上标注注解(建议使用两个对象, 实现解耦)
     * <p>
     * pageQuery方法需要自行实现, 即导出Excel的数据来源, 根据查询条件和当前页数和每页条数进行数据层查询, 当返回List的条数为NULL或者小于DEFAULT_PAGE_SIZE(每页条数)时, 将视为查询结束, 反之则会发生死循环
     * <p>
     * convert方法需要自行实现, 参数就是查询出来的list中的每个元素引用, 可以对对象属性的转换或者对象的转换, 但是必须返回标注注解的对象
     */
    @RequestMapping("/exportDemo")
    public void exportResponse(@RequestParam(value = "fieldValues") String fieldValues, HttpServletResponse httpServletResponse) {
        ParamEntity param = JSON.parseObject(fieldValues, ParamEntity.class);
        ExcelExpoerTara.of(ExportVO.class).query(param,  new ExportFunction<ParamEntity, ResultEntity>() {
                    /**
                     * @param queryQaram 查询条件对象
                     * @param pageNum    当前页数,从1开始
                     * @param pageSize   每页条数,默认2000
                     * @return
                     */
                    @Override
                    public List<ResultEntity> pageQuery(ParamEntity queryQaram, int pageNum) {

                        //1.将pageNum和pageSize传入使用本组件的开发者自己项目的分页逻辑中
                        //2.调用自定义的分页查询方法
                        List<ResultEntity> result = null；
                        return result;
                    }

                    /**
                     * 将查询出来的每条数据进行转换
                     *
                     * @param o
                     */
                    @Override
                    public ExportVO convert(ResultEntity o) {
                        //自定义的转换逻辑
                        return new ExportVO();
                    }
                }).export("测试文件",httpServletResponse);
    }
```

#### 3)异步导出

该方案会异步多线程生成csv格式的Excel文件，并返回文件所在的路径.

```java
public class ExceExportDemo {
    private static final Logger logger = LoggerFactory.getLogger(ExceExportDemo.class);

    public void exportCsvDemo() {
        PageQueryParam query = PageQueryParam.builder().build();
        String path = ExcelExportTara.of(UserInfoVo.class).query(query, pageNo -> {
            // query list data from db
            List<UserDTO> list = new ArrayList<>(200);
            for (int i = 0; i < 200; i++) {
                UserDTO userDTO = new UserDTO();

                userDTO.setA(i);
                userDTO.setN(pageNo + "测试姓名" + i);
                userDTO.setD("测试地址" + i);
                list.add(userDTO);

                if (pageNo == 5 && i == 150) {
                    break;
                }
            }
            atomicInteger.getAndAdd(list.size());

            // convert to target data list
            return list.stream().map(u -> {
                UserInfoVo userInfoVo = new UserInfoVo();
                userInfoVo.setAddress(u.getD());
                userInfoVo.setAge(u.getA());
                userInfoVo.setName(u.getN());
                return userInfoVo;
            }).collect(Collectors.toList());

        }).exportCsv("Excel-Test");
        logger.info("path:{}", path);
        logger.info("data list size:{}", atomicInteger.get());
        //FileUtils.remove(path);
    }
}
```

#### 2.导入示例

##### 1)添加导入注解 `@ImportField`

```java

@Data
public class ImportParam implements Serializable {
    @ImportField(index = 1)
    private String userName;

    @ImportField(index = 3)
    private Date orderTime;

    @ImportField(index = 6, required = true)
    private String userPhone;

    @ImportField(index = 8)
    private Date commitTime;

    @ImportField(index = 9, range = {"100", "500"})
    private BigDecimal amount;
}
```

##### 2)导入demo

```java

public class ExcelmportDemo {
    private static final Logger logger = LoggerFactory.getLogger(ExcelmportDemo.class);

    public void importTest(String filePath) throws Exception {

        File file = new File(filePath);
        final FileInputStream inputStream = new FileInputStream(file);

        ExcelTara.builder(inputStream, ImportParam.class)
                .importExcel(true, new ImportFunction<ImportParam>() {

                    /**
                     * @param sheetIndex 当前执行的Sheet的索引, 从1开始
                     * @param rowIndex   当前执行的行数, 从1开始
                     * @param param      Excel行数据的实体
                     */
                    @Override
                    public void onProcess(int sheetIndex, int rowIndex, ImportParam param) {
                        logger.info("sheet[{}],第{}行，解析数据为:{}", sheetIndex, rowIndex, JSON.toJSONString(param));
                        try {
                            //  handleImportData(param);
                        } catch (Exception e) {
                            logger.error(" handle record error", e);
                        }
                    }

                    /**
                     * @param errorEntity 错误信息实体
                     */
                    @Override
                    public void onError(ErrorEntity errorEntity) {
                        //将每条数据非空和正则校验后的错误信息errorEntity进行自定义处理

                        logger.info(errorEntity.toString());
                        ExcelTaraTool.addErrorEntity(errorEntity);
                    }
                });
        //获取导入错误数据
        List<List<String>> records = ExcelTaraTool.getErrorEntityRecords();
        //生成cvs
        ExcelTaraTool.writeRecords("import_error.csv", records);
        //获取file对象
        File workFile = ExcelTaraTool.getWorkFile("import_error.csv");
    }
}

```

### TODO

- `ExcelExportField` 注解支持样式
- 模板导出Excel
- 简易导入导出数据
- 代码注释和性能优化

## Contact

My email :814494432@qq.com / xhhuangchn@outlook.com


