package com.sondertara;

import com.sondertara.domain.ImportParam;
import com.sondertara.excel.boot.ExcelBeanReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
        List<ImportParam> list = ExcelBeanReader.load(file).rowError((rowDefinition, ex) -> {
            logger.warn("sheet[{}],the row[{}],error:", rowDefinition.getSheetIndex(), rowDefinition.getRowIndex(), ex);

        }).cellError((rowDefinition, cellDefinition, ex) -> {
            logger.warn("sheet[{}],the row[{}], cell[{}],error:", rowDefinition.getSheetIndex(), rowDefinition.getRowIndex(), cellDefinition.getColIndex(), ex);

        }).read(ImportParam.class);
    }
}
