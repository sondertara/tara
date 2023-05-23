package com.sondertara.excel;


import com.sondertara.common.time.DatePattern;
import com.sondertara.common.util.LocalDateTimeUtils;
import com.sondertara.excel.boot.ExcelBeanReader;
import com.sondertara.excel.boot.ExcelSimpleReader;
import com.sondertara.excel.domain.ExcelDutyAdjustRecord;
import com.sondertara.excel.domain.ExcelDutyStaffArrangementTemplate;
import com.sondertara.excel.domain.ExcelDutyVacation;
import com.sondertara.excel.domain.HolidayCfg;
import com.sondertara.excel.fast.reader.Cell;
import com.sondertara.excel.fast.reader.ReadableWorkbook;
import com.sondertara.excel.fast.reader.Row;
import com.sondertara.excel.meta.annotation.converter.ExcelKVConvert;
import com.sondertara.excel.meta.model.ExcelRowDef;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void test2() {
        final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(EXCEL_TEMPLATE_DIR + "duty_vacation.xlsx");
        List<ExcelDutyVacation> read = ExcelBeanReader.load(is).read(ExcelDutyVacation.class);
        Assertions.assertEquals(9, read.size());
    }

    /**
     * Raw Excel parser, this is very faster
     *
     * @see ExcelSimpleReader
     */
    @Test
    public void testRaw() {
        final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(EXCEL_TEMPLATE_DIR + "duty_vacation.xlsx");
        try (ReadableWorkbook read = ExcelSimpleReader.load(is).read()) {

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

    @Test
    public void test3() {
        final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(EXCEL_TEMPLATE_DIR + "duty_adjust.xlsx");

        List<ExcelDutyAdjustRecord> recordList = ExcelBeanReader.load(is).read(ExcelDutyAdjustRecord.class);
        System.out.println(recordList);
        Assertions.assertEquals(5, recordList.size());
    }


    @Test
    public void testWithCallback() {
        final List<ExcelRowDef> list = new ArrayList<>();
        final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(EXCEL_TEMPLATE_DIR + "duty_template.xlsx");
        final List<ExcelDutyStaffArrangementTemplate> dutyStaffArrangementTemplate =
                ExcelBeanReader.load(is).read(ExcelDutyStaffArrangementTemplate.class);
    }

}
