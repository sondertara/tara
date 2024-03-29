package com.sondertara.excel.antlr.tablemodel;

import com.sondertara.excel.meta.model.TaraCell;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.poi.ss.usermodel.BorderStyle;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 该类对应于 Excel 的 [行]
 * this class corresponds to each [row] of Excel.
 *
 * @author huangxiaohu
 */
public class Row implements Iterable<TaraCell>, Serializable {

    private static final long serialVersionUID = 5924803090073879378L;

    /**
     * row-num. start from 1
     */
    private int rowNum;
    private final boolean zeroHeight;
    private final float heightInPoints;
    private final short height;

    /**
     * key - col-name.
     * value - Row{@link Row}
     */
    private final Map<String, TaraCell> colCellMap;

    public Row(org.apache.poi.ss.usermodel.Row row, Map<String, TaraCell> colCellMap) {
        rowNum = row.getRowNum() + 1;
        zeroHeight = row.getZeroHeight();
        heightInPoints = row.getHeightInPoints();
        height = row.getHeight();
        this.colCellMap = colCellMap;
    }

    /**
     * get the cell of the row by cell name
     */
    public TaraCell getCell(String cellName) {
        return colCellMap.get(cellName);
    }

    /**
     * deep clone
     *
     * @return new Row
     */
    public Row copy() {
        return SerializationUtils.clone(this);
    }

    /**
     * traverse all cells in the row
     */
    @Override
    public Iterator<TaraCell> iterator() {
        return colCellMap.values().iterator();
    }

    public boolean getZeroHeight() {
        return zeroHeight;
    }

    public float getHeightInPoints() {
        return heightInPoints;
    }

    public short getHeight() {
        return height;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public Map<String, TaraCell> getColCellMap() {
        return colCellMap;
    }

    /**
     * 设置该行边框的样式，你可以更改边框的样式，如粗线、虚线等
     * set border style of this row, you can change the style of the border, such as
     * thick line, dotted line, etc.
     *
     * @param positionEnum position enum
     * @param borderStyle  style enum
     */
    public void setBorderStyle(BorderPositionEnum positionEnum, BorderStyle borderStyle) {
        Set<Entry<String, TaraCell>> entrySet = colCellMap.entrySet();
        switch (positionEnum) {
            default:
            case AROUND:
                int i = 0;
                for (Entry<String, TaraCell> entry : entrySet) {
                    TaraCell cell = entry.getValue();
                    CellStyle cellStyle = cell.getCellStyle();
                    if (i == 0) {
                        cellStyle.setBorderLeftEnum(borderStyle);
                    } else if (i == entrySet.size() - 1) {
                        cellStyle.setBorderRightEnum(borderStyle);
                    }
                    cellStyle.setBorderTopEnum(borderStyle);
                    cellStyle.setBorderBottomEnum(borderStyle);

                    cell.setCellStyle(cellStyle);
                    i++;
                }
                break;
            case TOP:
                for (Entry<String, TaraCell> entry : entrySet) {
                    TaraCell cell = entry.getValue();
                    CellStyle cellStyle = cell.getCellStyle();
                    cellStyle.setBorderTopEnum(borderStyle);
                    cell.setCellStyle(cellStyle);
                }
                break;
            case BOTTOM:
                for (Entry<String, TaraCell> entry : entrySet) {
                    TaraCell cell = entry.getValue();
                    CellStyle cellStyle = cell.getCellStyle();
                    cellStyle.setBorderBottomEnum(borderStyle);
                    cell.setCellStyle(cellStyle);
                }
                break;
            case LEFT:
                for (Entry<String, TaraCell> entry : entrySet) {
                    TaraCell cell = entry.getValue();
                    CellStyle cellStyle = cell.getCellStyle();
                    cellStyle.setBorderLeftEnum(borderStyle);
                    cell.setCellStyle(cellStyle);
                }
                break;
            case RIGHT:
                for (Entry<String, TaraCell> entry : entrySet) {
                    TaraCell cell = entry.getValue();
                    CellStyle cellStyle = cell.getCellStyle();
                    cellStyle.setBorderRightEnum(borderStyle);
                    cell.setCellStyle(cellStyle);
                }
                break;
        }
    }
}
