package com.sondertara.model;

import com.sondertara.excel.common.ExcelTaraHelper;
import com.sondertara.excel.entity.ErrorEntity;
import org.junit.jupiter.api.Test;

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
public class ExcelTaraHelperTest {

    @Test
    public void test() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ErrorEntity errorEntity = ErrorEntity.builder().errorMessage("test")
                        .cellIndex(1).cellValue("66633").columnName("hahah111").rowIndex(1).sheetIndex(1).build();
                ExcelTaraHelper.addErrorEntity(errorEntity);

                List<List<String>> records = ExcelTaraHelper.getErrorEntityRecords();

                ExcelTaraHelper.writeRecords("hahah.csv", records);

                ExcelTaraHelper.writeRecords("test22.csv", records);

                ExcelTaraHelper.closeAllPrinter();
                ExcelTaraHelper.getWorkFile("test22.csv");

            }
        }).start();

    }

}
