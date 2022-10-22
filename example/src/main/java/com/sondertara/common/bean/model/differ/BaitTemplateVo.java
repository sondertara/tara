package com.sondertara.common.bean.model.differ;

import lombok.Data;

import java.util.List;

/**
 * @author sondertara
 * @since 2022/4/27 下午 02:39
 */
@Data
public class BaitTemplateVo {
    private Long id;

    private String name;

    private String osType;

    private List<FileBaitVo> fileBaitList;

    private List<ArpBaitVo> arpBaitList;

    private List<ProcessBaitVo> processBaitList;
}
