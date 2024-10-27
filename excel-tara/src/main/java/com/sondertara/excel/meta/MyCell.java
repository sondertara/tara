package com.sondertara.excel.meta;

import com.sondertara.common.struct.weapper.Wrapper;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * TODO
 *
 * @author huangxiaohu.1ih
 * @date 2024/8/20 17:10
 */
public interface MyCell extends Wrapper {
    void setCellValue(Object title);

    void setCellStyle(CellStyle build);
}
