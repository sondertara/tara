
package com.sondertara.excel.common;

import java.io.File;

/**
 * @author huangxiaohu
 */
public final class Constant {

    /**
     * The name of a supported {@linkplain java.nio.charset.Charset charset}
     */
    public static String CHARSET = "GBK";
    /**
     * row cached in memory,if row counts greater than this will flush to disk.
     */
    public static final int DEFAULT_ROW_ACCESS_WINDOW_SIZE = 2000;
    /**
     * query page size.
     */
    public static int DEFAULT_PAGE_SIZE = 2000;
    /**
     * one sheet row size when export multi sheets
     */
    public static final int DEFAULT_RECORD_COUNT_PEER_SHEET = 80000;
    /**
     * enable cell  auto column width,if open will reduce performance.
     */
    public static final boolean OPEN_AUTO_COLUMN_WIDTH = false;
    /**
     * enable set style to a cell
     */
    public static final boolean OPEN_CELL_STYLE = false;

    /**
     * cell symbol in xml
     */
    public static final String CELL = "c";
    /**
     * cell location symbol in xml
     */
    public static final String XYZ_LOCATION = "r";
    /**
     * cell type symbol in xml
     */
    public static final String CELL_T_PROPERTY = "t";
    /**
     * cell value symbol in xml
     */
    public static final String CELL_S_VALUE = "s";
    /**
     * row element symbol in xml
     */
    public static final String ROW = "row";
    /***
     * auto size width for chinese
     */
    public static final int CHINESES_ATUO_SIZE_COLUMN_WIDTH_MAX = 60;
    public static final int CHINESES_ATUO_SIZE_COLUMN_WIDTH_MIN = 15;
    /**
     * maximum row count to open multi sheets export
     */
    public static final int MAX_RECORD_COUNT_PEER_SHEET = 1000000;

    /**
     * async csv file workspace.
     */
    public static final String FILE_PATH = System.getProperties().getProperty("user.dir") + File.separator + "export" + File.separator;
    /**
     * the num query thread
     */
    public static final int PRODUCER_COUNT = 2;
    /**
     * the number handle thread
     */
    public static final int CONSUMER_COUNT = 4;
    /**
     * the workspace
     */
    public static final String FILE_STORAGE_PREFIX = "TARA_FILE_DIR";
}
