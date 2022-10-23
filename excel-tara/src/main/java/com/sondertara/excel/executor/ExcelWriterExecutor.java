package com.sondertara.excel.executor;

import com.sondertara.excel.context.ExcelRawWriterContext;
import com.sondertara.excel.exception.ExcelAnnotationWriterException;
import com.sondertara.excel.exception.ExcelWriterException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;

/**
 * @author huangxiaohu
 */
@Slf4j
public class ExcelWriterExecutor extends AbstractExcelWriterExecutor {

    public ExcelWriterExecutor(final ExcelRawWriterContext<Workbook> writerContext) {
        super(writerContext);
    }

    @Override
    public void beforeCallback() {

    }


    @Override
    public void handleComplexHeader(SXSSFSheet sheet, String sheetIdentity) {
        log.debug("start handle complex header!");
        final long startTimeMillis = System.currentTimeMillis();
        try {
            super.handleComplexHeader(sheet, sheetIdentity);
        } catch (final Throwable e) {
            throw new ExcelAnnotationWriterException(sheetIdentity, this.curSheetIndex, this.curRowIndex, this.curColIndex, "", "复杂表头生成失败!", e);
        } finally {
            log.debug("finish handle complex header! [cost:{}ms]", (System.currentTimeMillis() - startTimeMillis));
        }

    }

    @Override
    public void addDataValidation(SXSSFSheet sheet, String sheetIdentity) {
        log.debug("start add data validation!");
        final long startTimeMillis = System.currentTimeMillis();
        try {
            super.addDataValidation(sheet, sheetIdentity);
        } catch (final Throwable e) {
            throw new ExcelAnnotationWriterException(sheetIdentity, this.curSheetIndex, this.curRowIndex, this.curColIndex, "", "添加单元格校验器失败", e);
        } finally {
            log.debug("finish add data validation! [cost:{}ms]", (System.currentTimeMillis() - startTimeMillis));
        }
    }

    @Override
    public void initHeadTitle(SXSSFSheet sheet, String sheetIdentity) {
        log.debug("start write head title!");
        final long startTimeMillis = System.currentTimeMillis();
        try {
            super.initHeadTitle(sheet, sheetIdentity);
        } catch (final Throwable e) {
            throw new ExcelWriterException(sheetIdentity, this.curSheetIndex, this.curRowIndex, this.curColIndex, "", "初始化标题失败", e);
        } finally {
            log.debug("finish write head title! [cost:{}ms]", (System.currentTimeMillis() - startTimeMillis));
        }
    }

    @Override
    public void initData() {
        log.debug("start write data!");
        final long startTimeMillis = System.currentTimeMillis();
        try {
            super.initData();
        } catch (final Throwable e) {
            throw new ExcelWriterException("写入数据失败", e);
        } finally {
            log.debug("finish write data! [cost:{}ms]", (System.currentTimeMillis() - startTimeMillis));
        }
    }

    @Override
    public void afterCallback() {

    }
}
