package com.sondertara.common.command;

import com.sondertara.common.io.FileUtils;
import com.sondertara.common.model.ResultDTO;
import com.sondertara.common.util.StringFormatter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @author huangxiaohu
 */
public class ExecOsCommand {

    public static ResultDTO<String> getCommandResult(ProcessWrapper command) {

        OSType osType = OSType.getOsType();
        if (osType == null) {
            return ResultDTO.fail("UNKNOWN_OS_TYPE", "操作系统无法识别");
        }
        String scriptFile = null;
        if (command.isPipeline()) {
            scriptFile = generateScriptFile(command.getCommand(), osType == OSType.WINDOWS);
            command.setCommand(scriptFile);
        }
        // 组装Process
        ParameterProcess pp = new ParameterProcess();
        Process pro = pp.parameterProcess(osType, command);
        int exitValue;
        try {
            exitValue = pro.waitFor();
        } catch (InterruptedException e1) {
            return ResultDTO.fail(StringFormatter.format("Exec command failed:{}", e1.getMessage()));
        } finally {
            if (null != scriptFile) {
               FileUtils.del(new File(scriptFile));
            }
        }
        // 处理Process返回结果
        return ResultProcessUtils.process(exitValue, pro, command);

    }

    private static String generateScriptFile(String command, boolean isWindows) {
        String dirName = System.getProperty("java.io.tmpdir");
        StringBuilder sb = new StringBuilder(dirName);
        String suffix = isWindows ? ".bat" : ".sh";
        try {
            File file = new File(sb.append(File.separator).append("tara_").append(UUID.randomUUID()).append(suffix).toString());
            FileUtils.writeString(command,file, StandardCharsets.UTF_8);

            FileUtils.writeString(cleanupScript(command),file, StandardCharsets.UTF_8);
            String path = file.getAbsolutePath();
            if (!isWindows) {
                executeCommand("chmod", "u+x", path);
            }
            return path;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getScriptPath(String workingDirectory, String scriptFileName) {
        return Paths.get(workingDirectory, scriptFileName);
    }

    private void createScript(String workingDirectory, String scriptFileName, Boolean isWindows, String scriptValue) throws IOException, InterruptedException {
        Path scriptPath = getScriptPath(workingDirectory, scriptFileName);
        FileUtils.writeString( cleanupScript(scriptValue), scriptPath.toFile(),StandardCharsets.UTF_8);

        if (!isWindows) {
            executeCommand(workingDirectory, null, "chmod", "u+x", scriptFileName);
        }

    }

    private static String cleanupScript(String scriptValue) {
        return scriptValue.replaceAll("(\\r\\n|\\n|\\r)", System.getProperty("line.separator"));
    }

    private static void executeCommand(String... command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        process.waitFor();
    }

}
