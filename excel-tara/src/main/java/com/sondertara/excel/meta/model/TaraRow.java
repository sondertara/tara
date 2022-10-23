package com.sondertara.excel.meta.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author huangxiaohu
 */
@Data
public class TaraRow implements Iterable<TaraCell>, Serializable {

    private long rowIndex;

    protected Object rowData;

    private final List<TaraCell> cells;
    private final int physicalCellCount;

    public TaraRow(long rowIndex, int physicalCellCount) {
        this.rowIndex = rowIndex;
        this.physicalCellCount = physicalCellCount;
        this.cells = new ArrayList<>();
    }

    /**
     * Returns a cell in this row by column index;
     *
     * @param index - zero-based column index
     * @return Cell value
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public TaraCell getCell(int index) {
        return cells.get(index);
    }

    public List<TaraCell> getCells(int beginIndex, int endIndex) {
        return cells.subList(beginIndex, endIndex);
    }

    public Optional<TaraCell> getOptionalCell(int index) {
        return index < 0 || index >= cells.size() ? Optional.empty() : Optional.ofNullable(cells.get(index));
    }

    // public Optional<TaraCell> getFirstNonEmptyCell() {
    // return stream().filter(Objects::nonNull).filter(cell ->
    // !cell.getText().isEmpty()).findFirst();
    // }

    public int getCellCount() {
        return cells.size();
    }

    public boolean hasCell(int index) {
        return index >= 0 && index < cells.size() && cells.get(index) != null;
    }

    public int getPhysicalCellCount() {
        return physicalCellCount;
    }

    @Override
    public String toString() {
        return "Row " + rowIndex + ' ' + cells;
    }

    @Override
    public Iterator<TaraCell> iterator() {
        return cells.iterator();
    }

    public Stream<TaraCell> stream() {
        return cells.stream();
    }

    public void addCell(TaraCell cell) {
        this.cells.add(cell);
    }
}
