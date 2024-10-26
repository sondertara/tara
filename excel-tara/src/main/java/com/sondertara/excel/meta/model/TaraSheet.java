package com.sondertara.excel.meta.model;

import lombok.Data;
import org.apache.poi.ss.usermodel.SheetVisibility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author huangxiaohu
 */
@Data
public class TaraSheet implements Iterable<TaraRow>, ExcelSheetVisitor {
    /**
     * zero-based
     */
    public int sheetIndex;
    protected String name;


    protected boolean hasTitle = true;
    protected int firstDataRow=1;

    private Map<Integer, String> titles = new LinkedHashMap<>();
    protected List<TaraRow> rows = new ArrayList<>();

    private int rowCount = 0;

    private SheetVisibility visibility;

    public TaraSheet(int sheetIndex, String name, SheetVisibility visibility) {
        this.sheetIndex = sheetIndex;
        this.name = name;
        this.visibility = visibility;
    }

    public TaraSheet(int sheetIndex, String name) {
        this.sheetIndex = sheetIndex;
        this.name = name;
    }

    public TaraSheet(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public TaraSheet(int sheetIndex, SheetVisibility visibility) {

        this(sheetIndex, null, null);
    }

    @Override
    public Iterator<TaraRow> iterator() {
        return rows.iterator();
    }

    @Override
    public void forEach(Consumer<? super TaraRow> action) {
        rows.iterator().forEachRemaining(action);
    }


    public void addRow(TaraRow row) {
        this.rows.add(row);
    }

    @Override
    public int firstDataRow() {
        return this.firstDataRow;
    }
}
