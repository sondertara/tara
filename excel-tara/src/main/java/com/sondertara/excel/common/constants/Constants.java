package com.sondertara.excel.common.constants;

import java.io.File;

/**
 * @author huangxiaohu
 */
public final class Constants {

    /**
     * row cached in memory,if row counts greater than this will flush to disk.
     */
    public static final int DEFAULT_ROW_ACCESS_WINDOW_SIZE = 2000;
    /**
     * one sheet row size when export multi sheets
     */
    public static final int DEFAULT_RECORD_COUNT_PEER_SHEET = 500_000;
    /**
     * enable cell auto column width,if open will reduce performance.
     */
    public static final boolean OPEN_AUTO_COLUMN_WIDTH = true;
    /**
     * enable set style to a cell
     */
    public static final boolean OPEN_CELL_STYLE = true;
    /***
     * auto size width for chinese
     */
    public static final int CHINESE_AUTO_SIZE_COLUMN_WIDTH_MAX = 60;
    public static final int CHINESE_AUTO_SIZE_COLUMN_WIDTH_MIN = 15;
    /**
     * async csv file workspace.
     */
    public static final String FILE_PATH = System.getProperties().getProperty("java.io.tmpdir") + File.separator + "export_tmp" + File.separator;
    /**
     * the num query thread
     */
    public static final int PRODUCER_COUNT = Runtime.getRuntime().availableProcessors() * 2;
    /**
     * the number handle thread
     */
    public static final int CONSUMER_COUNT = 4;
    public static final String OCTET_STREAM_CONTENT_TYPE = "application/octet-stream";
    public static final String[] EXCEL_FILE_SUFFIX = new String[]{".xlsx", ".xls"};
    public static final int DEFAULT_COL_WIDTH = 16;
    /**
     * The name of a supported {@linkplain java.nio.charset.Charset charset}
     */
    public static String CHARSET = "GBK";
    public static String CSV_SUFFIX = ".csv";
}
