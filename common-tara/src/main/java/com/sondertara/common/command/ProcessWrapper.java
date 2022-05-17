package com.sondertara.common.command;

import com.sondertara.common.util.StringUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * @author huangxiaohu
 */
@Data
public class ProcessWrapper implements Serializable {

    private String command;

    private boolean pipeline;
    private String hostName;
    private String ip;

    public ProcessWrapper(String command, String hostName, String ip) {
        this.command = command;
        this.hostName = hostName;
        this.ip = ip;
        this.pipeline = false;
        String s = StringUtils.removeWhiteLines(command);
        if (StringUtils.countLines(s) > 1) {
            this.pipeline = true;
        }
    }

    public ProcessWrapper(String command) {
        this.command = command;
        this.pipeline = false;
        String s = StringUtils.removeWhiteLines(command);
        if (StringUtils.countLines(s) > 1) {
            this.pipeline = true;
        }
    }
}
