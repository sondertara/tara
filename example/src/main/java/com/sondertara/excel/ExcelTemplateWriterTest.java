package com.sondertara.excel;

import com.sondertara.excel.domain.HolidayCfg;
import com.sondertara.excel.domain.User;
import com.sondertara.excel.boot.ExcelTemplateWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelTemplateWriterTest {
    private static final String DEFAULT_TARGET_EXCEL_DIR = "target/generated-excel/";

    @BeforeAll
    public void setup() {

        File targetPath = new File(DEFAULT_TARGET_EXCEL_DIR);
        if (!targetPath.exists()) {
            targetPath.mkdirs();
        }
    }

    /**
     * 导出模版 -- 多个Model类
     */
    @Test
    public void testWriteTemplateXlsxWdithMulitModel() {
        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "template_by_models.xlsx"))) {
            ExcelTemplateWriter.mapper(User.class, HolidayCfg.class).to(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出模版 -- 单个Model类
     */
    @Test
    public void testWriteTempateXlsxWidthSingleModel() {
        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "template_by_model.xlsx"))) {
            ExcelTemplateWriter.mapper(HolidayCfg.class).to(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
