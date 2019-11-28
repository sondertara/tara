[![Build Status](https://travis-ci.org/sondertara/tara.svg?branch=master)](https://travis-ci.org/sondertara/tara)
![Java](https://woolson.gitee.io/npmer-badge/Java-555555-1.8-44cc11-check-ffffff-square-gradient-shadow.svg)
![Maven Central](https://img.shields.io/maven-central/v/com.sondertara/tara)


## Tara
Tara是一个纯java项目,包括常用util工具类和excel处理两个模块

## System Requirements

- Language: Java 8
- Environment: MacOS, Windows,Linux

## TODO

* [x] excel导入导出优化
* [x] 上传maven仓库
* [ ] 通知模块
* [ ] java doc



## Quick Start

 ### [excel-tara]
高性能excel处理工具
- 支持导入大批量数据处理
- 异步多线程导出数据
- 生成导入模板

#### 引入maven依赖,version为上方maven仓库中版本
```
 <dependency>
    <groupId>com.sondertara</groupId>
    <artifactId>excel-tara</artifactId>
    <version>${version}</version>
</dependency>
```
#### 1.导出示例
##### 1)添加导出注解 `@ExportField`

```
@Data
public class ExportVO {

    @ExportField(columnName = "姓名")
    private String name;
    @ExportField(columnName = "年龄")
    private Integer age;
    @ExportField(columnName = "住址")
    private String address;
}
```
##### 2)同步导出

```
/**
     * 导出Demo
     *
     * @ExportField写在 ExportVO的属性字段上
     * <p>
     * <p>
     * ExportVO是标注注解的类,Excel映射的导出类，需要自己定义
     * ParamEntity是查询的参数对象，需要自己定义
     * ResultEntity是分页查询到的结果List内部元素，需要自己定义
     * <p>
     * ExportVO可以和ResultEntity使用同一个对象,即直接在查询的结果对象上标注注解(建议使用两个对象, 实现解耦)
     * <p>
     * pageQuery方法需要自行实现, 即导出Excel的数据来源, 根据查询条件和当前页数和每页条数进行数据层查询, 当返回List的条数为NULL或者小于DEFAULT_PAGE_SIZE(每页条数)时, 将视为查询结束, 反之则会发生死循环
     <p>
     * convert方法需要自行实现, 参数就是查询出来的list中的每个元素引用, 可以对对象属性的转换或者对象的转换, 但是必须返回标注注解的对象
     */
    @RequestMapping("/exportDemo")
    public void exportResponse(@RequestParam(value = "fieldValues") String fieldValues, HttpServletResponse httpServletResponse) {
        ParamEntity param = JSON.parseObject(fieldValues, ParamEntity.class);
        ExcelBoot.builder(httpServletResponse, ExcelHelper.builder().fileName("导出列表").build(), ExportVO.class).exportResponse(param,
                new ExportFunction<ParamEntity, ResultEntity>() {
                    /**
                     * @param queryQaram 查询条件对象
                     * @param pageNum    当前页数,从1开始
                     * @param pageSize   每页条数,默认2000
                     * @return
                     */
                    @Override
                    public List<ResultEntity> pageQuery(ParamEntity queryQaram, int pageNum, int pageSize) {

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
                });
    }
```


## Contact 