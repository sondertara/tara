package com.sondertara.excel.antlr.tablemodel;

import com.sondertara.excel.meta.model.TaraCell;
import com.sondertara.excel.antlr.parser.VariableParserBaseVisitor;
import com.sondertara.excel.antlr.parser.VariableParserLexer;
import com.sondertara.excel.antlr.parser.VariableParserParser;
import com.sondertara.excel.antlr.ExcelHelper;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 该类对应于 Excel 的 [sheet页]
 * This class corresponds to the [sheet page] of Excel.
 *
 * @author huangxiaohu
 */
public class SheetTable implements Iterable<TaraCell> {

    /**
     * 行信息存储在这里
     * row information is stored here
     * <p>
     * key - 对应 Excel 上的 "行号"，从 1 开始。
     * row-number. start from 1
     * <p>
     * value - Row{@link Row}
     */
    private final Map<Integer, Row> rowMap = new ConcurrentHashMap<>();

    /**
     * 单元格的宽度存储在这里，开发者无需操作该属性
     * the width of the TaraCell is stored here, developers do not need to manipulate
     * this attribute
     * <p>
     * key - 列的索引，从 0 开始（Apache poi 行号和列号都是从 0 开始的）
     * col-index. start from 0 (Apache poi row and column numbers start from 0)
     * <p>
     * value - 列的宽度
     * col-width
     */
    private final Map<Integer, Integer> colWidthMap = new ConcurrentHashMap<>();

    /**
     * 对应 excel 的 sheet 名称
     * sheet name in excel
     */
    private String sheetName;

    /**
     * excel 表格的最后一行行号
     * 当该对象被初始化时，会更新此属性的值
     * <p>
     * the last row num in excel.
     * when the object is instantiated the value of the variable will be updated.
     */
    private int lastRowNum = 0;

    /**
     * 数字正则匹配
     * regex to match number
     */
    private final Pattern numberMatch = Pattern.compile("\\d+");

    public SheetTable(XSSFSheet xssfSheet) {

        List<CellRangeAddress> mergedRegions = xssfSheet.getMergedRegions();

        sheetName = xssfSheet.getSheetName();
        Iterator<org.apache.poi.ss.usermodel.Row> rowIterator = xssfSheet.rowIterator();
        rowIterator.forEachRemaining(row -> {
            int rowIndex = row.getRowNum();
            lastRowNum = Math.max(lastRowNum, rowIndex + 1);
            Map<String, TaraCell> colTaraCellMap = new ConcurrentHashMap<>();
            Iterator<org.apache.poi.ss.usermodel.Cell> TaraCellIterator = row.cellIterator();
            TaraCellIterator.forEachRemaining(TaraCell -> {

                int columnIndex = TaraCell.getColumnIndex();

                // get the merged region
                CellRangeAddress TaraCellRangeAddress = mergedRegions.stream().filter(TaraCellAddresses -> TaraCellAddresses.getFirstRow() == rowIndex && TaraCellAddresses.getFirstColumn() == columnIndex).findFirst().orElse(null);

                colTaraCellMap.put(ExcelHelper.getColName(columnIndex), new TaraCell((XSSFCell) TaraCell, TaraCellRangeAddress));
                colWidthMap.put(columnIndex, xssfSheet.getColumnWidth(columnIndex));
            });
            Row descRow = new Row(row, colTaraCellMap);
            rowMap.put(row.getRowNum() + 1, descRow);
        });
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public int getLastRowNum() {
        return lastRowNum;
    }

    public Map<Integer, Integer> getColWidthMap() {
        return colWidthMap;
    }

    public Iterator<Row> rowIterator() {
        return rowMap.values().iterator();
    }

    @Override
    public Iterator<TaraCell> iterator() {
        return new TaraCellRowIterator();
    }

    public class TaraCellRowIterator implements Iterator<TaraCell> {
        private int currentRowNum = 1;
        private Iterator<TaraCell> currentTaraCellIterator;

        /**
         * traverse every valid TaraCell.
         * if there is a valid TaraCell, then return true, after traversing the last TaraCell,
         * return false.
         */
        @Override
        public boolean hasNext() {
            if (currentTaraCellIterator == null) {
                if (currentRowNum <= getLastRowNum()) {
                    Row currentRow = getRow(currentRowNum);
                    // init TaraCell iterator.
                    currentTaraCellIterator = currentRow.iterator();
                } else {
                    return false;
                }
            }

            /*
             * traverse all TaraCells, line by line,
             * if the current line has been traversed, then traverse the TaraCells of the next
             * line.
             */
            if (!currentTaraCellIterator.hasNext()) {
                currentRowNum++;
                // get the TaraCell of the next row.
                if (currentRowNum <= getLastRowNum()) {
                    Row currentRow = getRow(currentRowNum);
                    currentTaraCellIterator = currentRow.iterator();
                } else {
                    return false;
                }
            }

            // start traversing current TaraCells.
            return currentTaraCellIterator.hasNext();
        }

        @Override
        public TaraCell next() {
            if (hasNext()) {
                return currentTaraCellIterator.next();
            }
            return null;
        }
    }

    /**
     * 获取行
     * get the specified row by row-number
     */
    public Row getRow(int rowNum) {
        return rowMap.get(rowNum);
    }

    /**
     * 删除行号大于等于指定 rowNum 的行
     * remove rows which row-num greater than or equals the specified row-num
     *
     * @param rowNum specified row-num
     */
    public void removeRowGE(int rowNum) {
        rowMap.keySet().stream().filter(key -> key >= rowNum).forEach(rowMap::remove);
        lastRowNum = rowNum - 1;
    }

    /**
     * 在表格最后添加一行
     * append a row at the end
     *
     * @param srcRow source row{@link Row}
     * @return desc row
     */
    public Row appendRow(Row srcRow) {

        lastRowNum++;
        Row descRow = srcRow.copy();

        // update row num
        descRow.setRowNum(lastRowNum);
        descRow.iterator().forEachRemaining(TaraCell -> {
            TaraCell.setRow(lastRowNum);

            int srcRowNum = srcRow.getRowNum();
            int descRowNum = descRow.getRowNum();
            int subtractRowNum = descRowNum - srcRowNum;

            MergedRegion mergedRegion = TaraCell.getMergedRegion();
            if (mergedRegion != null) {
                // update row num
                mergedRegion.setFirstRowNum(descRowNum);
                mergedRegion.setLastRowNum(mergedRegion.getLastRowNum() + subtractRowNum);
                TaraCell.setMergedRegion(mergedRegion);
            }

            // update formula row num
            if (TaraCell.getCellType().equals(CellType.FORMULA) && TaraCell.getValue() != null) {

                String oldFormula = TaraCell.getValue().toString();

                // lexical analysis
                VariableParserLexer lexer = new VariableParserLexer(CharStreams.fromString(oldFormula));
                CommonTokenStream tokens = new CommonTokenStream(lexer);

                // syntax analysis
                VariableParserParser parser = new VariableParserParser(tokens);

                String newFormula = parser.formula().exprList().accept(new VariableParserBaseVisitor<String>() {
                    @Override
                    public String visitExprList(VariableParserParser.ExprListContext ctx) {
                        String oldExprList = ctx.getText();
                        String newExprList = super.visitExprList(ctx);
                        return oldFormula.replaceAll(oldExprList, newExprList);
                    }

                    /**
                     * array
                     * e.g. B4:B5
                     */
                    @Override
                    public String visitArray(VariableParserParser.ArrayContext ctx) {
                        String arrayText = ctx.getText();
                        for (TerminalNode terminalNode : ctx.IDENTIFIER()) {
                            String identifierText = terminalNode.getText();
                            Matcher matcher = numberMatch.matcher(identifierText);
                            if (matcher.find()) {
                                int srcRowNum = Integer.parseInt(matcher.group());
                                int descRowNum = srcRowNum + subtractRowNum;
                                String replaceText = identifierText.replaceAll(Integer.toString(srcRowNum), Integer.toString(descRowNum));
                                arrayText = arrayText.replaceAll(identifierText, replaceText);
                            }
                        }
                        return arrayText;
                    }

                    /**
                     * name
                     * e.g. B4
                     */
                    @Override
                    public String visitName(VariableParserParser.NameContext ctx) {
                        String nameTest = ctx.getText();
                        String identifierText = ctx.qualifiedName().IDENTIFIER(0).getText();
                        Matcher matcher = numberMatch.matcher(identifierText);
                        if (matcher.find()) {
                            int srcRowNum = Integer.parseInt(matcher.group());
                            int descRowNum = srcRowNum + subtractRowNum;
                            String replaceText = identifierText.replaceAll(Integer.toString(srcRowNum), Integer.toString(descRowNum));
                            nameTest = nameTest.replaceAll(identifierText, replaceText);
                        }
                        return nameTest;
                    }
                });
                TaraCell.setFormula(newFormula);
            }
        });

        rowMap.put(lastRowNum, descRow);
        return descRow;
    }

    /**
     * 合并单元格
     * merge TaraCells
     * <p>
     * row num start from 1.
     * col name start from "A"
     */
    public void mergeCell(int firstRowNum, int lastRowNum, String firstColName, String lastColName) {
        MergedRegion mergedRegion = new MergedRegion(firstRowNum, lastRowNum, firstColName, lastColName);
        this.mergeCell(mergedRegion);
    }

    /**
     * 合并单元格
     * merge TaraCells
     *
     * @param mergedRegion 合并类
     */
    public void mergeCell(MergedRegion mergedRegion) {
        int firstRowNum = mergedRegion.getFirstRowNum();
        String firstColName = mergedRegion.getFirstColName();
        Row row = rowMap.get(firstRowNum);
        TaraCell TaraCell = row.getCell(firstColName);
        TaraCell.setMergedRegion(mergedRegion);
    }

    /**
     * 合并单元格
     * merge TaraCells
     *
     * @param mergedRegionList 合并类集合
     */
    public void mergeCellBatch(List<MergedRegion> mergedRegionList) {
        mergedRegionList.forEach(this::mergeCell);
    }

    /**
     * 设置指定范围的边框的样式，你可以更改边框的样式，如粗线、虚线等
     * set border style of the specified range, you can change the style of the
     * border, such as thick line, dotted line, etc.
     * <p>
     * row-num start from 1.
     * col-name start from "A"
     *
     * @param borderStyle        style enum
     * @param borderPositionEnum position enum
     */
    public void setBorderStyle(int firstRowNum, int lastRowNum, String firstColName, String lastColName, BorderStyle borderStyle, BorderPositionEnum borderPositionEnum) {
        Integer firstColIndex = ExcelHelper.getColIndex(firstColName);
        Integer lastColIndex = ExcelHelper.getColIndex(lastColName);

        rowMap.entrySet().stream().filter(entry -> entry.getKey() >= firstRowNum && entry.getKey() <= lastRowNum).forEach(entry -> {
            Row row = entry.getValue();
            row.getColCellMap().entrySet().stream().filter(TaraCellEntry -> ExcelHelper.getColIndex(TaraCellEntry.getKey()) >= firstColIndex && ExcelHelper.getColIndex(TaraCellEntry.getKey()) <= lastColIndex).forEach(TaraCellEntry -> {
                String colName = TaraCellEntry.getKey();
                TaraCell TaraCell = TaraCellEntry.getValue();
                CellStyle CellStyle = TaraCell.getCellStyle();

                switch (borderPositionEnum) {
                    case AROUND:
                        if (colName.equals(firstColName)) {
                            CellStyle.setBorderLeftEnum(borderStyle);
                        } else if (colName.equals(lastColName)) {
                            CellStyle.setBorderRightEnum(borderStyle);
                        }
                        CellStyle.setBorderTopEnum(borderStyle);
                        CellStyle.setBorderBottomEnum(borderStyle);
                        break;
                    case LEFT:
                        CellStyle.setBorderLeftEnum(borderStyle);
                        break;
                    case RIGHT:
                        CellStyle.setBorderRightEnum(borderStyle);
                        break;
                    case TOP:
                        CellStyle.setBorderTopEnum(borderStyle);
                        break;
                    case BOTTOM:
                        CellStyle.setBorderBottomEnum(borderStyle);
                        break;
                    default:
                        break;
                }
                TaraCell.setCellStyle(CellStyle);
            });
        });
    }
}
