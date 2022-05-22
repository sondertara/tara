package com.sondertara.excel;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

/**
 * @author chenzw
 */
public class ExcelXmlCodecUtils {

    public static int getColIndex(String r) {
        String s = r.replaceAll("\\d+", "");
        int index = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            byte b = (byte) c;
            index = (i * 26) + b - 65;
        }
        return index + 1;
    }

    public static int getTotalRow(String ref) {
        String sTotal = ref.substring(ref.indexOf(":") + 1).toUpperCase().replaceAll("[A-Z]", "");
        return Integer.valueOf(sTotal);
    }

    public static String getDataFormat(int styleIndex, StylesTable stylesTable) {
        XSSFCellStyle cellStyle = stylesTable.getStyleAt(styleIndex);
        String dataFormatString = cellStyle.getDataFormatString();
        if (dataFormatString == null) {
            short dataFormatIndex = cellStyle.getDataFormat();
            dataFormatString = BuiltinFormats.getBuiltinFormat(dataFormatIndex);
        }
        return dataFormatString;
    }

}
