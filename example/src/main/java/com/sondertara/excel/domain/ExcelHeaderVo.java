package com.sondertara.excel.domain;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author huangxiaohu
 */
@Data
public class ExcelHeaderVo implements Serializable {

    int bottom;
    private List<Element> elements = new ArrayList<>();
    private int right;
    private int left;
    private int top;
    private int step;
    private String fillColor;

    private AtomicBoolean firstAdd = new AtomicBoolean(false);

    public void addElement(Element element) {

        AtomicInteger width = new AtomicInteger(0);
        AtomicInteger height = new AtomicInteger(element.height);
        this.step = Math.max(this.step, element.calcStep());

        if (this.firstAdd.compareAndSet(false, true)) {
            this.left = element.left();
            this.top = element.top();
        }
        if (element.rIndex == 0) {
            element.rIndex = this.top;
        }
        if (element.cIndex == 0) {
            element.cIndex = this.right;
        }
        calcIdx(element, height);

        this.bottom = Math.max(this.bottom, height.get()) + this.top;
        this.right = element.width + element.cIndex;
        this.elements.add(element);
    }


    private void calcIdx(Element element, AtomicInteger height) {
        int maxHeight = 0;
        for (Element child : element.children) {
            maxHeight = Math.max(maxHeight, child.height);
            child.cIndex = child.cIndex + element.cIndex;
            child.rIndex = child.rIndex + element.rIndex;
            if (!child.children.isEmpty()) {
                calcIdx(child, height);
            }
        }
        height.addAndGet(maxHeight);
        System.out.println("jj" + height);
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public List<Element> getElements() {
        return elements;
    }

    public int getBottom() {
        return bottom;
    }

    public int getRight() {
        return right;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    @ToString
    public static class Element implements Serializable {
        private String fillColor;
        private int rIndex = 0;
        private int cIndex = 0;
        private int width = 1;
        private int height = 1;
        private String name;
        private int nameLength;

        private int tmpWidth;

        private List<Element> children = new ArrayList<>();

        public Element position(int rIndex, int cIndex) {
            this.rIndex = rIndex;
            this.cIndex = cIndex;
            this.tmpWidth = 0;
            return this;
        }

        public String getName() {
            return name;
        }

        public int getrIndex() {
            return rIndex;
        }

        public int getcIndex() {
            return cIndex;
        }

        public List<Element> getChildren() {
            return children;
        }

        public void setChildren(List<Element> children) {
            this.children = children;
        }

        public Element size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Element fillColor(String fillColor) {
            this.fillColor = fillColor;
            return this;
        }

        public int top() {
            return this.rIndex;
        }

        public int bottom() {
            return this.rIndex + this.height;
        }

        public int right() {
            return this.cIndex + this.width;
        }

        public int left() {
            return this.cIndex;
        }

        int calcStep() {
            int step = 0;
            try {
                if (name == null) {
                    name = "";
                }
                for (Element child : this.children) {
                    step = Math.max(step, child.calcStep() / child.width);
                }
                String gb2312 = new String(name.getBytes("GB2312"), StandardCharsets.ISO_8859_1);
                System.out.println(gb2312.length());
                this.nameLength = gb2312.length();
                return Math.max(step, this.nameLength / this.width);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        public Element name(String name) {
            this.name = name;
            return this;
        }

        public Element addChild(Element child) {
            if (child.rIndex == 0) {
                child.rIndex = bottom();
            }
            if (child.cIndex == 0) {
                child.cIndex = this.cIndex + tmpWidth;
            }
            tmpWidth += child.width;
            this.width = tmpWidth;
            this.children.add(child);
            return this;
        }
    }
}
