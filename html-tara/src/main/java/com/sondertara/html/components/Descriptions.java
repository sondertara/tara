package com.sondertara.html.components;

import com.sondertara.common.struct.Pair;
import com.sondertara.common.collection.Lists;
import j2html.tags.DomContent;
import j2html.tags.specialized.DivTag;

import java.util.List;
import java.util.Optional;

import static j2html.TagCreator.*;


public class Descriptions extends BaseComponent {
    private List<Pair<String, String>> items;

    private  int colSize = 3;

    @Override
    public DomContent build() {
        List<List<Pair<String, String>>> list = Lists.partition(items, colSize);
        DivTag divTag = div(attrs(".ant-descriptions-view"),
                table(tbody(
                                each(list, pairs ->
                                        tr(attrs(".ant-descriptions-row"),
                                                each(pairs, pair ->
                                                        td(attrs(".ant-descriptions-item"),
                                                                section(attrs(".ant-descriptions-item-container"))
                                                                        .with(aside(attrs(".ant-descriptions-item-label"), pair.getKey()))
                                                                        .with(section(attrs(".ant-descriptions-item-content"), Optional.ofNullable(pair.getValue()).orElse("")))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
        return divTag;
    }

    Descriptions(List<Pair<String, String>> items, int colSize) {
        this.items = items;
        this.colSize = colSize;
        init();
    }

    public static DescriptionsBuilder builder() {
        return new DescriptionsBuilder();
    }

    public static class DescriptionsBuilder {
        private List<Pair<String, String>> items;
        private int colSize = 3;

        DescriptionsBuilder() {
        }

        public DescriptionsBuilder items(List<Pair<String, String>> items) {
            this.items = items;
            return this;
        }

        public DescriptionsBuilder colSize(int colSize) {
            this.colSize = colSize;
            return this;
        }

        public Descriptions build() {
            return new Descriptions(this.items, this.colSize);
        }


        @Override
        public String toString() {
            return "Descriptions.DescriptionsBuilder(items=" + this.items + ", colSize=" + this.colSize + ")";
        }
    }

}
