package com.sondertara.excel.utils;

import com.sondertara.common.exception.TaraException;
import com.sondertara.common.util.StringUtils;
import com.sondertara.excel.common.constants.Constants;
import com.sondertara.excel.exception.ExcelException;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * @author huangxiaohu
 */
public class ExcelResponseUtils {

    /**
     * 获取内建的response
     *
     * @param response
     * @param fileName
     * @return
     */
    public static HttpServletResponse getBuiltinResponse(HttpServletResponse response, String fileName) {
        response.setContentType(Constants.OCTET_STREAM_CONTENT_TYPE);

        if (StringUtils.isBlank(fileName)) {
            fileName = "template.xlsx";
        } else if (!StringUtils.endWithAny(fileName, Constants.EXCEL_FILE_SUFFIX)) {
            fileName = fileName + ".xlsx";
        }

        try {
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new ExcelException("文件名编码转换异常！");
        }

        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + "; filename*=utf-8''" + fileName);
        return response;
    }

    public static void writeResponse(HttpServletResponse httpServletResponse, String fileName, Consumer<OutputStream> consumer) {
        try (OutputStream out = httpServletResponse.getOutputStream()) {
            if (!fileName.endsWith(Constants.CSV_SUFFIX)) {
                int indexOf = fileName.lastIndexOf(".");
                if (indexOf > 0) {
                    fileName = fileName.substring(0, indexOf) + Constants.EXCEL_FILE_SUFFIX[0];
                } else {
                    fileName = fileName + Constants.EXCEL_FILE_SUFFIX[0];
                }
            }
            httpServletResponse.setContentType(Constants.OCTET_STREAM_CONTENT_TYPE);
            String s = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            httpServletResponse.setHeader("Content-disposition", "attachment; filename=" + s);
            httpServletResponse.flushBuffer();
            consumer.accept(out);
        } catch (Exception e) {
            throw new TaraException("Download Excel error", e);

        }

    }
}
