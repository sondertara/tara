package org.cherubim.excel.entity;

import lombok.Builder;
import lombok.Data;
import org.cherubim.excel.common.Constant;

/**
 * @author huangxiaohu
 */
@Builder
@Data
public class ExcelHelper {
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 用户空间
     */
    private String receiptUser;
    /**
     * 起始页
     */
    private Integer pageStart;
    /**
     * 截止页
     */
    private Integer pageEnd;
    /**
     * 分页大小，默认20000
     *
     * @see Constant#DEFAULT_PAGE_SIZE
     */
    private Integer pageSize;
}
