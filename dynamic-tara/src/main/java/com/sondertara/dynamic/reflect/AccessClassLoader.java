package com.sondertara.dynamic.reflect;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by wangzhiyuan on 2018/8/13
 */
public class AccessClassLoader extends ClassLoader {
    private static AccessClassLoader accessClassLoader = new AccessClassLoader();

    private AccessClassLoader() {
        super(Thread.currentThread().getContextClassLoader());
    }

    public static AccessClassLoader instance() {
        return accessClassLoader;
    }

    /**
     * 将.class文件输出到d:/
     *
     * @param className 类全限定名。如：com.cn.zsy.Main
     * @param bytes     字节码数组
     */
    public static void output2File(String className, byte[] bytes) {
        String fileName = className.substring(className.lastIndexOf(".") + 1);
        String dest = "d:/" + fileName + ".class";
        System.out.println("className = [" + className + "], dest : " + dest);

        try (FileChannel channel = new FileOutputStream(dest).getChannel()) {
            channel.write(ByteBuffer.wrap(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 定义类
     *
     * @param name
     * @param data
     * @return
     */
    public Class<?> defineClassForName(String name, byte[] data) {
        try {
            // 类已经定义过，直接加载
            return this.loadClass(name);
        } catch (ClassNotFoundException e) {
            // 类没有定义过，动态生成
            return this.defineClass(name, data, 0, data.length);
        }
    }


}
