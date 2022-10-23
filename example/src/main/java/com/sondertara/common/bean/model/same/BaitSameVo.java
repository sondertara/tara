package com.sondertara.common.bean.model.same;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author sondertara
 * @since 2022/4/27 下午 02:39
 */
@Data
public class BaitSameVo {
    private Long id;

    private String name;

    private String osType;
    private List<String> users;
    private Map<String, String> apps;
}
