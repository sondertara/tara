
package com.sondertara.excel.common;

import java.io.File;

/**
 * @author huangxiaohu
 */
public final class Constant {

    public static final String CHARSET = "GBK";
    /**
     * Excel自动刷新到磁盘的数量
     */
    public static final int DEFAULT_ROW_ACCESS_WINDOW_SIZE = 2000;
    /**
     * 分页条数
     */
    public static int DEFAULT_PAGE_SIZE = 2000;
    /**
     * 分Sheet条数
     */
    public static final int DEFAULT_RECORD_COUNT_PEER_SHEET = 80000;
    /**
     * 是否开启自动适配宽度,影响性能
     */
    public static final boolean OPEN_AUTO_COLUMN_WIDTH = false;

    public static final boolean OPEN_CELL_STYLE = false;


    public static final String CELL = "c";
    public static final String XYZ_LOCATION = "r";
    public static final String CELL_T_PROPERTY = "t";
    public static final String CELL_S_VALUE = "s";
    public static final String ROW = "row";
    /***
     * 中文自适应
     */
    public static final int CHINESES_ATUO_SIZE_COLUMN_WIDTH_MAX = 60;
    public static final int CHINESES_ATUO_SIZE_COLUMN_WIDTH_MIN = 15;

    public static final int MAX_RECORD_COUNT_PEER_SHEET = 1000000;

    /**
     * 生成文件目录
     */
    public static final String FILE_PATH = System.getProperties().getProperty("user.dir") + File.separator + "export" + File.separator;
    /**
     * 生产线程数
     */
    public static final int PRODUCER_COUNT = 2;
    /**
     * 消费线程数
     */
    public static final int CONSUMER_COUNT = 4;
}
