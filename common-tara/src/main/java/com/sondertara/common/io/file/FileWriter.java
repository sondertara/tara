package com.sondertara.common.io.file;


import com.sondertara.common.exception.IORuntimeException;
import com.sondertara.common.io.FileUtils;
import com.sondertara.common.io.IoUtils;
import com.sondertara.common.lang.Assert;
import com.sondertara.common.util.StringUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 文件写入器
 *
 * @author Looly
 */
public class FileWriter extends FileWrapper {
    private static final long serialVersionUID = 1L;

    /**
     * 创建 FileWriter
     *
     * @param file 文件
     * @return FileWriter
     */
    public static FileWriter create(File file, Charset charset) {
        return new FileWriter(file, charset);
    }

    /**
     * 创建 FileWriter, 编码：{@link FileWrapper#DEFAULT_CHARSET}
     *
     * @param file 文件
     * @return FileWriter
     */
    public static FileWriter create(File file) {
        return new FileWriter(file);
    }

    // ------------------------------------------------------- Constructor start

    /**
     * 构造
     *
     * @param file 文件
     */
    public FileWriter(File file, Charset charset) {
        super(file, charset);
        checkFile();
    }

    /**
     * 构造
     *
     * @param file 文件
     */
    public FileWriter(File file, String charset) {
        this(file, Charset.forName(charset));
    }

    /**
     * 构造
     *
     * @param filePath 文件路径，相对路径会被转换为相对于ClassPath的路径
     */
    public FileWriter(String filePath, Charset charset) {
        this(FileUtils.file(filePath), charset);
    }

    /**
     * 构造
     *
     * @param filePath 文件路径，相对路径会被转换为相对于ClassPath的路径
     */
    public FileWriter(String filePath, String charset) {
        this(FileUtils.file(filePath), Charset.forName(charset));
    }

    /**
     * 构造<br>
     * 编码使用 {@link FileWrapper#DEFAULT_CHARSET}
     *
     * @param file 文件
     */
    public FileWriter(File file) {
        this(file, DEFAULT_CHARSET);
    }

    /**
     * 构造<br>
     * 编码使用 {@link FileWrapper#DEFAULT_CHARSET}
     *
     * @param filePath 文件路径，相对路径会被转换为相对于ClassPath的路径
     */
    public FileWriter(String filePath) {
        this(filePath, DEFAULT_CHARSET);
    }
    // ------------------------------------------------------- Constructor end

    /**
     * 将String写入文件
     *
     * @param content  写入的内容
     * @param isAppend 是否追加
     * @return 目标文件
     * @throws IORuntimeException IO异常
     */
    public File write(String content, boolean isAppend) throws IORuntimeException {
        BufferedWriter writer = null;
        try {
            writer = getWriter(isAppend);
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            IoUtils.close(writer);
        }
        return file;
    }

    /**
     * 将String写入文件，覆盖模式
     *
     * @param content 写入的内容
     * @return 目标文件
     * @throws IORuntimeException IO异常
     */
    public File write(String content) throws IORuntimeException {
        return write(content, false);
    }

    /**
     * 将String写入文件，追加模式
     *
     * @param content 写入的内容
     * @return 写入的文件
     * @throws IORuntimeException IO异常
     */
    public File append(String content) throws IORuntimeException {
        return write(content, true);
    }

    /**
     * 将列表写入文件，覆盖模式
     *
     * @param <T>  集合元素类型
     * @param list 列表
     * @return 目标文件
     * @throws IORuntimeException IO异常
     */
    public <T> File writeLines(Iterable<T> list) throws IORuntimeException {
        return writeLines(list, false);
    }

    /**
     * 将列表写入文件，追加模式
     *
     * @param <T>  集合元素类型
     * @param list 列表
     * @return 目标文件
     * @throws IORuntimeException IO异常
     */
    public <T> File appendLines(Iterable<T> list) throws IORuntimeException {
        return writeLines(list, true);
    }

    /**
     * 将列表写入文件
     *
     * @param <T>      集合元素类型
     * @param list     列表
     * @param isAppend 是否追加
     * @return 目标文件
     * @throws IORuntimeException IO异常
     * @since 3.1.0
     */
    public <T> File writeLines(Iterable<T> list, boolean isAppend) throws IORuntimeException {
        try (PrintWriter writer = getPrintWriter(isAppend)) {
            boolean isFirst = true;
            for (T t : list) {
                if (null != t) {
                    if (isFirst) {
                        isFirst = false;
                        if (isAppend && FileUtils.isNotEmpty(this.file)) {
                            // 追加模式下且文件非空，补充换行符
                            printNewLine(writer);
                        }
                    } else {
                        printNewLine(writer);
                    }
                    writer.print(t);

                    writer.flush();
                }
            }
        }
        return this.file;
    }


    /**
     * 将Map写入文件，每个键值对为一行，一行中键与值之间使用kvSeparator分隔
     *
     * @param map         Map
     * @param kvSeparator 键和值之间的分隔符，如果传入null使用默认分隔符" = "
     * @param isAppend    是否追加
     * @return 目标文件
     * @throws IORuntimeException IO异常
     * @since 4.0.5
     */
    public File writeMap(Map<?, ?> map, String kvSeparator, boolean isAppend) throws IORuntimeException {
        if (null == kvSeparator) {
            kvSeparator = " = ";
        }
        try (PrintWriter writer = getPrintWriter(isAppend)) {
            for (Entry<?, ?> entry : map.entrySet()) {
                if (null != entry) {
                    writer.print(StringUtils.format("{}{}{}", entry.getKey(), kvSeparator, entry.getValue()));
                    printNewLine(writer);
                    writer.flush();
                }
            }
        }
        return this.file;
    }

    /**
     * 写入数据到文件
     *
     * @param data 数据
     * @param off  数据开始位置
     * @param len  数据长度
     * @return 目标文件
     * @throws IORuntimeException IO异常
     */
    public File write(byte[] data, int off, int len) throws IORuntimeException {
        return write(data, off, len, false);
    }

    /**
     * 追加数据到文件
     *
     * @param data 数据
     * @param off  数据开始位置
     * @param len  数据长度
     * @return 目标文件
     * @throws IORuntimeException IO异常
     */
    public File append(byte[] data, int off, int len) throws IORuntimeException {
        return write(data, off, len, true);
    }

    /**
     * 写入数据到文件
     *
     * @param data     数据
     * @param off      数据开始位置
     * @param len      数据长度
     * @param isAppend 是否追加模式
     * @return 目标文件
     * @throws IORuntimeException IO异常
     */
    public File write(byte[] data, int off, int len, boolean isAppend) throws IORuntimeException {
        try (FileOutputStream out = new FileOutputStream(FileUtils.touch(file), isAppend)) {
            out.write(data, off, len);
            out.flush();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return file;
    }

    /**
     * 将流的内容写入文件<br>
     * 此方法会自动关闭输入流
     *
     * @param in 输入流，不关闭
     * @return dest
     * @throws IORuntimeException IO异常
     */
    public File writeFromStream(InputStream in) throws IORuntimeException {
        return writeFromStream(in, true);
    }

    /**
     * 将流的内容写入文件
     *
     * @param in        输入流，不关闭
     * @param isCloseIn 是否关闭输入流
     * @return dest
     * @throws IORuntimeException IO异常
     * @since 5.5.2
     */
    public File writeFromStream(InputStream in, boolean isCloseIn) throws IORuntimeException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(FileUtils.touch(file));
            IoUtils.copy(in, out);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            IoUtils.close(out);
            if (isCloseIn) {
                IoUtils.close(in);
            }
        }
        return file;
    }

    /**
     * 获得一个输出流对象
     *
     * @return 输出流对象
     * @throws IORuntimeException IO异常
     */
    public BufferedOutputStream getOutputStream() throws IORuntimeException {
        try {
            return new BufferedOutputStream(new FileOutputStream(FileUtils.touch(file)));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 获得一个带缓存的写入对象
     *
     * @param isAppend 是否追加
     * @return BufferedReader对象
     * @throws IORuntimeException IO异常
     */
    public BufferedWriter getWriter(boolean isAppend) throws IORuntimeException {
        try {
            return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileUtils.touch(file), isAppend), charset));
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 获得一个打印写入对象，可以有print
     *
     * @param isAppend 是否追加
     * @return 打印对象
     * @throws IORuntimeException IO异常
     */
    public PrintWriter getPrintWriter(boolean isAppend) throws IORuntimeException {
        return new PrintWriter(getWriter(isAppend));
    }

    /**
     * 检查文件
     *
     * @throws IORuntimeException IO异常
     */
    private void checkFile() throws IORuntimeException {
        Assert.notNull(file, "File to write content is null !");
        if (this.file.exists() && false == file.isFile()) {
            throw new IORuntimeException("File [{}] is not a file !", this.file.getAbsoluteFile());
        }
    }

    /**
     * 打印新行
     *
     * @param writer Writer
     * @since 4.0.5
     */
    private void printNewLine(PrintWriter writer) {
        writer.println();
    }
}
