package com.sondertara.excel.base;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * @author huangxiaohu
 */
public interface TaraExcelWriter {
    /**
     * write to OutputStream
     *
     * @param out out
     */
    void to(OutputStream out);

    /**
     * to HttpServletResponse
     *
     * @param httpServletResponse HttpServletResponse
     * @param fileName            filename
     */
    void to(HttpServletResponse httpServletResponse, String fileName);

}
