package com.sondertara.html.components.table;

import com.sondertara.html.components.BaseComponent;
import j2html.tags.DomContent;
import j2html.tags.specialized.TableTag;
import j2html.utils.EscapeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static j2html.TagCreator.each;
import static j2html.TagCreator.table;
import static j2html.TagCreator.td;
import static j2html.TagCreator.tr;

/**
 * @author huangxiaohu
 */
public class RawTableView extends BaseComponent {

    private List<List<String>> data;

    private String withClass;

    private Map<Integer, Integer> colspanMap;

    @Override
    public DomContent build() {
        TableTag tableTag = table(each(data, row -> tr(each(row, (index, cell) -> {
            if (colspanMap.get(index) != null) {
                return td(cell).attr("colspan", colspanMap.get(index));
            }
            return td(cell);
        })))).withClasses("grid-table", withClass);
        return tableTag;
    }

    RawTableView(List<List<String>> data, String withClass, Map<Integer, Integer> colspanMap) {
        this.data = data;
        this.withClass = withClass;
        this.colspanMap = colspanMap == null ? new HashMap<>(1) : colspanMap;

        init();
    }

    public static RawTableViewBuilder builder() {
        return new RawTableViewBuilder();
    }

    public static class RawTableViewBuilder {
        private List<List<String>> data;
        private String withClass;

        private Map<Integer, Integer> colspan;

        private RawTableViewBuilder() {
        }

        public RawTableViewBuilder data(List<List<String>> data) {
            this.data = data;
            return this;
        }

        public RawTableViewBuilder colSpan(Map<Integer, Integer> colspan) {
            this.colspan = colspan;
            return this;
        }


        public RawTableViewBuilder withClass(String withClass) {
            this.withClass = withClass;
            return this;
        }

        public RawTableView build() {
            return new RawTableView(this.data, this.withClass, this.colspan);
        }

        @Override
        public String toString() {
            return "RawTableView.RawTableViewBuilder(data=" + this.data + ", withClass=" + this.withClass + ")";
        }
    }
}
