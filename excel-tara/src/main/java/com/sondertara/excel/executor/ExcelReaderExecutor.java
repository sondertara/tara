package com.sondertara.excel.executor;

import com.sondertara.excel.context.ExcelRawReaderContext;
import com.sondertara.excel.exception.ExcelReaderException;
import com.sondertara.excel.meta.model.AnnotationSheet;
import com.sondertara.excel.meta.model.ExcelRowDef;
import com.sondertara.excel.processor.ExcelPerRowProcessor;
import com.sondertara.excel.utils.ExcelFieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author huangxiaohu
 */
public class ExcelReaderExecutor<T> extends AbstractExcelReaderExecutor<T> implements ExcelPerRowProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ExcelReaderExecutor.class);

    public ExcelReaderExecutor(ExcelRawReaderContext<List<T>> readerContext) {
        super(readerContext);
    }

    private void preProcess(ExcelRowDef row) {
        row.setSheetIndex(curSheetIndex);

        this.curRowIndex = row.getRowIndex();
        this.curSheet = (AnnotationSheet) readerContext.getSheetDefinitions().get(curSheetIndex);
        if (this.curSheet == null) {
            this.curSheet = (AnnotationSheet) readerContext.getSheetDefinitions().get(curSheetIndex + 1);
        }
    }

    @Override
    public void processTotalRow(int totalRows) {
        this.totalRows = totalRows;
        logger.info("total rows: {}", totalRows);
    }

    @Override
    public void processPerRow(ExcelRowDef row) {
        preProcess(row);

        if (this.isTitleRow(row)) {
            return;
        }

        if (!this.isEmptyRow(row)) {
            this.preSet(row);
            if (this.validate(row)) {
                this.format(row);
            }
        }
    }

    @Override
    protected ExcelPerRowProcessor getExcelRowProcess() {
        return this;
    }

    @Override
    public boolean isEmptyRow(ExcelRowDef row) {
        logger.debug("start validate empty row!");
        long startTimeMillis = System.currentTimeMillis();

        try {
            if (super.isEmptyRow(row)) {
                logger.warn("Found empty row at [sheet={},row={}]", row.getSheetIndex(), row.getRowIndex());
                return true;
            } else {
                return false;
            }
        } catch (Throwable e) {
            throw new ExcelReaderException(this.curSheetIndex, this.curRowIndex, this.curColIndex,
                    ExcelFieldUtils.getCellValue(row, this.curColIndex), e.getMessage(), e);
        } finally {
            logger.debug("finish validate emtpy row! [cost:{}ms] ", (System.currentTimeMillis() - startTimeMillis));
        }
    }

    @Override
    public void preSet(ExcelRowDef row) {
        super.preSet(row);
    }

    @Override
    public boolean validate(ExcelRowDef row) {
        logger.debug("start validate empty row!");
        long startTimeMillis = System.currentTimeMillis();
        try {
            return super.validate(row);
        } catch (Throwable e) {
            throw new ExcelReaderException(this.curSheetIndex, this.curRowIndex, this.curColIndex,
                    ExcelFieldUtils.getCellValue(row, this.curColIndex), e.getMessage(), e);
        } finally {
            logger.debug("finish validate empty row! [cost:{}ms]", (System.currentTimeMillis() - startTimeMillis));
        }
    }

    @Override
    public void format(ExcelRowDef row) {
        logger.debug("start format and assign value!");
        long startTimeMillis = System.currentTimeMillis();
        try {
            super.format(row);
        } catch (Throwable e) {
            throw new ExcelReaderException(this.curSheetIndex, this.curRowIndex, this.curColIndex,
                    ExcelFieldUtils.getCellValue(row, this.curColIndex), e.getMessage(), e);
        } finally {
            logger.debug("finish format and assign value! [cost:{}ms]", (System.currentTimeMillis() - startTimeMillis));
        }
    }

    @Override
    protected boolean isTitleRow(ExcelRowDef row) {
        return super.isTitleRow(row);
    }

    @Override
    public List<T> execute() {
        logger.debug("start read!");
        long startTimeMillis = System.currentTimeMillis();
        try {
            return super.execute();
        } finally {
            logger.debug("finish read![total cost:{}ms]", (System.currentTimeMillis() - startTimeMillis));
        }
    }
}
