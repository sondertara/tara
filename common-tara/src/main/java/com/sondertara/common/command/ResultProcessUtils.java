package com.sondertara.common.command;

import com.sondertara.common.model.ResultDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author huangxiaohu
 */
public class ResultProcessUtils {

    public static Process getProcess(String... commands) {
        Process pro = null;
        ProcessBuilder pb = new ProcessBuilder(commands);
        try {
            pro = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pro;
    }

    public static ResultDTO<String> process(int exitValue, Process pro, ProcessWrapper command) {
        StringBuilder result = new StringBuilder();
        String line;

        if (null != command.getHostName()) {
            result.append("hostName:").append(command.getHostName()).append("\n");
        }
        if (null != command.getIp()) {
            result.append("ip:").append(command.getIp()).append("\n");
        }
        ResultDTO<String> resultDTO = new ResultDTO<>();
        if (exitValue == 0) {
            InputStream in = pro.getInputStream();
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            try {
                while ((line = br.readLine()) != null) {
                    result.append(line).append("\n");
                }
                in.close();
                isr.close();
                br.close();
                pro.destroy();
                resultDTO.setCode("0");
                resultDTO.setData(result.toString());
                resultDTO.setSuccess(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            InputStream errIn = pro.getErrorStream();
            InputStreamReader errIsr = new InputStreamReader(errIn);
            BufferedReader errBr = new BufferedReader(errIsr);
            try {
                result.append("Command execute with error,exit status :").append(exitValue).append(".\n");
                while ((line = errBr.readLine()) != null) {
                    result.append(line).append("\n");
                }

                errIn.close();
                errIsr.close();
                errBr.close();
                pro.destroy();
                resultDTO.setMsg(result.toString());
                resultDTO.setCode(String.valueOf(exitValue));
                resultDTO.setSuccess(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultDTO;
    }
}
