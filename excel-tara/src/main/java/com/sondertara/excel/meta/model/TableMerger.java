package com.sondertara.excel.meta.model;

import java.util.ArrayList;
import java.util.List;

public class TableMerger {
    private List<List<ExcelHeadDef>> table;

    public TableMerger(List<List<String>> data) {
        this.table = new ArrayList<>();


        for (int i = 0; i < data.size(); i++) {

            List<ExcelHeadDef> cellRow = new ArrayList<>();

            for (int i1 = 0; i1 < data.get(i).size(); i1++) {
                cellRow.add(new ExcelHeadDef(data.get(i).get(i1), i1, i));
            }
            this.table.add(cellRow);
        }
    }

    public void mergeCells() {
        for (int i = 0; i < table.size(); i++) {
            for (int j = 0; j < table.get(i).size(); ) {
                int end = j + 1;
                while (end < table.get(i).size() && table.get(i).get(j).getName().equals(table.get(i).get(end).getName())) {
                    end++;
                }
                if (end - j > 1) { // 如果有重复的单元格
                    table.get(i).get(j).colSpan = end - j;
                    // 移除合并的单元格
                    for (int k = j + 1; k < end; k++) {
                        table.get(i).remove(j + 1);
                    }
                }
                j += table.get(i).get(j).colSpan;
            }
        }
    }

    public void mergeRows() {
        for (int j = 0; j < table.get(0).size(); j++) {
            for (int i = 0; i < table.size(); ) {
                int end = i + 1;
                while (end < table.size() && table.get(i).get(j).getName().equals(table.get(end).get(j).getName()) && table.get(end).get(j).rowSpan == 1) {
                    end++;
                }
                if (end - i > 1) { // 如果有重复的单元格
                    table.get(i).get(j).rowSpan = end - i;
                    // 标记已合并的单元格
                    for (int k = i + 1; k < end; k++) {
                        table.get(k).get(j).rowSpan = 0; // 设置为0表示已被合并
                    }
                }
                i += table.get(i).get(j).rowSpan;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (List<ExcelHeadDef> row : table) {
            for (ExcelHeadDef cell : row) {
                sb.append(cell.getName()).append("(").append(cell.rowSpan).append(",").append(cell.colSpan).append(")\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}