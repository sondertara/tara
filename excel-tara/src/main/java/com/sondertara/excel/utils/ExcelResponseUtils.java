package com.sondertara.excel.utils;

import com.sondertara.excel.constants.ExcelConstants;
import com.sondertara.excel.exception.ExcelException;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

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
        response.setContentType(ExcelConstants.OCTET_STREAM_CONTENT_TYPE);

        if (StringUtils.isBlank(fileName)) {
            fileName = "template.xlsx";
        } else if (!StringUtils.endsWithAny(fileName, ".xlsx", ".xls")) {
            fileName = fileName + ".xlsx";
        }

        try {
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new ExcelException("文件名编码转换异常！");
        }

        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + fileName + "\"; filename*=utf-8''" + fileName);
        return response;
    }
}
