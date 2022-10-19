package com.sondertara;


import com.sondertara.domain.ExcelDutyAdjustRecord;
import com.sondertara.domain.ExcelDutyStaffArrangementTemplate;
import com.sondertara.domain.ExcelDutyVacation;
import com.sondertara.domain.HolidayCfg;
import com.sondertara.excel.boot.ExcelBeanReader;
import com.sondertara.excel.boot.ExcelSimpleReader;
import com.sondertara.excel.exception.ExcelReaderException;
import com.sondertara.excel.fast.reader.Cell;
import com.sondertara.excel.fast.reader.ReadableWorkbook;
import com.sondertara.excel.fast.reader.Row;
import com.sondertara.excel.meta.model.ExcelRowDef;
import com.sondertara.excel.support.ExcelReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelReaderTest {

    private static final String EXCEL_TEMPLATE_DIR = "excel-template/";// "excel-template/";

    /**
     * 测试导入注解
     */
    @Test
    public void test() {
        final InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(EXCEL_TEMPLATE_DIR + "multi_sheet_data.xlsx");

        List<HolidayCfg> list = ExcelBeanReader.load(is).read(HolidayCfg.class);

        Assertions.assertEquals(1000, list.size());
    }

    @Test
    public void test2() {
        final InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(EXCEL_TEMPLATE_DIR + "duty_vacation.xlsx");
        List<ExcelDutyVacation> read = ExcelBeanReader.load(is).read(ExcelDutyVacation.class);
        Assertions.assertEquals(9, read.size());
    }

    @Test
    public void testRaw() {
        final InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(EXCEL_TEMPLATE_DIR + "duty_vacation.xlsx");
        ReadableWorkbook read = ExcelSimpleReader.load(is).read();
        read.getSheets().forEach(sheet -> {
            try {

                List<Row> rows = sheet.read();
                for (int i = 1; i < rows.size(); i++) {
                    Row cells = rows.get(i);
                    Cell cell = cells.getCell(2);
                    System.out.println(cell.asDate());
                }
                for (Row row : rows) {
                    System.out.println(row);
                }
                Assertions.assertEquals(10, rows.size());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    @Test
    public void test3() {
        final InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(EXCEL_TEMPLATE_DIR + "duty_adjust.xlsx");

        List<ExcelDutyAdjustRecord> recordList = ExcelBeanReader.load(is).read(ExcelDutyAdjustRecord.class);
        System.out.println(recordList);
        Assertions.assertEquals(5, recordList.size());
    }


    @Test
    public void testWithCallback() {
        final List<ExcelRowDef> list = new ArrayList<>();
        final InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(EXCEL_TEMPLATE_DIR + "duty_template.xlsx");
        final List<ExcelDutyStaffArrangementTemplate> dutyStaffArrangementTemplate = ExcelReader.newInstance(is)
                .configRowReadExceptionCallback((rowDefinition, ex) -> {
                    System.out.println(rowDefinition);


                    list.add(rowDefinition);

                    throw (ExcelReaderException) ex;

                })
                .configCellReadExceptionCallback((rowDefinition, cellDefinition, ex) -> {
                    System.out.println(cellDefinition.getColTitle());
                    System.out.println(ex);
                })
                .read(ExcelDutyStaffArrangementTemplate.class);

        System.out.println(list.size());
        System.out.println(dutyStaffArrangementTemplate);
    }

}
