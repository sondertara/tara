package com.sondertara;

import com.alibaba.fastjson.JSON;
import com.sondertara.excel.ExcelTara;
import com.sondertara.excel.common.ExcelTaraTool;
import com.sondertara.excel.entity.ErrorEntity;
import com.sondertara.excel.function.ImportFunction;
import com.sondertara.model.ImportParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * the import demo
 * <p>
 * date 2019/12/15 9:08 下午
 *
 * @author huangxiaohu
 * @version 1.0
 * @since 1.0
 **/
public class ExcelmportDemo {
    private static final Logger logger = LoggerFactory.getLogger(ExcelmportDemo.class);

    public void importTest(String filePath) throws Exception {

        File file = new File(filePath);
        final FileInputStream inputStream = new FileInputStream(file);

        ExcelTara.of(ImportParam.class).handler(new ImportFunction<ImportParam>() {

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
        }).readExcel(true, inputStream);
        //获取导入错误数据
        List<List<String>> records = ExcelTaraTool.getErrorEntityRecords();
        //生成cvs
        ExcelTaraTool.writeRecords("import_error.csv", records);
        //获取file对象
        File workFile = ExcelTaraTool.getWorkFile("import_error.csv");
//        FileUtil.remove(workFile);
    }
}
