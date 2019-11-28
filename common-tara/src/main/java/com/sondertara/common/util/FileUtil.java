package com.sondertara.common.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * file util
 *
 * @author huangxiaohu
 * @version 1.0
 * @since 1.0
 * date 2019/11/8 1:00 下午
 **/
public class FileUtil {


    /**
     * 递归删除文件（夹）
     *
     * @param file 待删除的文件（夹）
     * @return
     */

    static boolean remove(File file) {
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            return file.delete();
        }
        Arrays.asList(file.listFiles()).forEach(FileUtil::remove);
        return file.delete();

    }

    /**
     * 递归向上删除文件（夹），包括最终父级目录
     *
     * @param file  待删除的文件（夹）
     * @param depth 父级向上的深度
     * @return 结果
     */

    static boolean removeParent(File file, int depth) throws FileNotFoundException {
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

    public static void main(String[] args) {
        final int anInt = Integer.valueOf(null);
        System.out.println(anInt);
    }

}
