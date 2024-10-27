package com.sondertara.excel.meta;

import com.sondertara.common.struct.weapper.Wrapper;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

/**
 * TODO
 *
 * @author huangxiaohu.1ih
 * @date 2024/8/20 17:03
 */
public interface MyWorkBook extends Wrapper {
    CellStyle createCellStyle();

    Font createFont();
}
