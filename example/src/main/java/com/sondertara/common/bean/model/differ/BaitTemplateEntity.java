package com.sondertara.common.bean.model.differ;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sondertara
 * @since 2022/4/27 下午 04:37
 */
@Data
public class BaitTemplateEntity {
    private Integer id;

    private String name;

    private String osType;

    private List<FileBaitEntity> fileBaitList = new ArrayList<>();

    private List<ArpBaitEntity> arpBaitList = new ArrayList<>();

    private List<ProcessBaitEntity> processBaitList = new ArrayList<>();
}
