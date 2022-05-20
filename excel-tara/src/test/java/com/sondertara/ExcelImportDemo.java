package com.sondertara;

import com.alibaba.fastjson.JSON;
import com.sondertara.excel.ExcelImportTara;
import com.sondertara.excel.common.ExcelTaraHelper;
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
public class ExcelImportDemo {
    private static final Logger logger = LoggerFactory.getLogger(ExcelImportDemo.class);

    public void importTest(String filePath) throws Exception {

        File file = new File(filePath);
        final FileInputStream inputStream = new FileInputStream(file);

        ExcelImportTara.mapper(ImportParam.class).from(inputStream).onRow((sheetIndex, rowIndex, entity) -> {
            logger.info("sheet[{}],第{}行，解析数据为:{}", sheetIndex, rowIndex, JSON.toJSONString(entity));
            try {
                //  handleImportData(param);
            } catch (Exception e) {
                logger.error(" handle record error", e);
            }
        }).onError(errorEntity -> {
            //将每条数据非空和正则校验后的错误信息errorEntity进行自定义处理

            logger.info(errorEntity.toString());
            ExcelTaraHelper.addErrorEntity(errorEntity);
        }).read();

        //获取导入错误数据
        List<List<String>> records = ExcelTaraHelper.getErrorEntityRecords();
        //生成cvs
        ExcelTaraHelper.writeRecords("import_error.csv", records);
        //获取file对象
        File workFile = ExcelTaraHelper.getWorkFile("import_error.csv");
//        FileUtil.remove(workFile);
    }
}
