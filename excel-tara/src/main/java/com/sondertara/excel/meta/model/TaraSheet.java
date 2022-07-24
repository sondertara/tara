package com.sondertara.excel.meta.model;

import lombok.Data;
import org.apache.poi.ss.usermodel.SheetVisibility;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


@Data
public class TaraSheet implements Iterable<TaraRow>, Comparable<TaraSheet> {
    private SXSSFSheet sheet;
    private int index;
    protected String name;

    private Map<Integer, String> titles = new LinkedHashMap<>();
    protected List<TaraRow> rows;

    private SheetVisibility visibility;

    public TaraSheet(SXSSFSheet sxssfSheet, int index) {
        this.sheet = sxssfSheet;
        this.index = index;
        this.name = sxssfSheet.getSheetName();

    }

    public TaraSheet(SXSSFSheet sxssfSheet, int index, SheetVisibility visibility) {
        this.sheet = sheet;
        this.index = index;
        this.name = sxssfSheet.getSheetName();
        this.visibility = visibility;
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
}
