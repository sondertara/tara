package com.sondertara.excel.executor;

import com.sondertara.excel.context.ExcelWriterContext;
import com.sondertara.excel.exception.ExcelWriterException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huangxiaohu
 */
@Slf4j
public class ExcelWriterExecutor extends AbstractExcelWriterExecutor {

    public ExcelWriterExecutor(final ExcelWriterContext writerContext) {
        super(writerContext);
    }

    @Override
    public void beforeCallback() {

    }

    @Override
    public void sheetPaging() {
        log.debug("start sheet paging!");
        final long startTimeMillis = System.currentTimeMillis();
        try {
            super.sheetPaging();
        } catch (final Throwable e) {
            throw new ExcelWriterException(this.curSheetIndex, this.curRowIndex, this.curColIndex, "", "Sheet分页失败", e);
        } finally {
            log.debug("finish sheet paging! [cost:{}ms]", (System.currentTimeMillis() - startTimeMillis));
        }

    }

    @Override
    public void handleComplexHeader() {
        log.debug("start handle complex header!");
        final long startTimeMillis = System.currentTimeMillis();
        try {
            super.handleComplexHeader();
        } catch (final Throwable e) {
            throw new ExcelWriterException(this.curSheetIndex, this.curRowIndex, this.curColIndex, "", "复杂表头生成失败!", e);
        } finally {
            log.debug("finish handle complex header! [cost:{}ms]", (System.currentTimeMillis() - startTimeMillis));
        }

    }

    @Override
    public void addDataValidation() {
        log.debug("start add data validation!");
        final long startTimeMillis = System.currentTimeMillis();
        try {
            super.addDataValidation();
        } catch (final Throwable e) {
            throw new ExcelWriterException(this.curSheetIndex, this.curRowIndex, this.curColIndex, "", "添加单元格校验器失败", e);
        } finally {
            log.debug("finish add data validation! [cost:{}ms]", (System.currentTimeMillis() - startTimeMillis));
        }
    }

    @Override
    public void initHeadTitle() {
        log.debug("start write head title!");
        final long startTimeMillis = System.currentTimeMillis();
        try {
            super.initHeadTitle();
        } catch (final Throwable e) {
            throw new ExcelWriterException(this.curSheetIndex, this.curRowIndex, this.curColIndex, "", "初始化标题失败", e);
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
            throw new ExcelWriterException(this.curSheetIndex, this.curRowIndex, this.curColIndex, "", "写入数据失败", e);
        } finally {
            log.debug("finish write data! [cost:{}ms]", (System.currentTimeMillis() - startTimeMillis));
        }
    }

    @Override
    public void afterCallback() {

    }
}
