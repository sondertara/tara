package com.sondertara.excel.meta;

/**
 * TODO
 *
 * @author huangxiaohu.1ih
 * @date 2024/8/20 16:42
 */
public interface MyRow {


    /**
     * Set the row's height in points.
     *
     * @param height the height in points. <code>-1</code>  resets to the default height
     */
    void setHeightInPoints(float height);

    int getRowNum();

    MyCell createCell(int cellIndex);

    void setHeight(short i);
}
