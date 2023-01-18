package com.sondertara.common.io;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

public class FileUtilsTest {

    @Test
    public void  listFiles(){
        List<String> list = FileUtils.listFileNames(System.getProperty("user.dir"));
        System.out.println(list.size());
    }

    @Test
    public void  ls(){
        File[] files = FileUtils.ls(System.getProperty("user.dir"));
        System.out.println(files.length);
    }
}
