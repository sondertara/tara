package com.sondertara.common.os;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * TODO
 *
 * @author huangxiaohu.1ih
 * @date 2024/7/24 14:48
 */
class JdkUtilsTest {

    @Test
    void testInImageCode() {
        boolean result = JdkUtils.inImageCode();
        Assertions.assertEquals(false, result);
    }

    @Test
    void testIs3VMOrGreater() {
        boolean result = JdkUtils.is3VMOrGreater();
        Assertions.assertEquals(true, result);
    }

    @Test
    void testIs4VMOrGreater() {
        boolean result = JdkUtils.is4VMOrGreater();
        Assertions.assertEquals(true, result);
    }

    @Test
    void testIs5VMOrGreater() {
        boolean result = JdkUtils.is5VMOrGreater();
        Assertions.assertEquals(true, result);
    }

    @Test
    void testIs6VMOrGreater() {
        boolean result = JdkUtils.is6VMOrGreater();
        Assertions.assertEquals(true, result);
    }

    @Test
    void testIs7VMOrGreater() {
        boolean result = JdkUtils.is7VMOrGreater();
        Assertions.assertEquals(true, result);
    }

    @Test
    void testIs8VMOrGreater() {
        boolean result = JdkUtils.is8VMOrGreater();
        Assertions.assertEquals(true, result);
    }

    @Test
    void testIs9VMOrGreater() {
        boolean result = JdkUtils.is9VMOrGreater();
        Assertions.assertEquals(false, result);
    }


    @Test
    void testIs11VMOrGreater() {
        boolean result = JdkUtils.is11VMOrGreater();
        Assertions.assertEquals(false, result);
    }

    @Test
    void testIs17VMOrGreater() {
        boolean result = JdkUtils.is17VMOrGreater();
        Assertions.assertEquals(false, result);
    }


    @Test
    void testGetJavaVersion() {
        int result = JdkUtils.getJavaVersion(JvmConstants.MAJOR_19);
        Assertions.assertEquals(19, result);
    }

    @Test
    void testGetJavaExecutable() {
        File result = JdkUtils.getJavaExecutable();
        Assertions.assertEquals(new File("C:\\Program Files\\Java\\jdk1.8.0_152\\jre\\bin\\java.exe"), result);
    }

    @Test
    void testGetTempDirectoryPath() {
        String result = JdkUtils.getTempDirectoryPath();
    }

    @Test
    void testGetTempDirectory() {
        File result = JdkUtils.getTempDirectory();
        Assertions.assertEquals(new File("C:\\Users\\HUANGX~1.1IH\\AppData\\Local\\Temp"), result);
    }

    @Test
    void testGetUserHomeDirectoryPath() {
        String result = JdkUtils.getUserHomeDirectoryPath();
    }

    @Test
    void testGetUserHomeDirectory() {
        File result = JdkUtils.getUserHomeDirectory();
        Assertions.assertEquals(new File("C:\\Users\\huangxiaohu.1ih"), result);
    }

    @Test
    void testCpuCore() {
        int result = JdkUtils.cpuCore();
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme