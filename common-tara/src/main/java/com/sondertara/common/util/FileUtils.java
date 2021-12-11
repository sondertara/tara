package com.sondertara.common.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

/**
 * file util
 *
 * @author huangxiaohu
 * @version 1.0
 * @since 1.0
 * date 2019/11/8 1:00 下午
 **/
public class FileUtils {


    /**
     * 递归删除文件（夹）
     *
     * @param file 待删除的文件（夹）
     * @return 是否删除
     */

    public static boolean remove(File file) {
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            return file.delete();
        }
        Arrays.asList(file.listFiles()).forEach(FileUtils::remove);
        return file.delete();

    }

    /**
     * 递归删除文件（夹）路径
     *
     * @param path 待删除的文件（夹）路径
     * @return 是否删除
     */

    public static boolean remove(String path) {
        final File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            return file.delete();
        }
        Arrays.asList(file.listFiles()).forEach(FileUtils::remove);
        return file.delete();

    }

    /**
     * 递归向上删除文件（夹），包括最终父级目录
     *
     * @param file  待删除的文件（夹）
     * @param depth 父级向上的深度
     * @return 结果
     */

    public static boolean removeParent(File file, int depth) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getPath());
        }
        File parentFile = file.getParentFile();
        for (int i = 1; i < depth; i++) {
            parentFile = parentFile.getParentFile();
        }

        if (null == parentFile) {
            return false;
        }

        return remove(parentFile);
    }

    /**
     * 获取文件属性
     *
     * @param filePath 路径
     */
    public static BasicFileAttributes getAttributes(String filePath) throws IOException {

        BasicFileAttributes attributes = Files.readAttributes(new File(filePath).toPath(), BasicFileAttributes.class);
        return attributes;


    }

    /**
     * 获取文件创建时间
     *
     * @param filePath 路径
     */
    public static LocalDateTime getTimeCreate(String filePath) throws IOException {

        return LocalDateTime.ofInstant(getAttributes(filePath).creationTime().toInstant(), ZoneId.systemDefault());


    }

    /**
     * 获取文件最后修改时间
     *
     * @param filePath 路径
     */
    public static LocalDateTime getTimeLastModify(String filePath) throws IOException {

        return LocalDateTime.ofInstant(getAttributes(filePath).lastModifiedTime().toInstant(), ZoneId.systemDefault());


    }


}
