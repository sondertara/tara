package com.sondertara.html.components;

import j2html.tags.DomContent;
import j2html.tags.specialized.DivTag;
import lombok.Getter;

import static j2html.TagCreator.attrs;
import static j2html.TagCreator.div;


/**
 * @author huangxiaohu
 */

@Getter
public class Card extends BaseComponent {
    private String title;
    private DomContent content;

    @Override
    public DomContent build() {
        DivTag divTag = div(attrs(".ant-card"), div(title).withClass("ant-card-head"), content);
        return divTag;
    }

    Card(String title, DomContent content) {
        this.title = title;
        this.content = content;
        init();
    }

    public static CardBuilder builder() {
        return new CardBuilder();
    }

    public String getTitle() {
        return this.title;
    }

    public DomContent getContent() {
        return this.content;
    }

    public static class CardBuilder {
        private String title;
        private DomContent content;

        CardBuilder() {
        }

        public CardBuilder title(String title) {
            this.title = title;
            return this;
        }

        public CardBuilder content(DomContent content) {
            this.content = content;
            return this;
        }

        public Card build() {
            return new Card(this.title, this.content);
        }

        public String toString() {
            return "Card.CardBuilder(title=" + this.title + ", content=" + this.content + ")";
        }
    }


}
