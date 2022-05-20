package com.sondertara.excel.parser;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * @author huangxiaohu
 */
public interface TaraExcelWriter {

    void to(OutputStream out);

    void to(HttpServletResponse httpServletResponse, String fileName);


}
