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
public class TaraSheet implements Iterable<TaraRow>, Comparable<TaraSheet> {
    public int index;
    protected String name;

    private Map<Integer, String> titles = new LinkedHashMap<>();
    protected List<TaraRow> rows = new ArrayList<>();

    private int rowCount = 0;

    private SheetVisibility visibility;

    public TaraSheet(int index, String name, SheetVisibility visibility) {
        this.index = index;
        this.name = name;
        this.visibility = visibility;
    }

    public TaraSheet(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public TaraSheet(int index) {
        this.index = index;
    }

    public TaraSheet(int index, SheetVisibility visibility) {

        this(index, null, null);
    }

    @Override
    public Iterator<TaraRow> iterator() {
        return rows.iterator();
    }

    @Override
    public void forEach(Consumer<? super TaraRow> action) {
        rows.iterator().forEachRemaining(action);
    }

    @Override
    public int compareTo(TaraSheet o) {
        return 0;
    }

    public void addRow(TaraRow row) {
        this.rows.add(row);
    }
}
