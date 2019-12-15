package com.sondertara.model;

import com.sondertara.excel.common.ExcelTaraTool;
import com.sondertara.excel.entity.ErrorEntity;

import java.util.List;

/**
 * TODO
 * <p>
 * date 2019/12/15 7:52 下午
 *
 * @author huangxiaohu
 * @version 1.0
 * @since 1.0
 **/
public class Test {

    public static void main(String[] args) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ErrorEntity errorEntity = ErrorEntity.builder().errorMessage("test")
                        .cellIndex(1).cellValue("66633").columnName("hahah111").rowIndex(1).sheetIndex(1).build();
                ExcelTaraTool.addErrorEntity(errorEntity);

                List<List<String>> records = ExcelTaraTool.getErrorEntityRecords();

                ExcelTaraTool.writeRecords("hahah.csv", records);

                ExcelTaraTool.writeRecords("test22.csv", records);

                ExcelTaraTool.closeAllPrinter();
                ExcelTaraTool.getWorkFile("test22.csv");

            }
        }).start();

    }

}
