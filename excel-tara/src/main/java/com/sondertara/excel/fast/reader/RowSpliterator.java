/*
 * Copyright 2016 Dhatim.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sondertara.excel.fast.reader;

import com.sondertara.common.util.StringUtils;
import com.sondertara.excel.exception.ExcelReaderException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.sondertara.excel.fast.reader.DefaultXMLInputFactory.factory;

class RowSpliterator implements Spliterator<Row> {

    private final SimpleXmlReader r;
    private final ReadableWorkbook workbook;

    private final HashMap<CellRangeAddress, String> sharedFormula = new HashMap<>();
    private int rowCapacity = 16;

    public RowSpliterator(ReadableWorkbook workbook, InputStream inputStream) throws XMLStreamException {
        this.workbook = workbook;
        this.r = new SimpleXmlReader(factory, inputStream);

        r.goTo("sheetData");
    }

    @Override
    public boolean tryAdvance(Consumer<? super Row> action) {
        try {
            if (hasNext()) {
                action.accept(next());
                return true;
            } else {
                return false;
            }
        } catch (XMLStreamException e) {
            throw new ExcelReaderException(e);
        }
    }

    @Override
    public Spliterator<Row> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return DISTINCT | IMMUTABLE | NONNULL | ORDERED;
    }

    private boolean hasNext() throws XMLStreamException {
        if (r.goTo(() -> r.isStartElement("row") || r.isEndElement("sheetData"))) {
            return "row".equals(r.getLocalName());
        } else {
            return false;
        }
    }

    private Row next() throws XMLStreamException {
        if (!"row".equals(r.getLocalName())) {
            throw new NoSuchElementException();
        }
        int rowIndex = r.getIntAttribute("r");
        List<Cell> cells = new ArrayList<>(rowCapacity);
        int physicalCellCount = 0;

        while (r.goTo(() -> r.isStartElement("c") || r.isEndElement("row"))) {
            if ("row".equals(r.getLocalName())) {
                break;
            }

            Cell cell = parseCell();
            CellAddress addr = cell.getAddress();
            ensureSize(cells, addr.getColumn() + 1);

            cells.set(addr.getColumn(), cell);
            physicalCellCount++;
        }
        rowCapacity = Math.max(rowCapacity, cells.size());
        return new Row(rowIndex, physicalCellCount, cells);
    }

    private Cell parseCell() throws XMLStreamException {
        String cellRef = r.getAttribute("r");
        CellAddress addr = new CellAddress(cellRef);
        String type = r.getOptionalAttribute("t").orElse("n");
        String styleString = r.getAttribute("s");
        String formatId = null;
        String formatString = null;
        if (styleString != null) {
            int index = Integer.parseInt(styleString);
            if (index < workbook.getFormats().size()) {
                formatId = workbook.getFormats().get(index);
                formatString = workbook.getNumFmtIdToFormat().get(formatId);
            }
        }

        if ("inlineStr".equals(type)) {
            return parseInlineStr(addr);
        } else if ("s".equals(type)) {
            return parseString(addr);
        } else {
            return parseOther(addr, type, formatId, formatString);
        }
    }

    private Cell parseOther(CellAddress addr, String type, String dataFormatId, String dataFormatString) throws XMLStreamException {
        CellType definedType = parseType(type);
        Function<String, ?> parser = getParserForType(definedType, dataFormatString);

        Object value = null;
        String formula = null;
        String rawValue = null;
        while (r.goTo(() -> r.isStartElement("v") || r.isEndElement("c") || r.isStartElement("f"))) {
            if ("v".equals(r.getLocalName())) {
                rawValue = r.getValueUntilEndElement("v");
                try {
                    value = "".equals(rawValue) ? null : parser.apply(rawValue);
                } catch (ExcelReaderException e) {
                    if (workbook.getReadingOptions().isCellInErrorIfParseError()) {
                        definedType = CellType.ERROR;
                    } else {
                        throw e;
                    }
                }
            } else if ("f".equals(r.getLocalName())) {
                String ref = r.getAttribute("ref");
                String t = r.getAttribute("t");
                formula = r.getValueUntilEndElement("f");
                if ("array".equals(t) && ref != null) {
                    CellRangeAddress range = CellRangeAddress.valueOf(ref);
                    sharedFormula.put(range, formula);
                }
            } else {
                break;
            }
        }

        if (formula == null) {
            formula = getSharedFormula(addr).orElse(null);
        }

        if (formula == null && value == null && definedType == CellType.NUMERIC) {
            return new Cell(workbook, CellType.BLANK, null, addr, null, rawValue);
        } else {
            CellType cellType = (formula != null) ? CellType.FORMULA : definedType;
            return new Cell(workbook, cellType, value, addr, formula, rawValue, dataFormatId, dataFormatString);
        }
    }

    private Cell parseString(CellAddress addr) throws XMLStreamException {
        r.goTo(() -> r.isStartElement("v") || r.isEndElement("c"));
        if (r.isEndElement("c")) {
            return empty(addr, CellType.STRING);
        }
        String v = r.getValueUntilEndElement("v");
        if (v.isEmpty()) {
            return empty(addr, CellType.STRING);
        }
        int index = Integer.parseInt(v);
        String sharedStringValue = workbook.getSharedStringsTable().getItemAt(index);
        Object value = sharedStringValue;
        String formula = null;
        String rawValue = sharedStringValue;
        return new Cell(workbook, CellType.STRING, value, addr, formula, rawValue);
    }

    private Cell empty(CellAddress addr, CellType type) {
        return new Cell(workbook, type, "", addr, null, "");
    }

    private Cell parseInlineStr(CellAddress addr) throws XMLStreamException {
        Object value = null;
        String formula = null;
        String rawValue = null;
        while (r.goTo(() -> r.isStartElement("is") || r.isEndElement("c") || r.isStartElement("f"))) {
            if ("is".equals(r.getLocalName())) {
                rawValue = r.getValueUntilEndElement("is");
                value = rawValue;
            } else if ("f".equals(r.getLocalName())) {
                formula = r.getValueUntilEndElement("f");
            } else {
                break;
            }
        }
        CellType cellType = formula == null ? CellType.STRING : CellType.FORMULA;
        return new Cell(workbook, cellType, value, addr, formula, rawValue);
    }

    private Optional<String> getSharedFormula(CellAddress addr) {
        for (Map.Entry<CellRangeAddress, String> entry : sharedFormula.entrySet()) {
            if (entry.getKey().isInRange(addr.getRow(), addr.getColumn())) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    private CellType parseType(String type) {
        switch (type) {
            case "b":
                return CellType.BOOLEAN;
            case "e":
                return CellType.ERROR;
            case "n":
                return CellType.NUMERIC;
            case "str":
                return CellType.FORMULA;
            case "s":
            case "inlineStr":
                return CellType.STRING;
            default:
        }
        throw new IllegalStateException("Unknown cell type : " + type);
    }

    private Function<String, ?> getParserForType(CellType type, String dataFormatString) {
        switch (type) {
            case BOOLEAN:
                return RowSpliterator::parseBoolean;
            case NUMERIC:
                if (StringUtils.containsAny(dataFormatString, "y", "m", "d", "h", "s", "Y", "M", "D")) {
                    return RowSpliterator::parseDate;
                }
                return RowSpliterator::parseNumber;
            case FORMULA:
            case ERROR:
                return Function.identity();
            default:
        }
        throw new IllegalStateException("No parser defined for type " + type);
    }

    private static BigDecimal parseNumber(String s) {
        try {
            return new BigDecimal(s);
        } catch (NumberFormatException e) {
            throw new ExcelReaderException("Cannot parse number : " + s, e);
        }
    }

    private static Boolean parseBoolean(String s) {
        if ("0".equals(s)) {
            return Boolean.FALSE;
        } else if ("1".equals(s)) {
            return Boolean.TRUE;
        } else {
            throw new ExcelReaderException("Invalid boolean cell value: '" + s + "'. Expecting '0' or '1'.");
        }
    }

    private static Date parseDate(String s) {
        return DateUtil.getJavaDate(Double.parseDouble(s));
    }

    private static void ensureSize(List<?> list, int newSize) {
        if (list.size() == newSize) {
            return;
        }
        int toAdd = newSize - list.size();
        for (int i = 0; i < toAdd; i++) {
            list.add(null);
        }
    }

}
