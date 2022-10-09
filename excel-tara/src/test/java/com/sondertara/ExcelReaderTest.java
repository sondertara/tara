package com.sondertara;


import com.sondertara.domain.ExcelDutyAdjustRecord;
import com.sondertara.domain.ExcelDutyStaffArrangementTemplate;
import com.sondertara.domain.ExcelDutyVacation;
import com.sondertara.domain.HolidayCfg;
import com.sondertara.excel.exception.ExcelReaderException;
import com.sondertara.excel.meta.model.ExcelRowDef;
import com.sondertara.excel.support.ExcelReader;
import com.sondertara.excel.support.callback.RowReadExCallback;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelReaderTest {

    private static final String EXCEL_TEMPLATE_DIR = "excel-template/";// "excel-template/";

    @Test
    public void test() {
        final InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(EXCEL_TEMPLATE_DIR + "multi_sheet_data.xlsx");
        final ExcelReader excelReader = new ExcelReader(is);
        final List<HolidayCfg> holidayCfgs = excelReader.read(HolidayCfg.class);

        Assertions.assertEquals(1000, holidayCfgs.size());
    }

    @Test
    public void test2() {
        final InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(EXCEL_TEMPLATE_DIR + "duty_vacation.xlsx");
        final ExcelReader excelReader = new ExcelReader(is);
        final List<ExcelDutyVacation> excelDutyVacations = excelReader.read(ExcelDutyVacation.class);

        Assertions.assertEquals(9, excelDutyVacations.size());
    }

    @Test
    public void test3() {
        final InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(EXCEL_TEMPLATE_DIR + "duty_adjust.xlsx");
        final ExcelReader excelReader = new ExcelReader(is);
        final List<ExcelDutyAdjustRecord> excelDutyAdjustRecords = excelReader.read(ExcelDutyAdjustRecord.class);

         Assertions.assertEquals(5, excelDutyAdjustRecords.size());
    }

    /*@Test
    public void test4() {
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(EXCEL_TEMPLATE_DIR + "duty_template.xlsx");

        ExcelReader excelReader = new ExcelReader(is);

        List<ExcelDutyStaffArrangementTemplate> dutyStaffArrangementTemplate = excelReader.read(ExcelDutyStaffArrangementTemplate.class);
         Assertions.assertEquals(1, dutyStaffArrangementTemplate.size());
    }*/

    @Test
    public void testWithCallback() {
        final List<ExcelRowDef> list = new ArrayList<>();
        final InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(EXCEL_TEMPLATE_DIR + "duty_template.xlsx");
        final List<ExcelDutyStaffArrangementTemplate> dutyStaffArrangementTemplate = ExcelReader.newInstance(is)
                .configRowReadExceptionCallback(new RowReadExCallback() {
                    @Override
                    public void call(ExcelRowDef rowDefinition, Exception ex) {
                        System.out.println(rowDefinition);


                        list.add(rowDefinition);

                        throw (ExcelReaderException) ex;

                    }
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
