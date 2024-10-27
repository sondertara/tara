package com.sondertara.html.components.table;

import lombok.Getter;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author huangxiaohu
 */
@Getter
public class TableCol implements Serializable {

    private String dataIndex;

    private String title;
    private String index;

    private boolean autoMerge;

    private int colspan;

    private Function<String, Boolean> validator;

    private final Function<String, String> convertor;

    public static TableColBuilder builder() {
        return new TableColBuilder();
    }


    TableCol(String dataIndex, String title, String index, boolean autoMerge, int colspan, Function<String, Boolean> validator, Function<String, String> convertor) {
        this.dataIndex = dataIndex;
        this.title = title==null?dataIndex:title;
        this.index = index;
        this.autoMerge = autoMerge;
        this.colspan = colspan;
        this.validator = validator;
        this.convertor = convertor;
    }

    public static class TableColBuilder {
        private String dataIndex;

        private String title;
        private String index;
        private boolean autoMerge;

        private int colspan;
        private Function<String, Boolean> validator;
        private Function<String, String> convertor;

        TableColBuilder() {
        }

        public TableColBuilder dataIndex(String dataIndex) {
            this.dataIndex = dataIndex;
            return this;
        }

        public TableColBuilder title(String title) {
            this.title = title;
            return this;
        }

        public TableColBuilder colspan(int colspan) {
            this.colspan = colspan;
            return this;
        }


        public TableColBuilder index(String index) {
            this.index = index;
            return this;
        }

        public TableColBuilder autoMerge(boolean autoMerge) {
            this.autoMerge = autoMerge;
            return this;
        }

        public TableColBuilder validator(Function<String, Boolean> validator) {
            this.validator = validator;
            return this;
        }

        public TableColBuilder convertor(Function<String, String> convertor) {
            this.convertor = convertor;
            return this;
        }


        public TableCol build() {
            if (null == this.validator) {
                this.validator = s -> true;
            }
            if (null == this.convertor) {
                this.convertor = s -> s;
            }
            return new TableCol(this.dataIndex, title, this.index, this.autoMerge, colspan, this.validator, convertor);
        }

        @Override
        public String toString() {
            return "TableCol.TableColBuilder(dataIndex=" + this.dataIndex + ", index=" + this.index + ", autoMerge=" + this.autoMerge + ", validator=" + this.validator + ")";
        }
    }
}
