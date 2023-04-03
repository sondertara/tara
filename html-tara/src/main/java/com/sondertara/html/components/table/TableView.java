package com.sondertara.html.components.table;

import com.google.common.collect.ArrayTable;
import com.sondertara.common.util.CollectionUtils;
import com.sondertara.common.util.StringUtils;
import com.sondertara.html.components.BaseComponent;
import j2html.tags.DomContent;
import j2html.tags.Text;
import j2html.tags.specialized.TableTag;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static j2html.TagCreator.attrs;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.table;
import static j2html.TagCreator.td;
import static j2html.TagCreator.th;
import static j2html.TagCreator.tr;

/**
 * @author huangxiaohu
 */
@SuppressWarnings("UnstableApiUsage")
public class TableView extends BaseComponent {

    private  DomContent header;

    private  DomContent footer;

    private  boolean hasTitle;

    private  String withClass;

    private List<TableCol> columns;

    private  List<Map<String, Object>> data;

    private List<Integer> rowIndexList;

    TableView(DomContent header, DomContent footer, boolean hasTitle, String withClass, List<TableCol> columns, List<Map<String, Object>> data) {
        this.header = header;
        this.footer = footer;
        this.hasTitle = hasTitle;
        this.withClass = withClass;
        this.columns = columns;
        this.data = data;
        init();
    }

    public static TableViewBuilder builder() {
        return new TableViewBuilder();
    }

    @Override
    public DomContent build() {
        DomContent headerDivTag = new Text("");
        if (header != null) {
            headerDivTag = div(attrs(".table-title"), header);
        }
        Map<String, TableCol> colMap = columns.stream().collect(Collectors.toMap(TableCol::getDataIndex, Function.identity()));
        if (CollectionUtils.isEmpty(data)) {
            TableTag tableTag = table(each(columns, tableCol -> th(tableCol.getDataIndex()).withCondHidden(!hasTitle))).withClasses("grid-table", withClass);
            return div(headerDivTag, tableTag, footer);
        }
        rowIndexList = IntStream.range(0, data.size()).boxed().collect(Collectors.toList());


        ArrayTable<Integer, String, TableCell> table = initTable(colMap);
        TableTag tableTag = table(each(columns, tableCol -> th(tableCol.getTitle()).withCondHidden(!hasTitle)), each(rowIndexList, rowIndex -> {

            Map<String, TableCell> row = table.row(rowIndex);

            AtomicReference<String> trClass = new AtomicReference<>("none");

            return tr(each(row, cell -> {
                TableCell tableCell = cell.getValue();
                int rowSpan = tableCell.getRowSpan();
                if (rowSpan == 0) {
                    return new Text("");
                }

                String cellValue = "";
                if (StringUtils.hasText(tableCell.getValue())) {
                    cellValue = tableCell.getValue();
                }
                String tdClass = "none";
                TableCol tableCol = colMap.get(tableCell.getColumnName());
                if (tableCol != null && tableCol.getValidator() != null) {
                    Boolean validate = tableCol.getValidator().apply(cellValue);
                    if (!validate) {
                        trClass.set("font-red");
                        tdClass = "warning-td";

                    }
                }
                if (tableCell.getColspan() > 0) {
                    return td(cellValue).attr("rowspan", rowSpan).attr("colspan", tableCell.getColspan()).withClass(tdClass);
                }
                return td(cellValue).attr("rowspan", rowSpan).withClass(tdClass);

            })).withClass(trClass.get());
        })).withClasses("grid-table", withClass);

        return div(headerDivTag, tableTag, footer);
    }

    @SuppressWarnings("UnstableApiUsage")
    private ArrayTable<Integer, String, TableCell> initTable(final Map<String, TableCol> colMap) {
        List<String> colNames = columns.stream().map(TableCol::getDataIndex).collect(Collectors.toList());
        ArrayTable<Integer, String, TableCell> table = ArrayTable.create(rowIndexList, colNames);
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> row = data.get(i);
            for (String colName : colNames) {

                String value = Optional.ofNullable(row.get(colName)).map(Object::toString).orElse("");
                TableCol tableCol = colMap.get(colName);
                if (null != tableCol.getConvertor()) {
                    value = tableCol.getConvertor().apply(value);
                }
                TableCell tableCell = TableCell.builder().rowIndex(i).columnName(colName).rowSpan(1).value(value).build();
                table.put(i, colName, tableCell);
            }
        }

        for (String s : table.columnKeySet()) {
            if (!colMap.get(s).isAutoMerge()) {
                continue;
            }
            Map<Integer, TableCell> cellMap = table.column(s);
            int rowSpan = 1;
            TableCell prev = cellMap.get(0);
            for (int i = 1; i < cellMap.keySet().size(); i++) {
                if (Objects.equals(cellMap.get(i).getValue(), prev.getValue())) {
                    rowSpan++;
                    prev.setRowSpan(rowSpan);
                    cellMap.get(i).setRowSpan(0);
                } else {
                    rowSpan = 1;
                    prev = cellMap.get(i);
                }
            }
        }

        return table;
    }


    public static class TableViewBuilder {
        private DomContent header;
        private DomContent footer;
        private boolean hasTitle = true;
        private String withClass;
        private List<TableCol> columns;
        private List<Map<String, Object>> data;

        TableViewBuilder() {
        }

        public TableViewBuilder header(DomContent header) {
            this.header = header;
            return this;
        }

        public TableViewBuilder footer(DomContent footer) {
            this.footer = footer;
            return this;
        }

        public TableViewBuilder hasTitle(boolean hasTitle) {
            this.hasTitle = hasTitle;
            return this;
        }

        public TableViewBuilder withClass(String withClass) {
            this.withClass = withClass;
            return this;
        }

        public TableViewBuilder columns(List<TableCol> columns) {
            this.columns = columns;
            return this;
        }

        public TableViewBuilder data(List<Map<String, Object>> data) {
            this.data = data;
            return this;
        }

        public TableView build() {
            return new TableView(this.header, this.footer, this.hasTitle, this.withClass, this.columns, this.data);
        }

        @Override
        public String toString() {
            return "TableView.TableViewBuilder(header=" + this.header + ", footer=" + this.footer + ", hasTitle=" + this.hasTitle + ", withClass=" + this.withClass + ", columns=" + this.columns + ", data=" + this.data + ")";
        }
    }
}
