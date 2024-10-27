/*
 * Copyright (c) 2023 looly(loolly@aliyun.com)
 * Hutool is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.sondertara.common.os;

import com.sondertara.common.io.file.Files;
import com.sondertara.common.logging.Loggers;
import com.sondertara.common.reflect.ReflectUtils;
import com.sondertara.common.text.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import static com.sondertara.common.os.SystemPropertys.getJavaIOTmpDir;

/**
 * JDK相关工具类，包括判断JDK版本等<br>
 * 工具部分方法来自fastjson2的JDKUtils
 *
 * @author fastjson, looly
 */
public class JdkUtils {


    /**
     * JDK版本
     */
    public static final int JVM_VERSION;
    /**
     * 是否JDK8<br>
     * 由于Hutool基于JDK8编译，当使用JDK版本低于8时，不支持。
     */
    public static final boolean IS_JDK8;
    /**
     * 是否大于等于JDK17
     */
    public static final boolean IS_AT_LEAST_JDK17;


    static {
        // JVM版本
        JVM_VERSION = _getJvmVersion();
        IS_JDK8 = 8 == JVM_VERSION;
        IS_AT_LEAST_JDK17 = JVM_VERSION >= 17;

        // JVM名称
        final String jvmName = _getJvmName();
    }

    /** JVM vendor info. */
    public static final String JVM_VENDOR = System.getProperty("java.vm.vendor");
//    public static final String JVM_VERSION = System.getProperty("java.vm.version");
    public static final String JVM_NAME = System.getProperty("java.vm.name");
    public static final String JVM_SPEC_VERSION = System.getProperty("java.specification.version");

    /** The value of <tt>System.getProperty("java.version")</tt>. **/
    public static final String JAVA_VERSION = System.getProperty("java.version");

    /** The value of <tt>System.getProperty("os.name")</tt>. **/
    public static final String OS_NAME = System.getProperty("os.name");
    /** True iff running on Linux. */
    public static final boolean LINUX = OS_NAME.startsWith("Linux");
    /** True iff running on Windows. */
    public static final boolean WINDOWS = OS_NAME.startsWith("Windows");
    /** True iff running on SunOS. */
    public static final boolean SUN_OS = OS_NAME.startsWith("SunOS");
    /** True iff running on Mac OS X */
    public static final boolean MAC_OS_X = OS_NAME.startsWith("Mac OS X");
    /** True iff running on FreeBSD */
    public static final boolean FREE_BSD = OS_NAME.startsWith("FreeBSD");

    public static final String OS_ARCH = System.getProperty("os.arch");
    public static final String OS_VERSION = System.getProperty("os.version");
    public static final String JAVA_VENDOR = System.getProperty("java.vendor");

    private static final int JVM_MAJOR_VERSION;
    private static final int JVM_MINOR_VERSION;

    /** True iff running on a 64bit JVM */
    public static final boolean JRE_IS_64BIT;

    static {
        final StringTokenizer st = new StringTokenizer(JVM_SPEC_VERSION, ".");
        JVM_MAJOR_VERSION = Integer.parseInt(st.nextToken());
        if (st.hasMoreTokens()) {
            JVM_MINOR_VERSION = Integer.parseInt(st.nextToken());
        } else {
            JVM_MINOR_VERSION = 0;
        }
        boolean is64Bit = false;
        String datamodel = null;
        try {
            datamodel = System.getProperty("sun.arch.data.model");
            if (datamodel != null) {
                is64Bit = datamodel.contains("64");
            }
        } catch (SecurityException ex) {}
        if (datamodel == null) {
            if (OS_ARCH != null && OS_ARCH.contains("64")) {
                is64Bit = true;
            } else {
                is64Bit = false;
            }
        }
        JRE_IS_64BIT = is64Bit;
    }

    public static final boolean JRE_IS_MINIMUM_JAVA8 = true;
    public static final boolean JRE_IS_MINIMUM_JAVA9 = JVM_MAJOR_VERSION > 1 || (JVM_MAJOR_VERSION == 1 && JVM_MINOR_VERSION >= 9);
    public static final boolean JRE_IS_MINIMUM_JAVA11 = JVM_MAJOR_VERSION >= 11;


    public static final int PID = getProcessId0();


    public static final boolean IS_WINDOWS = isWindows0();
    public static final int JAVA_VERSION_INT = javaVersion();
    public static final boolean IS_ANDROID = isAndroid0();

    public static final boolean IS_KAFFE_JVM = isKaffeJVM();
    private static final boolean IS_IKVM_DOT_NET = isIkvmDotNet0();
    public static final boolean IS_GROOVY_AVAILABLE = isGroovyAvailable0();
    public static final boolean IS_MAC_OS = OS.isMacOSX();
    /**
     * See https://github.com/oracle/graal/blob/master/sdk/src/org.graalvm.nativeimage/src/org/graalvm/nativeimage/ImageInfo.java
     */
    private static final boolean IMAGE_CODE = (System.getProperty("org.graalvm.nativeimage.imagecode") != null);


    /**
     * Return whether this runtime environment lives within a native image.
     */
    public static boolean inImageCode() {
        return IMAGE_CODE;
    }

    /**
     * @return the jvm process ID, -1 if it cannot be figured out.
     */
    public static int getPid() {
        return PID;
    }

    private static boolean isOSX() {
        return StringUtils.startsWithIgnoreCase(OS_NAME,"macosx") || StringUtils.startsWithIgnoreCase(OS_NAME,"osx");
    }

    private static boolean isWindows0() {
        return System.getProperty("os.name", "").toLowerCase(Locale.US).contains("win");
    }

    private static boolean isIkvmDotNet0() {
        String vmName = System.getProperty("java.vm.name", "").toUpperCase(Locale.US);
        return "IKVM.NET".equals(vmName);
    }

    private static boolean isAndroid0() {
        // Idea: Sometimes java binaries include Android classes on the classpath, even if it isn't actually Android.
        // Rather than check if certain classes are present, just check the VM, which is tied to the JDK.

        // Optional improvement: check if `android.os.Build.VERSION` is >= 24. On later versions of Android, the
        // OpenJDK is used, which means `Unsafe` will actually work as expected.

        // Android sets this property to Dalvik, regardless of whether it actually is.
        String vmName = System.getProperty("java.vm.name");
        boolean isAndroid = "Dalvik".equals(vmName);
        if (!isAndroid) {
            String runtime = System.getProperty("java.runtime.name");
            isAndroid = StringUtils.getEmptyIfNull(runtime).toLowerCase().contains("android");
        }
        if (isAndroid) {
            Logger logger = Loggers.getLogger(JdkUtils.class);
            logger.debug("Platform: Android");
        }
        return isAndroid;
    }

    private static boolean isKaffeJVM() {
        try {
            Class.forName("kaffe.util.NotImplemented");
            return true;
        } catch (Exception t) {
            // swallow as this simply doesn't seem to be Kaffe
        }
        return false;
    }

    private static int javaVersion() {
        final int majorVersion;

        if (isAndroid0()) {
            majorVersion = 6;
        } else {
            majorVersion = majorVersionFromJavaSpecificationVersion();
        }
        Logger logger = Loggers.getLogger(JdkUtils.class);
        logger.debug("Java version: {}", majorVersion);

        return majorVersion;
    }

    // Package-private for testing only
    private static int majorVersionFromJavaSpecificationVersion() {
        // http://www.oracle.com/technetwork/java/javase/versioning-naming-139433.html
        // http://openjdk.java.net/jeps/223 "New Version-String Scheme"

        String vm = System.getProperty("java.version"); // JLS 20.18.7
        if (vm == null) {
            vm = System.getProperty("java.runtime.version");
        }
        if (vm == null) {
            vm = System.getProperty("java.specification.version", "1.6");
        }
        return majorVersion(vm);
    }

    // Package-private for testing only
    private static int majorVersion(String javaVersion) {
        int index = StringUtils.indexOf(javaVersion, "_", 0);
        if (index != -1) {
            javaVersion = javaVersion.substring(0, index);
        }
        final String[] components = javaVersion.split("\\.");
        final int[] version = new int[components.length];
        for (int i = 0; i < components.length; i++) {
            try {
                version[i] = Integer.parseInt(components[i]);
            } catch (Exception ignored) {
                // ignore it
            }
        }

        if (version[0] == 1) {
            return version[1];
        } else {
            return version[0];
        }
    }


    public static boolean is3VMOrGreater() {
        return JAVA_VERSION_INT >= 3;
    }

    public static boolean is4VMOrGreater() {
        return JAVA_VERSION_INT >= 4;
    }

    public static boolean is5VMOrGreater() {
        return JAVA_VERSION_INT >= 5;
    }

    public static boolean is6VMOrGreater() {
        return JAVA_VERSION_INT >= 6;
    }

    public static boolean is7VMOrGreater() {
        return JAVA_VERSION_INT >= 7;
    }

    public static boolean is8VMOrGreater() {
        return JAVA_VERSION_INT >= 8;
    }

    public static boolean is9VMOrGreater() {
        return JAVA_VERSION_INT >= 9;
    }



    public static boolean is11VMOrGreater() {
        return JAVA_VERSION_INT >= 11;
    }



    public static boolean is17VMOrGreater() {
        return JAVA_VERSION_INT >= 17;
    }

    private static final Map<Integer, Integer> classMajorVersionToJdkVersion = new LinkedHashMap<Integer, Integer>();

    static {
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_1_1, 1);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_1_2, 2);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_1_3, 3);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_1_4, 4);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_1_5, 5);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_1_6, 6);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_1_7, 7);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_1_8, 8);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_1_9, 9);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_10, 10);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_11, 11);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_12, 12);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_13, 14);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_14, 14);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_15, 15);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_16, 16);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_17, 17);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_18, 18);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_19, 19);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_20, 20);
        classMajorVersionToJdkVersion.put((int) JvmConstants.MAJOR_21, 21);
    }


    /**
     * @param classMajorVersion jvm version
     * @return version
     * @see JvmConstants
     */
    public static int getJavaVersion(int classMajorVersion) {
        return classMajorVersionToJdkVersion.get(classMajorVersion);
    }

    /**
     * Find java executable File path from java.home system property.
     *
     * @return File associated with the java command, or null if not found.
     */
    public static File getJavaExecutable() {
        String javaHome = null;
        File result = null;
        // java.home
        // java.class.path
        // java.ext.dirs
        try {
            javaHome = System.getProperty("java.home");
        } catch (Exception t) {
            // ignore
        }
        if (null != javaHome) {
            try {
                File binDir = Files.newFile(javaHome, "bin");
                if (binDir.isDirectory() && binDir.canRead()) {
                    String[] execs = new String[]{"java", "java.exe"};
                    for (String exec : execs) {
                        result = new File(binDir, exec);
                        if (result.canRead()) {
                            break;
                        }
                    }
                }
            } catch (Throwable e) {
                // ignore ite
            }
        }
        return result;
    }

    private static boolean isGroovyAvailable0() {
        ClassLoader loader = JdkUtils.class.getClassLoader();
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        try {
            Class bindingClass = loader.loadClass("groovy.lang.Binding");
            return bindingClass != null;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    private static int getProcessId0() {
        try {
            if (IS_ANDROID) {
                Object runtimeMXBean = ReflectUtils.getDeclaredMethod(Class.forName("java.lang.management.ManagementFactory"), "getRuntimeMXBean").invoke(null);
                return Integer.parseInt(ReflectUtils.getDeclaredMethod(Class.forName("java.lang.management.RuntimeMXBean"), "getName").invoke(runtimeMXBean).toString().split("@")[0]);
            } else {
                java.lang.management.RuntimeMXBean runtimeMXBean = java.lang.management.ManagementFactory.getRuntimeMXBean();
                return Integer.parseInt(runtimeMXBean.getName().split("@")[0]);
            }
        } catch (Exception ex) {
            if (IS_ANDROID) {
                try {
                    return Integer.parseInt(new File("/proc/self").getCanonicalFile().getName());
                } catch (IOException e) {
                    return 0;
                }
            }
            return 0;
        }
    }


    /**
     * Returns the path to the system temporary directory.
     *
     * @return the path to the system temporary directory.
     */
    public static String getTempDirectoryPath() {
        return getJavaIOTmpDir();
    }

    /**
     * Returns a {@link File} representing the system temporary directory.
     *
     * @return the system temporary directory.
     */
    public static File getTempDirectory() {
        return Files.newFile(getTempDirectoryPath());
    }

    /**
     * Returns the path to the user's home directory.
     *
     * @return the path to the user's home directory.
     */
    public static String getUserHomeDirectoryPath() {
        return SystemPropertys.getUserHome();
    }

    /**
     * Returns a {@link File} representing the user's home directory.
     *
     * @return the user's home directory.
     */
    public static File getUserHomeDirectory() {
        return Files.newFile(getUserHomeDirectoryPath());
    }

    public static int cpuCore() {
        return CpuCoreSensor.availableProcessors();
    }

    /**
     * 获取JVM名称
     *
     * @return JVM名称
     */
    private static String _getJvmName() {
        return System.getProperty("java.vm.name");
    }

    /**
     * 根据{@code java.specification.version}属性值，获取版本号
     *
     * @return 版本号
     */
    private static int _getJvmVersion() {
        int jvmVersion = -1;

        try {
            String javaSpecVer = System.getProperty("java.specification.version");
            if (StringUtils.isNotBlank(javaSpecVer)) {
                if (javaSpecVer.startsWith("1.")) {
                    javaSpecVer = javaSpecVer.substring(2);
                }
                if (javaSpecVer.indexOf('.') == -1) {
                    jvmVersion = Integer.parseInt(javaSpecVer);
                }
            }
        } catch (Throwable ignore) {
            // 默认JDK8
            jvmVersion = 8;
        }

        return jvmVersion;
    }

}
