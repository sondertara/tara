package com.sondertara.common.excel.model;

import com.alibaba.fastjson.JSON;
import com.sondertara.common.util.MapUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author: chenxinshi
 * @date: 2018/8/14
 * @desc: 导入失败结果dto，调用ExcelUtil.getImportResult()获取ImportResultDTO
 */
@Data
public class ImportFailureDTO implements Serializable {

    private ImportFailureDTO() {
    }

    public static ImportFailureDTO getImportFailure(ImportBaseDTO importBaseDTO) {
        return getImportFailure(importBaseDTO, importBaseDTO.getCode(), importBaseDTO.getMessage());
    }

    public static ImportFailureDTO getImportFailure(ImportBaseDTO importBaseDTO, String code, String message) {
        ImportFailureDTO importFailureDTO = new ImportFailureDTO();
        importFailureDTO.setRowNum(importBaseDTO.getRowNum());
        importFailureDTO.setCode(code);
        importFailureDTO.setMessage(message);
        importFailureDTO.setExtra(MapUtil.toMap(JSON.toJSONString(importBaseDTO)));
        return importFailureDTO;
    }

    /**
     * 行号
     */
    private Integer rowNum;

    /**
     * 失败编码
     */
    private String code;

    /**
     * 失败信息
     */
    private String message;

    /**
     * 额外信息
     */
    private Map extra;
}
