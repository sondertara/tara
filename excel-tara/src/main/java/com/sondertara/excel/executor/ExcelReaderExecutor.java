package com.sondertara.excel.executor;


import com.sondertara.excel.ExcelFieldUtils;
import com.sondertara.excel.context.ExcelReaderContext;
import com.sondertara.excel.exception.ExcelReaderException;
import com.sondertara.excel.meta.model.ExcelRowDefinition;
import com.sondertara.excel.processor.ExcelPerRowProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author chenzw
 */
public class ExcelReaderExecutor<T> extends AbstractExcelReaderExecutor implements ExcelPerRowProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ExcelReaderExecutor.class);


    public ExcelReaderExecutor(ExcelReaderContext readerContext) {
        super(readerContext);
    }

    private void preProcess(ExcelRowDefinition row) {
        row.setSheetIndex(curSheetIndex);

        this.curRowIndex = row.getRowIndex();
        this.curSheet = readerContext.getSheetDefinitions().get(curSheetIndex);
    }

    @Override
    public void processTotalRow(int totalRows) {
        this.totalRows = totalRows;
        logger.info("total rows: {}", totalRows);
    }

    @Override
    public void processPerRow(ExcelRowDefinition row) {
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
    public boolean isEmptyRow(ExcelRowDefinition row) {
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
    public void preSet(ExcelRowDefinition row) {
        super.preSet(row);
    }


    @Override
    public boolean validate(ExcelRowDefinition row) {
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
    public void format(ExcelRowDefinition row) {
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
    protected boolean isTitleRow(ExcelRowDefinition row) {
        return super.isTitleRow(row);
    }

    @Override
    public List executeRead() {
        logger.debug("start read!");
        long startTimeMillis = System.currentTimeMillis();
        try {
            return super.executeRead();
        } finally {
            logger.debug("finish read![total cost:{}ms]", (System.currentTimeMillis() - startTimeMillis));
        }
    }
}
