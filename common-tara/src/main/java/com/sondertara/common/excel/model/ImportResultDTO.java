package com.sondertara.common.excel.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: chenxinshi
 * @date: 2018/8/14
 * @desc: 导入结果dto，调用ExcelUtil.getImportResult()
 */
@Data
public class ImportResultDTO implements Serializable {

    /**
     * 导入总记录数
     */
    private Integer total;

    /**
     * 每条导入失败记录的失败信息
     */
    private List<ImportFailureDTO> failureList;

    /**
     * 导入失败记录文件url
     */
    private String url;


    public static ImportResultDTO getResult(Integer total, List<ImportFailureDTO> importFailureDTOS) {
        ImportResultDTO importResultDTO = new ImportResultDTO();
        importResultDTO.setTotal(total);
        importResultDTO.setFailureList(importFailureDTOS);
        return importResultDTO;
    }

}
