package com.sondertara.common.command;

/**
 * @author huangxiaohu
 */
public class ParameterProcess {
    public Process parameterProcess(OSType osType, ProcessWrapper command) {
        Process pro = null;
        if (osType == OSType.UNIX || osType == OSType.LINUX || osType == OSType.MAC) {
            String[] commands = { "/bin/sh", "-c", command.getCommand() };
            pro = ResultProcessUtils.getProcess(commands);
        } else if (osType == OSType.WINDOWS) {
            String[] commands = { "cmd /C", command.getCommand() };
            pro = ResultProcessUtils.getProcess(commands);
        } else {
            String commands = command.getCommand();
            pro = ResultProcessUtils.getProcess(commands);
        }
        return pro;
    }
}
