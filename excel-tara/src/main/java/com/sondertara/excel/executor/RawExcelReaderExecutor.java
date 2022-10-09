package com.sondertara.excel.executor;

import com.sondertara.excel.analysis.RawXlsxAnalysisHandler;
import com.sondertara.excel.context.ExcelRawReaderContext;
import com.sondertara.excel.meta.model.TaraWorkbook;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * @author huangxiaohu
 */

public class RawExcelReaderExecutor implements TaraExcelExecutor<TaraWorkbook> {

    protected int curSheetIndex = 0;

    protected ExcelRawReaderContext readerContext;
    protected TaraWorkbook workbook;

    public RawExcelReaderExecutor(final ExcelRawReaderContext readerContext) {
        this.readerContext = readerContext;
        this.workbook = new TaraWorkbook();
    }

    @Override
    public TaraWorkbook execute() {
        // 延迟解析比率
        ZipSecureFile.setMinInflateRatio(-1.0d);
        try (final OPCPackage pkg = OPCPackage.open(readerContext.getInputStream())) {
            final XSSFReader xssfReader = new XSSFReader(pkg);
            final XMLReader parser = XMLReaderFactory
                    .createXMLReader("com.sun.org.apache.xerces.internal.parsers.SAXParser");

            parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            final ContentHandler xlsxAnalysisHandler = new RawXlsxAnalysisHandler(xssfReader.getStylesTable(),
                    xssfReader.getSharedStringsTable(), workbook);
            parser.setContentHandler(xlsxAnalysisHandler);
            InputStream data = xssfReader.getWorkbookData();
            parser.parse(new InputSource(data));
            final Iterator<InputStream> sheets = xssfReader.getSheetsData();
            RawXlsxAnalysisHandler contentHandler = (RawXlsxAnalysisHandler) parser.getContentHandler();
            while (sheets.hasNext()) {
                contentHandler.beforeParseSheet(this.curSheetIndex);
                final InputStream sheet = sheets.next();
                final InputSource sheetSource = new InputSource(sheet);
                parser.parse(sheetSource);
                sheet.close();
                contentHandler.afterParseSheet(this.curSheetIndex);
                this.curSheetIndex++;
            }
            contentHandler.finish();
        } catch (final IOException | SAXException | OpenXML4JException e) {
            e.printStackTrace();
        }
        return this.workbook;
    }
}
