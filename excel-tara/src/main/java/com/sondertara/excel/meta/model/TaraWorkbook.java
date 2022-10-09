package com.sondertara.excel.meta.model;

import com.sondertara.excel.exception.ExcelReaderException;

import java.util.ArrayList;
import java.util.List;

public class TaraWorkbook {
    private Integer activeTab;

    private final List<TaraSheet> sheets = new ArrayList<>();

    /**
     * get the specified sheet by index
     *
     * @param index sheet index in excel. start from 0
     */
    public TaraSheet getSheet(int index) {
        return sheets.get(index);
    }

    public int getSheetCount() {
        return sheets.size();
    }

    /**
     * get the specified sheet by sheet name
     *
     * @param sheetName sheet name
     */
    public TaraSheet getSheet(String sheetName) {
        for (TaraSheet sheet : sheets) {
            if (sheetName.equals(sheet.getName())) {
                return sheet;
            }
        }
        throw new ExcelReaderException("Unknown sheet name: " + sheetName);
    }

    public Integer getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(Integer activeTab) {
        this.activeTab = activeTab;
    }

    public List<TaraSheet> getSheets() {
        return this.sheets;
    }

    public void addSheet(TaraSheet sheet) {
        this.sheets.add(sheet);
    }
}
