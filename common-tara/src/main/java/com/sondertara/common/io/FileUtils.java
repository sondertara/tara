package com.sondertara.common.io;

import com.sondertara.common.exception.TaraException;
import com.sondertara.common.io.file.visitor.CopyVisitor;
import com.sondertara.common.io.file.visitor.DelVisitor;
import com.sondertara.common.io.file.visitor.MoveVisitor;
import com.sondertara.common.lang.Assert;
import com.sondertara.common.lang.map.EnumerationIter;
import com.sondertara.common.util.ArrayUtils;
import com.sondertara.common.util.CharUtils;
import com.sondertara.common.util.ClassUtils;
import com.sondertara.common.util.RegexUtils;
import com.sondertara.common.util.ResourceUtils;
import com.sondertara.common.util.StringUtils;
import com.sondertara.common.util.URLUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
     * Class文件扩展名
     */
    public static final String CLASS_EXT = ".class";
    /**
     * Jar文件扩展名
     */
    public static final String JAR_FILE_EXT = ".jar";
    /**
     * 在Jar中的路径jar的扩展名形式
     */
    public static final String JAR_PATH_EXT = ".jar!";
    /**
     * 绝对路径判断正则
     */
    private static final Pattern PATTERN_PATH_ABSOLUTE = Pattern.compile("^[a-zA-Z]:([/\\\\].*)?");

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
        Arrays.asList(Objects.requireNonNull(file.listFiles())).forEach(FileUtils::remove);
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
        Arrays.asList(Objects.requireNonNull(file.listFiles())).forEach(FileUtils::remove);
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

        return Files.readAttributes(new File(filePath).toPath(), BasicFileAttributes.class);

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

    /**
     * 是否为Windows环境
     *
     * @return 是否为Windows环境
     * @since 3.0.9
     */
    public static boolean isWindows() {
        return FileNameUtils.WINDOWS_SEPARATOR == File.separatorChar;
    }

    /**
     * 列出指定路径下的目录和文件<br>
     * 给定的绝对路径不能是压缩包中的路径
     *
     * @param path 目录绝对路径或者相对路径
     * @return 文件列表（包含目录）
     */
    public static File[] ls(String path) {
        if (path == null) {
            return null;
        }

        File file = file(path);
        if (file.isDirectory()) {
            return file.listFiles();
        }
        throw new TaraException(StringUtils.format("Path [{}] is not directory!", path));
    }

    /**
     * 文件是否为空<br>
     * 目录：里面没有文件时为空 文件：文件大小为0时为空
     *
     * @param file 文件
     * @return 是否为空，当提供非目录时，返回false
     */
    public static boolean isEmpty(File file) {
        if (null == file || !file.exists()) {
            return true;
        }

        if (file.isDirectory()) {
            String[] subFiles = file.list();
            return ArrayUtils.isEmpty(subFiles);
        } else if (file.isFile()) {
            return file.length() <= 0;
        }

        return false;
    }

    /**
     * 目录是否为空
     *
     * @param file 目录
     * @return 是否为空，当提供非目录时，返回false
     */
    public static boolean isNotEmpty(File file) {
        return !isEmpty(file);
    }

    /**
     * 目录是否为空
     *
     * @param dir 目录
     * @return 是否为空
     */
    public static boolean isDirEmpty(File dir) {
        return isDirEmpty(dir.toPath());
    }

    /**
     * 递归遍历目录以及子目录中的所有文件<br>
     * 如果提供file为文件，直接返回过滤结果
     *
     * @param path       当前遍历文件或目录的路径
     * @param fileFilter 文件过滤规则对象，选择要保留的文件，只对文件有效，不过滤目录
     * @return 文件列表
     * @since 3.2.0
     */
    public static List<File> loopFiles(String path, FileFilter fileFilter) {
        return loopFiles(file(path), fileFilter);
    }

    /**
     * 递归遍历目录以及子目录中的所有文件<br>
     * 如果提供file为文件，直接返回过滤结果
     *
     * @param file       当前遍历文件或目录
     * @param fileFilter 文件过滤规则对象，选择要保留的文件，只对文件有效，不过滤目录
     * @return 文件列表
     */
    public static List<File> loopFiles(File file, FileFilter fileFilter) {
        return loopFiles(file, -1, fileFilter);
    }

    /**
     * 递归遍历目录并处理目录下的文件，可以处理目录或文件：
     * <ul>
     * <li>非目录则直接调用{@link Consumer}处理</li>
     * <li>目录则递归调用此方法处理</li>
     * </ul>
     *
     * @param file     文件或目录，文件直接处理
     * @param consumer 文件处理器，只会处理文件
     * @since 5.5.2
     */
    public static void walkFiles(File file, Consumer<File> consumer) {
        if (file.isDirectory()) {
            final File[] subFiles = file.listFiles();
            if (ArrayUtils.isNotEmpty(subFiles)) {
                for (File tmp : subFiles) {
                    walkFiles(tmp, consumer);
                }
            }
        } else {
            consumer.accept(file);
        }
    }

    /**
     * 递归遍历目录以及子目录中的所有文件<br>
     * 如果提供file为文件，直接返回过滤结果
     *
     * @param file       当前遍历文件或目录
     * @param maxDepth   遍历最大深度，-1表示遍历到没有目录为止
     * @param fileFilter 文件过滤规则对象，选择要保留的文件，只对文件有效，不过滤目录，null表示接收全部文件
     * @return 文件列表
     * @since 4.6.3
     */
    public static List<File> loopFiles(File file, int maxDepth, FileFilter fileFilter) {
        return loopFiles(file.toPath(), maxDepth, fileFilter);
    }

    /**
     * 递归遍历目录以及子目录中的所有文件<br>
     * 如果用户传入相对路径，则是相对classpath的路径<br>
     * 如："test/aaa"表示"${classpath}/test/aaa"
     *
     * @param path 相对ClassPath的目录或者绝对路径目录
     * @return 文件列表
     * @since 3.2.0
     */
    public static List<File> loopFiles(String path) {
        return loopFiles(file(path));
    }

    /**
     * 递归遍历目录以及子目录中的所有文件
     *
     * @param file 当前遍历文件
     * @return 文件列表
     */
    public static List<File> loopFiles(File file) {
        return loopFiles(file, null);
    }

    /**
     * 获得指定目录下所有文件<br>
     * 不会扫描子目录<br>
     * 如果用户传入相对路径，则是相对classpath的路径<br>
     * 如："test/aaa"表示"${classpath}/test/aaa"
     *
     * @param path 相对ClassPath的目录或者绝对路径目录
     * @return 文件路径列表（如果是jar中的文件，则给定类似.jar!/xxx/xxx的路径）
     * @throws TaraException IO异常
     */
    public static List<String> listFileNames(String path) throws TaraException {
        if (path == null) {
            return new ArrayList<>(0);
        }
        int index = path.lastIndexOf(JAR_PATH_EXT);
        if (index < 0) {
            // 普通目录
            final List<String> paths = new ArrayList<>();
            final File[] files = ls(path);
            for (File file : files) {
                if (file.isFile()) {
                    paths.add(file.getName());
                }
            }
            return paths;
        }

        // jar文件
        path = getAbsolutePath(path);
        // jar文件中的路径
        index = index + FileUtils.JAR_FILE_EXT.length();
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(path.substring(0, index));
            // 防止出现jar!/com/google/这类路径导致文件找不到
            return listFileNames(jarFile, StringUtils.removePrefix(path.substring(index + 1), "/"));
        } catch (IOException e) {
            throw new TaraException(StringUtils.format("Can not read file path of [{}]", path), e);
        } finally {
            try {
                IOUtils.close(jarFile);
            } catch (IOException ignored) {

            }
        }
    }

    public static List<String> listFileNames(ZipFile zipFile, String dir) {
        if (StringUtils.isNotBlank(dir)) {
            // 目录尾部添加"/"
            dir = StringUtils.addSuffixIfNot(dir, StringUtils.SLASH);
        }

        final List<String> fileNames = new ArrayList<>();
        String name;
        for (ZipEntry entry : new EnumerationIter<>(zipFile.entries())) {
            name = entry.getName();
            if (StringUtils.isEmpty(dir) || name.startsWith(dir)) {
                final String nameSuffix = StringUtils.removePrefix(name, dir);
                if (StringUtils.isNotEmpty(nameSuffix) && !StringUtils.contains(nameSuffix, CharUtils.SLASH)) {
                    fileNames.add(nameSuffix);
                }
            }
        }

        return fileNames;
    }

    /**
     * 创建File对象，自动识别相对或绝对路径，相对路径将自动从ClassPath下寻找
     *
     * @param path 相对ClassPath的目录或者绝对路径目录
     * @return File
     */
    public static File file(String path) {
        if (null == path) {
            return null;
        }
        return new File(getAbsolutePath(path));
    }

    /**
     * 创建File对象<br>
     * 此方法会检查slip漏洞，漏洞说明见http://blog.nsfocus.net/zip-slip-2/
     *
     * @param parent 父目录
     * @param path   文件路径
     * @return File
     */
    public static File file(String parent, String path) {
        return file(new File(parent), path);
    }

    /**
     * 创建File对象<br>
     * 根据的路径构建文件，在Win下直接构建，在Linux下拆分路径单独构建
     * 此方法会检查slip漏洞，漏洞说明见http://blog.nsfocus.net/zip-slip-2/
     *
     * @param parent 父文件对象
     * @param path   文件路径
     * @return File
     */
    public static File file(File parent, String path) {
        if (StringUtils.isBlank(path)) {
            throw new NullPointerException("File path is blank!");
        }
        return checkSlip(parent, buildFile(parent, path));
    }

    /**
     * 通过多层目录参数创建文件<br>
     * 此方法会检查slip漏洞，漏洞说明见http://blog.nsfocus.net/zip-slip-2/
     *
     * @param directory 父目录
     * @param names     元素名（多层目录名），由外到内依次传入
     * @return the file 文件
     * @since 4.0.6
     */
    public static File file(File directory, String... names) {
        Assert.notNull(directory, "directory must not be null");
        if (ArrayUtils.isEmpty(names)) {
            return directory;
        }

        File file = directory;
        for (String name : names) {
            if (null != name) {
                file = file(file, name);
            }
        }
        return file;
    }

    /**
     * 通过多层目录创建文件
     * <p>
     * 元素名（多层目录名）
     *
     * @param names 多层文件的文件名，由外到内依次传入
     * @return the file 文件
     * @since 4.0.6
     */
    public static File file(String... names) {
        if (ArrayUtils.isEmpty(names)) {
            return null;
        }

        File file = null;
        for (String name : names) {
            if (file == null) {
                file = file(name);
            } else {
                file = file(file, name);
            }
        }
        return file;
    }

    /**
     * 创建File对象
     *
     * @param uri 文件URI
     * @return File
     */
    public static File file(URI uri) {
        if (uri == null) {
            throw new NullPointerException("File uri is null!");
        }
        return new File(uri);
    }

    /**
     * 创建File对象
     *
     * @param url 文件URL
     * @return File
     */
    public static File file(URL url) {
        return new File(URLUtils.toURI(url));
    }

    /**
     * 获取临时文件路径（绝对路径）
     *
     * @return 临时文件路径
     * @since 4.0.6
     */
    public static String getTmpDirPath() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * 获取临时文件目录
     *
     * @return 临时文件目录
     * @since 4.0.6
     */
    public static File getTmpDir() {
        return file(getTmpDirPath());
    }

    /**
     * 获取用户路径（绝对路径）
     *
     * @return 用户路径
     * @since 4.0.6
     */
    public static String getUserHomePath() {
        return System.getProperty("user.home");
    }

    /**
     * 获取用户目录
     *
     * @return 用户目录
     * @since 4.0.6
     */
    public static File getUserHomeDir() {
        return file(getUserHomePath());
    }

    /**
     * 判断文件是否存在，如果path为null，则返回false
     *
     * @param path 文件路径
     * @return 如果存在返回true
     */
    public static boolean exist(String path) {
        return (null != path) && file(path).exists();
    }

    /**
     * 判断文件是否存在，如果file为null，则返回false
     *
     * @param file 文件
     * @return 如果存在返回true
     */
    public static boolean exist(File file) {
        return (null != file) && file.exists();
    }

    /**
     * 是否存在匹配文件
     *
     * @param directory 文件夹路径
     * @param regexp    文件夹中所包含文件名的正则表达式
     * @return 如果存在匹配文件返回true
     */
    public static boolean exist(String directory, String regexp) {
        final File file = new File(directory);
        if (!file.exists()) {
            return false;
        }

        final String[] fileList = file.list();
        if (fileList == null) {
            return false;
        }

        for (String fileName : fileList) {
            if (fileName.matches(regexp)) {
                return true;
            }

        }
        return false;
    }

    /**
     * 指定文件最后修改时间
     *
     * @param file 文件
     * @return 最后修改时间
     */
    public static Date lastModifiedTime(File file) {
        if (!exist(file)) {
            return null;
        }

        return new Date(file.lastModified());
    }

    /**
     * 指定路径文件最后修改时间
     *
     * @param path 绝对路径
     * @return 最后修改时间
     */
    public static Date lastModifiedTime(String path) {
        return lastModifiedTime(new File(path));
    }

    /**
     * 计算目录或文件的总大小<br>
     * 当给定对象为文件时，直接调用 {@link File#length()}<br>
     * 当给定对象为目录时，遍历目录下的所有文件和目录，递归计算其大小，求和返回<br>
     * 此方法不包括目录本身的占用空间大小。
     *
     * @param file 目录或文件,null或者文件不存在返回0
     * @return 总大小，bytes长度
     */
    public static long size(File file) {
        return size(file, false);
    }

    /**
     * 计算目录或文件的总大小<br>
     * 当给定对象为文件时，直接调用 {@link File#length()}<br>
     * 当给定对象为目录时，遍历目录下的所有文件和目录，递归计算其大小，求和返回
     *
     * @param file           目录或文件,null或者文件不存在返回0
     * @param includeDirSize 是否包括每层目录本身的大小
     * @return 总大小，bytes长度
     * @since 5.7.21
     */
    public static long size(File file, boolean includeDirSize) {
        if (null == file || !file.exists() || isSymlink(file)) {
            return 0;
        }

        if (file.isDirectory()) {
            long size = includeDirSize ? file.length() : 0;
            File[] subFiles = file.listFiles();
            if (ArrayUtils.isEmpty(subFiles)) {
                return 0L;
            }
            for (File subFile : subFiles) {
                size += size(subFile, includeDirSize);
            }
            return size;
        } else {
            return file.length();
        }
    }

    /**
     * 计算文件的总行数<br>
     * 读取文件采用系统默认编码，一般乱码不会造成行数错误。
     *
     * @param file 文件
     * @return 该文件总行数
     * @since 5.7.22
     */
    public static int getTotalLines(File file) {
        if (!isFile(file)) {
            throw new TaraException("Input must be a File");
        }
        try (final LineNumberReader lineNumberReader = new LineNumberReader(new java.io.FileReader(file))) {
            // 设置起始为1
            lineNumberReader.setLineNumber(1);
            // 跳过文件中内容
            // noinspection ResultOfMethodCallIgnored
            lineNumberReader.skip(Long.MAX_VALUE);
            // 获取当前行号
            return lineNumberReader.getLineNumber();
        } catch (IOException e) {
            throw new TaraException(e);
        }
    }

    /**
     * 给定文件或目录的最后修改时间是否晚于给定时间
     *
     * @param file      文件或目录
     * @param reference 参照文件
     * @return 是否晚于给定时间
     */
    public static boolean newerThan(File file, File reference) {
        if (null == reference || !reference.exists()) {
            // 文件一定比一个不存在的文件新
            return true;
        }
        return newerThan(file, reference.lastModified());
    }

    /**
     * 给定文件或目录的最后修改时间是否晚于给定时间
     *
     * @param file       文件或目录
     * @param timeMillis 做为对比的时间
     * @return 是否晚于给定时间
     */
    public static boolean newerThan(File file, long timeMillis) {
        if (null == file || !file.exists()) {
            // 不存在的文件一定比任何时间旧
            return false;
        }
        return file.lastModified() > timeMillis;
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param path 相对ClassPath的目录或者绝对路径目录，使用POSIX风格
     * @return 文件，若路径为null，返回null
     * @throws TaraException IO异常
     */
    public static File touch(String path) throws TaraException {
        if (path == null) {
            return null;
        }
        return touch(file(path));
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param file 文件对象
     * @return 文件，若路径为null，返回null
     * @throws TaraException IO异常
     */
    public static File touch(File file) throws TaraException {
        if (null == file) {
            return null;
        }
        if (!file.exists()) {
            mkParentDirs(file);
            try {
                // noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (Exception e) {
                throw new TaraException(e);
            }
        }
        return file;
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param parent 父文件对象
     * @param path   文件路径
     * @return File
     * @throws TaraException IO异常
     */
    public static File touch(File parent, String path) throws TaraException {
        return touch(file(parent, path));
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param parent 父文件对象
     * @param path   文件路径
     * @return File
     * @throws TaraException IO异常
     */
    public static File touch(String parent, String path) throws TaraException {
        return touch(file(parent, path));
    }

    /**
     * 创建所给文件或目录的父目录
     *
     * @param file 文件或目录
     * @return 父目录
     */
    public static File mkParentDirs(File file) {
        if (null == file) {
            return null;
        }
        return mkdir(getParent(file, 1));
    }

    /**
     * 创建父文件夹，如果存在直接返回此文件夹
     *
     * @param path 文件夹路径，使用POSIX格式，无论哪个平台
     * @return 创建的目录
     */
    public static File mkParentDirs(String path) {
        if (path == null) {
            return null;
        }
        return mkParentDirs(file(path));
    }

    /**
     * 删除文件或者文件夹<br>
     * 路径如果为相对路径，会转换为ClassPath路径！ 注意：删除文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param fullFileOrDirPath 文件或者目录的路径
     * @return 成功与否
     * @throws TaraException IO异常
     */
    public static boolean del(String fullFileOrDirPath) throws TaraException {
        return del(file(fullFileOrDirPath));
    }

    /**
     * 删除文件或者文件夹<br>
     * 注意：删除文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * <p>
     * 从5.7.6开始，删除文件使用{@link Files#delete(Path)}代替 {@link File#delete()}<br>
     * 因为前者遇到文件被占用等原因时，抛出异常，而非返回false，异常会指明具体的失败原因。
     * </p>
     *
     * @param file 文件对象
     * @return 成功与否
     * @throws TaraException IO异常
     * @see Files#delete(Path)
     */
    public static boolean del(File file) throws TaraException {
        if (file == null || !file.exists()) {
            // 如果文件不存在或已被删除，此处返回true表示删除成功
            return true;
        }

        if (file.isDirectory()) {
            // 清空目录下所有文件和目录
            boolean isOk = clean(file);
            if (!isOk) {
                return false;
            }
        }

        // 删除文件或清空后的目录
        final Path path = file.toPath();
        try {
            delFile(path);
        } catch (DirectoryNotEmptyException e) {
            // 遍历清空目录没有成功，此时补充删除一次（可能存在部分软链）
            del(path);
        } catch (IOException e) {
            throw new TaraException(e);
        }

        return true;
    }

    /**
     * 清空文件夹<br>
     * 注意：清空文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param dirPath 文件夹路径
     * @return 成功与否
     * @throws TaraException IO异常
     * @since 4.0.8
     */
    public static boolean clean(String dirPath) throws TaraException {
        return clean(file(dirPath));
    }

    /**
     * 清空文件夹<br>
     * 注意：清空文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param directory 文件夹
     * @return 成功与否
     * @throws TaraException IO异常
     * @since 3.0.6
     */
    public static boolean clean(File directory) throws TaraException {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return true;
        }

        final File[] files = directory.listFiles();
        if (null != files) {
            for (File childFile : files) {
                if (!del(childFile)) {
                    // 删除一个出错则本次删除任务失败
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 清理空文件夹<br>
     * 此方法用于递归删除空的文件夹，不删除文件<br>
     * 如果传入的文件夹本身就是空的，删除这个文件夹
     *
     * @param directory 文件夹
     * @return 成功与否
     * @throws TaraException IO异常
     * @since 4.5.5
     */
    public static boolean cleanEmpty(File directory) throws TaraException {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return true;
        }

        final File[] files = directory.listFiles();
        if (ArrayUtils.isEmpty(files)) {
            // 空文件夹则删除之
            return directory.delete();
        }

        for (File childFile : files) {
            cleanEmpty(childFile);
        }
        return true;
    }

    /**
     * 创建文件夹，如果存在直接返回此文件夹<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param dirPath 文件夹路径，使用POSIX格式，无论哪个平台
     * @return 创建的目录
     */
    public static File mkdir(String dirPath) {
        if (dirPath == null) {
            return null;
        }
        final File dir = file(dirPath);
        return mkdir(dir);
    }

    /**
     * 创建文件夹，会递归自动创建其不存在的父文件夹，如果存在直接返回此文件夹<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型<br>
     *
     * @param dir 目录
     * @return 创建的目录
     */
    public static File mkdir(File dir) {
        if (dir == null) {
            return null;
        }
        if (!dir.exists()) {
            mkdirsSafely(dir);
        }
        return dir;
    }

    /**
     * 安全地级联创建目录 (确保并发环境下能创建成功)
     *
     * <pre>
     *     并发环境下，假设 test 目录不存在，如果线程A mkdirs "test/A" 目录，线程B mkdirs "test/B"目录，
     *     其中一个线程可能会失败，进而导致以下代码抛出 FileNotFoundException 异常
     *
     *     file.getParentFile().mkdirs(); // 父目录正在被另一个线程创建中，返回 false
     *     file.createNewFile(); // 抛出 IO 异常，因为该线程无法感知到父目录已被创建
     * </pre>
     *
     * @param dir 待创建的目录
     * @return true表示创建成功，false表示创建失败
     * @since 5.7.21
     */
    public static boolean mkdirsSafely(File dir) {
        if (dir == null) {
            return false;
        }
        if (dir.isDirectory()) {
            return true;
        }
        return dir.exists();
    }

    /**
     * 创建临时文件<br>
     * 创建后的文件名为 prefix[Randon].tmp
     *
     * @param dir 临时文件创建的所在目录
     * @return 临时文件
     * @throws TaraException IO异常
     */
    public static File createTempFile(File dir) throws TaraException {
        return createTempFile("tara", null, dir, true);
    }

    /**
     * 在默认临时文件目录下创建临时文件，创建后的文件名为 prefix[Randon].tmp。
     * 默认临时文件目录由系统属性 {@code java.io.tmpdir} 指定。
     * 在 UNIX 系统上，此属性的默认值通常是 {@code "tmp"} 或 {@code "vartmp"}；
     * 在 Microsoft Windows 系统上，它通常是 {@code "C:\\WINNT\\TEMP"}。
     * 调用 Java 虚拟机时，可以为该系统属性赋予不同的值，但不保证对该属性的编程更改对该方法使用的临时目录有任何影响。
     *
     * @return 临时文件
     * @throws TaraException IO异常
     * @since 5.7.22
     */
    public static File createTempFile() throws TaraException {
        return createTempFile("tara", null, null, true);
    }

    /**
     * 在默认临时文件目录下创建临时文件，创建后的文件名为 prefix[Randon].suffix。
     * 默认临时文件目录由系统属性 {@code java.io.tmpdir} 指定。
     * 在 UNIX 系统上，此属性的默认值通常是 {@code "tmp"} 或 {@code "vartmp"}；
     * 在 Microsoft Windows 系统上，它通常是 {@code "C:\\WINNT\\TEMP"}。
     * 调用 Java 虚拟机时，可以为该系统属性赋予不同的值，但不保证对该属性的编程更改对该方法使用的临时目录有任何影响。
     *
     * @param suffix    后缀，如果null则使用默认.tmp
     * @param isReCreat 是否重新创建文件（删掉原来的，创建新的）
     * @return 临时文件
     * @throws TaraException IO异常
     * @since 5.7.22
     */
    public static File createTempFile(String suffix, boolean isReCreat) throws TaraException {
        return createTempFile("tara", suffix, null, isReCreat);
    }

    /**
     * 在默认临时文件目录下创建临时文件，创建后的文件名为 prefix[Randon].suffix。
     * 默认临时文件目录由系统属性 {@code java.io.tmpdir} 指定。
     * 在 UNIX 系统上，此属性的默认值通常是 {@code "tmp"} 或 {@code "vartmp"}；
     * 在 Microsoft Windows 系统上，它通常是 {@code "C:\\WINNT\\TEMP"}。
     * 调用 Java 虚拟机时，可以为该系统属性赋予不同的值，但不保证对该属性的编程更改对该方法使用的临时目录有任何影响。
     *
     * @param prefix    前缀，至少3个字符
     * @param suffix    后缀，如果null则使用默认.tmp
     * @param isReCreat 是否重新创建文件（删掉原来的，创建新的）
     * @return 临时文件
     * @throws TaraException IO异常
     * @since 5.7.22
     */
    public static File createTempFile(String prefix, String suffix, boolean isReCreat) throws TaraException {
        return createTempFile(prefix, suffix, null, isReCreat);
    }

    /**
     * 创建临时文件<br>
     * 创建后的文件名为 prefix[Randon].tmp
     *
     * @param dir       临时文件创建的所在目录
     * @param isReCreat 是否重新创建文件（删掉原来的，创建新的）
     * @return 临时文件
     * @throws TaraException IO异常
     */
    public static File createTempFile(File dir, boolean isReCreat) throws TaraException {
        return createTempFile("tara", null, dir, isReCreat);
    }

    /**
     * 创建临时文件<br>
     * 创建后的文件名为 prefix[Randon].suffix From com.jodd.io.ArrayUtils
     *
     * @param prefix    前缀，至少3个字符
     * @param suffix    后缀，如果null则使用默认.tmp
     * @param dir       临时文件创建的所在目录
     * @param isReCreat 是否重新创建文件（删掉原来的，创建新的）
     * @return 临时文件
     * @throws TaraException IO异常
     */
    public static File createTempFile(String prefix, String suffix, File dir, boolean isReCreat) throws TaraException {
        int exceptionsCount = 0;
        while (true) {
            try {
                File file = File.createTempFile(prefix, suffix, mkdir(dir)).getCanonicalFile();
                if (isReCreat) {
                    // noinspection ResultOfMethodCallIgnored
                    file.delete();
                    // noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                }
                return file;
            } catch (IOException ioex) { // fixes java.io.WinNTFileSystem.createFileExclusively access denied
                if (++exceptionsCount >= 50) {
                    throw new TaraException(ioex);
                }
            }
        }
    }

    /**
     * 通过JDK7+的 Files#copy(Path, Path, CopyOption...) 方法拷贝文件
     *
     * @param src     源文件路径
     * @param dest    目标文件或目录路径，如果为目录使用与源文件相同的文件名
     * @param options {@link StandardCopyOption}
     * @return File
     * @throws TaraException IO异常
     */
    public static File copyFile(String src, String dest, StandardCopyOption... options) throws TaraException {
        Assert.notBlank(src, "Source File path is blank !");
        Assert.notBlank(dest, "Destination File path is blank !");
        return copyFile(Paths.get(src), Paths.get(dest), options).toFile();
    }

    /**
     * 通过JDK7+的 Files#copy(Path, Path, CopyOption...) 方法拷贝文件
     *
     * @param src     源文件
     * @param dest    目标文件或目录，如果为目录使用与源文件相同的文件名
     * @param options {@link StandardCopyOption}
     * @return 目标文件
     * @throws TaraException IO异常
     */
    public static File copyFile(File src, File dest, StandardCopyOption... options) throws TaraException {
        // check
        Assert.notNull(src, "Source File is null !");
        if (!src.exists()) {
            throw new TaraException("File not exist: " + src);
        }
        Assert.notNull(dest, "Destination File or directiory is null !");
        if (equals(src, dest)) {
            throw new TaraException("Files '{}' and '{}' are equal", src, dest);
        }
        return copyFile(src.toPath(), dest.toPath(), options).toFile();
    }

    /**
     * 移动文件或者目录
     *
     * @param src        源文件或者目录
     * @param target     目标文件或者目录
     * @param isOverride 是否覆盖目标，只有目标为文件才覆盖
     * @throws TaraException IO异常
     */
    public static void move(File src, File target, boolean isOverride) throws TaraException {
        Assert.notNull(src, "Src file must be not null!");
        Assert.notNull(target, "target file must be not null!");
        move(src.toPath(), target.toPath(), isOverride);
    }

    /**
     * 移动文件或者目录
     *
     * @param src        源文件或者目录
     * @param target     目标文件或者目录
     * @param isOverride 是否覆盖目标，只有目标为文件才覆盖
     * @throws TaraException IO异常
     * @since 5.7.9
     */
    public static void moveContent(File src, File target, boolean isOverride) throws TaraException {
        Assert.notNull(src, "Src file must be not null!");
        Assert.notNull(target, "target file must be not null!");
        moveContent(src.toPath(), target.toPath(), isOverride);
    }

    /**
     * 修改文件或目录的文件名，不变更路径，只是简单修改文件名，不保留扩展名。<br>
     *
     * <pre>
     * ArrayUtils.rename(file, "aaa.png", true) xx/xx.png =》xx/aaa.png
     * </pre>
     *
     * @param file       被修改的文件
     * @param newName    新的文件名，如需扩展名，需自行在此参数加上，原文件名的扩展名不会被保留
     * @param isOverride 是否覆盖目标文件
     * @return 目标文件
     * @since 5.3.6
     */
    public static File rename(File file, String newName, boolean isOverride) {
        return rename(file, newName, false, isOverride);
    }

    /**
     * 修改文件或目录的文件名，不变更路径，只是简单修改文件名<br>
     * 重命名有两种模式：<br>
     * 1、isRetainExt为true时，保留原扩展名：
     *
     * <pre>
     * ArrayUtils.rename(file, "aaa", true) xx/xx.png =》xx/aaa.png
     * </pre>
     *
     * <p>
     * 2、isRetainExt为false时，不保留原扩展名，需要在newName中
     *
     * <pre>
     * ArrayUtils.rename(file, "aaa.jpg", false) xx/xx.png =》xx/aaa.jpg
     * </pre>
     *
     * @param file        被修改的文件
     * @param newName     新的文件名，可选是否包括扩展名
     * @param isRetainExt 是否保留原文件的扩展名，如果保留，则newName不需要加扩展名
     * @param isOverride  是否覆盖目标文件
     * @return 目标文件
     * @since 3.0.9
     */
    public static File rename(File file, String newName, boolean isRetainExt, boolean isOverride) {
        if (isRetainExt) {
            final String extName = FileUtils.extName(file);
            if (StringUtils.isNotBlank(extName)) {
                newName = newName.concat(".").concat(extName);
            }
        }
        return rename(file.toPath(), newName, isOverride).toFile();
    }

    /**
     * 获取规范的绝对路径
     *
     * @param file 文件
     * @return 规范绝对路径，如果传入file为null，返回null
     * @since 4.1.4
     */
    public static String getCanonicalPath(File file) {
        if (null == file) {
            return null;
        }
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new TaraException(e);
        }
    }

    /**
     * 获取绝对路径<br>
     * 此方法不会判定给定路径是否有效（文件或目录存在）
     *
     * @param path      相对路径
     * @param baseClass 相对路径所相对的类
     * @return 绝对路径
     */
    public static String getAbsolutePath(String path, Class<?> baseClass) {
        String normalPath;
        if (path == null) {
            normalPath = StringUtils.EMPTY;
        } else {
            normalPath = normalize(path);
            if (isAbsolutePath(normalPath)) {
                // 给定的路径已经是绝对路径了
                return normalPath;
            }
        }

        // 相对于ClassPath路径
        final URL url = ResourceUtils.getResource(normalPath, baseClass);
        if (null != url) {
            // 对于jar中文件包含file:前缀，需要去掉此类前缀，在此做标准化，since 3.0.8 解决中文或空格路径被编码的问题
            return normalize(URLUtils.getDecodedPath(url));
        }

        // 如果资源不存在，则返回一个拼接的资源绝对路径
        final String classPath = ClassUtils.getClassPath();
        if (null == classPath) {
            // throw new NullPointerException("ClassPath is null !");
            // 在jar运行模式中，ClassPath有可能获取不到，此时返回原始相对路径（此时获取的文件为相对工作目录）
            return path;
        }

        // 资源不存在的情况下使用标准化路径有问题，使用原始路径拼接后标准化路径
        return normalize(classPath.concat(Objects.requireNonNull(path)));
    }

    /**
     * 获取绝对路径，相对于ClassPath的目录<br>
     * 如果给定就是绝对路径，则返回原路径，原路径把所有\替换为/<br>
     * 兼容Spring风格的路径表示，例如：classpath:config/example.setting也会被识别后转换
     *
     * @param path 相对路径
     * @return 绝对路径
     */
    public static String getAbsolutePath(String path) {
        return getAbsolutePath(path, null);
    }

    /**
     * 获取标准的绝对路径
     *
     * @param file 文件
     * @return 绝对路径
     */
    public static String getAbsolutePath(File file) {
        if (file == null) {
            return null;
        }

        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            return file.getAbsolutePath();
        }
    }

    /**
     * 给定路径已经是绝对路径<br>
     * 此方法并没有针对路径做标准化，建议先执行{@link #normalize(String)}方法标准化路径后判断<br>
     * 绝对路径判断条件是：
     * <ul>
     * <li>以/开头的路径</li>
     * <li>满足类似于 c:/xxxxx，其中祖母随意，不区分大小写</li>
     * <li>满足类似于 d:\xxxxx，其中祖母随意，不区分大小写</li>
     * </ul>
     *
     * @param path 需要检查的Path
     * @return 是否已经是绝对路径
     */
    public static boolean isAbsolutePath(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }

        // 给定的路径已经是绝对路径了
        return StringUtils.C_SLASH == path.charAt(0) || RegexUtils.isMatch(PATTERN_PATH_ABSOLUTE, path);
    }

    /**
     * 判断是否为目录，如果path为null，则返回false
     *
     * @param path 文件路径
     * @return 如果为目录true
     */
    public static boolean isDirectory(String path) {
        return (null != path) && file(path).isDirectory();
    }

    /**
     * 判断是否为目录，如果file为null，则返回false
     *
     * @param file 文件
     * @return 如果为目录true
     */
    public static boolean isDirectory(File file) {
        return (null != file) && file.isDirectory();
    }

    /**
     * 判断是否为文件，如果path为null，则返回false
     *
     * @param path 文件路径
     * @return 如果为文件true
     */
    public static boolean isFile(String path) {
        return (null != path) && file(path).isFile();
    }

    /**
     * 判断是否为文件，如果file为null，则返回false
     *
     * @param file 文件
     * @return 如果为文件true
     */
    public static boolean isFile(File file) {
        return (null != file) && file.isFile();
    }

    /**
     * 检查两个文件是否是同一个文件<br>
     * 所谓文件相同，是指File对象是否指向同一个文件或文件夹
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 是否相同
     * @throws TaraException IO异常
     */
    public static boolean equals(File file1, File file2) throws TaraException {
        Assert.notNull(file1);
        Assert.notNull(file2);
        if (!file1.exists() || !file2.exists()) {
            // 两个文件都不存在判断其路径是否相同， 对于一个存在一个不存在的情况，一定不相同
            return !file1.exists()//
                    && !file2.exists()//
                    && pathEquals(file1, file2);
        }
        return equals(file1.toPath(), file2.toPath());
    }

    /**
     * 比较两个文件内容是否相同<br>
     * 首先比较长度，长度一致再比较内容<br>
     * 此方法来自Apache Commons io
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 两个文件内容一致返回true，否则false
     * @throws TaraException IO异常
     * @since 4.0.6
     */
    public static boolean contentEquals(File file1, File file2) throws TaraException {
        boolean file1Exists = file1.exists();
        if (file1Exists != file2.exists()) {
            return false;
        }

        if (!file1Exists) {
            // 两个文件都不存在，返回true
            return true;
        }

        if (file1.isDirectory() || file2.isDirectory()) {
            // 不比较目录
            throw new TaraException("Can't compare directories, only files");
        }

        if (file1.length() != file2.length()) {
            // 文件长度不同
            return false;
        }

        if (equals(file1, file2)) {
            // 同一个文件
            return true;
        }

        try (InputStream input1 = getInputStream(file1); InputStream input2 = getInputStream(file2)) {
            return IOUtils.contentEquals(input1, input2);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 文件路径是否相同<br>
     * 取两个文件的绝对路径比较，在Windows下忽略大小写，在Linux下不忽略。
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 文件路径是否相同
     * @since 3.0.9
     */
    public static boolean pathEquals(File file1, File file2) {
        if (isWindows()) {
            // Windows环境
            try {
                if (StringUtils.equalsIgnoreCase(file1.getCanonicalPath(), file2.getCanonicalPath())) {
                    return true;
                }
            } catch (Exception e) {
                if (StringUtils.equalsIgnoreCase(file1.getAbsolutePath(), file2.getAbsolutePath())) {
                    return true;
                }
            }
        } else {
            // 类Unix环境
            try {
                if (StringUtils.equals(file1.getCanonicalPath(), file2.getCanonicalPath())) {
                    return true;
                }
            } catch (Exception e) {
                if (StringUtils.equals(file1.getAbsolutePath(), file2.getAbsolutePath())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获得最后一个文件路径分隔符的位置
     *
     * @param filePath 文件路径
     * @return 最后一个文件路径分隔符的位置
     */
    public static int lastIndexOfSeparator(String filePath) {
        if (StringUtils.isNotEmpty(filePath)) {
            int i = filePath.length();
            char c;
            while (--i >= 0) {
                c = filePath.charAt(i);
                if (CharUtils.isFileSeparator(c)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 判断文件是否被改动<br>
     * 如果文件对象为 null 或者文件不存在，被视为改动
     *
     * @param file           文件对象
     * @param lastModifyTime 上次的改动时间
     * @return 是否被改动
     * @deprecated 拼写错误，请使用{@link #isModified(File, long)}
     */
    @Deprecated
    public static boolean isModifed(File file, long lastModifyTime) {
        return isModified(file, lastModifyTime);
    }

    /**
     * 判断文件是否被改动<br>
     * 如果文件对象为 null 或者文件不存在，被视为改动
     *
     * @param file           文件对象
     * @param lastModifyTime 上次的改动时间
     * @return 是否被改动
     */
    public static boolean isModified(File file, long lastModifyTime) {
        if (null == file || !file.exists()) {
            return true;
        }
        return file.lastModified() != lastModifyTime;
    }

    /**
     * 修复路径<br>
     * 如果原路径尾部有分隔符，则保留为标准分隔符（/），否则不保留
     * <ol>
     * <li>1. 统一用 /</li>
     * <li>2. 多个 / 转换为一个 /</li>
     * <li>3. 去除左边空格</li>
     * <li>4. .. 和 . 转换为绝对路径，当..多于已有路径时，直接返回根路径</li>
     * </ol>
     * <p>
     * 栗子：
     *
     * <pre>
     * "/foo//" =》 "/foo/"
     * "/foo/./" =》 "/foo/"
     * "/foo/../bar" =》 "/bar"
     * "/foo/../bar/" =》 "/bar/"
     * "/foo/../bar/../baz" =》 "/baz"
     * "/../" =》 "/"
     * "foo/bar/.." =》 "foo"
     * "foo/../bar" =》 "bar"
     * "foo/../../bar" =》 "bar"
     * "//server/foo/../bar" =》 "/server/bar"
     * "//server/../bar" =》 "/bar"
     * "C:\\foo\\..\\bar" =》 "C:/bar"
     * "C:\\..\\bar" =》 "C:/bar"
     * "~/foo/../bar/" =》 "~/bar/"
     * "~/../bar" =》 普通用户运行是'bar的home目录'，ROOT用户运行是'/bar'
     * </pre>
     *
     * @param path 原路径
     * @return 修复后的路径
     */
    public static String normalize(String path) {
        if (path == null) {
            return null;
        }

        // 兼容Spring风格的ClassPath路径，去除前缀，不区分大小写
        String pathToUse = StringUtils.removePrefixIgnoreCase(path, URLUtils.CLASSPATH_URL_PREFIX);
        // 去除file:前缀
        pathToUse = StringUtils.removePrefixIgnoreCase(pathToUse, URLUtils.FILE_URL_PREFIX);

        // 识别home目录形式，并转换为绝对路径
        if (StringUtils.startWith(pathToUse, '~')) {
            pathToUse = getUserHomePath() + pathToUse.substring(1);
        }

        // 统一使用斜杠
        pathToUse = pathToUse.replaceAll("[/\\\\]+", StringUtils.SLASH);
        // 去除开头空白符，末尾空白符合法，不去除
        pathToUse = StringUtils.trimStart(pathToUse);
        // 兼容Windows下的共享目录路径（原始路径如果以\\开头，则保留这种路径）
        if (path.startsWith("\\\\")) {
            pathToUse = "\\" + pathToUse;
        }

        String prefix = StringUtils.EMPTY;
        int prefixIndex = pathToUse.indexOf(StringUtils.COLON);
        if (prefixIndex > -1) {
            // 可能Windows风格路径
            prefix = pathToUse.substring(0, prefixIndex + 1);
            if (StringUtils.startWith(prefix, StringUtils.C_SLASH)) {
                // 去除类似于/C:这类路径开头的斜杠
                prefix = prefix.substring(1);
            }
            if (!prefix.contains(StringUtils.SLASH)) {
                pathToUse = pathToUse.substring(prefixIndex + 1);
            } else {
                // 如果前缀中包含/,说明非Windows风格path
                prefix = StringUtils.EMPTY;
            }
        }
        if (pathToUse.startsWith(StringUtils.SLASH)) {
            prefix += StringUtils.SLASH;
            pathToUse = pathToUse.substring(1);
        }

        List<String> pathList = StringUtils.split(pathToUse, StringUtils.C_SLASH);

        List<String> pathElements = new LinkedList<>();
        int tops = 0;
        String element;
        for (int i = pathList.size() - 1; i >= 0; i--) {
            element = pathList.get(i);
            // 只处理非.的目录，即只处理非当前目录
            if (!StringUtils.DOT.equals(element)) {
                if (StringUtils.DOUBLE_DOT.equals(element)) {
                    tops++;
                } else {
                    if (tops > 0) {
                        // 有上级目录标记时按照个数依次跳过
                        tops--;
                    } else {
                        // Normal path element found.
                        pathElements.add(0, element);
                    }
                }
            }
        }

        // issue#1703@Github
        if (tops > 0 && StringUtils.isEmpty(prefix)) {
            // 只有相对路径补充开头的..，绝对路径直接忽略之
            while (tops-- > 0) {
                // 遍历完节点发现还有上级标注（即开头有一个或多个..），补充之
                // Normal path element found.
                pathElements.add(0, StringUtils.DOUBLE_DOT);
            }
        }

        return prefix + String.join(StringUtils.SLASH, pathElements);
    }

    /**
     * 获得相对子路径
     * <p>
     * 栗子：
     *
     * <pre>
     * dirPath: d:/aaa/bbb    filePath: d:/aaa/bbb/ccc     =》    ccc
     * dirPath: d:/Aaa/bbb    filePath: d:/aaa/bbb/ccc.txt     =》    ccc.txt
     * </pre>
     *
     * @param rootDir 绝对父路径
     * @param file    文件
     * @return 相对子路径
     */
    public static String subPath(String rootDir, File file) {
        try {
            return subPath(rootDir, file.getCanonicalPath());
        } catch (IOException e) {
            throw new TaraException(e);
        }
    }

    /**
     * 获得相对子路径，忽略大小写
     * <p>
     * 栗子：
     *
     * <pre>
     * dirPath: d:/aaa/bbb    filePath: d:/aaa/bbb/ccc     =》    ccc
     * dirPath: d:/Aaa/bbb    filePath: d:/aaa/bbb/ccc.txt     =》    ccc.txt
     * dirPath: d:/Aaa/bbb    filePath: d:/aaa/bbb/     =》    ""
     * </pre>
     *
     * @param dirPath  父路径
     * @param filePath 文件路径
     * @return 相对子路径
     */
    public static String subPath(String dirPath, String filePath) {
        if (StringUtils.isNotEmpty(dirPath) && StringUtils.isNotEmpty(filePath)) {

            dirPath = StringUtils.removeSuffix(normalize(dirPath), "/");
            filePath = normalize(filePath);

            final String result = StringUtils.removePrefixIgnoreCase(filePath, dirPath);
            return StringUtils.removePrefix(result, "/");
        }
        return filePath;
    }

    // --------------------------------------------------------------------------------------------
    // name start

    /**
     * 返回文件名
     *
     * @param file 文件
     * @return 文件名
     * @see FileNameUtils#getName(File)
     * @since 4.1.13
     */
    public static String getName(File file) {
        return FileNameUtils.getName(file);
    }

    /**
     * 返回文件名<br>
     *
     * <pre>
     * "d:/test/aaa" 返回 "aaa"
     * "/test/aaa.jpg" 返回 "aaa.jpg"
     * </pre>
     *
     * @param filePath 文件
     * @return 文件名
     * @see FileNameUtils#getName(String)
     * @since 4.1.13
     */
    public static String getName(String filePath) {
        return FileNameUtils.getName(filePath);
    }

    /**
     * 获取文件后缀名，扩展名不带“.”
     *
     * @param file 文件
     * @return 扩展名
     * @see FileNameUtils#getSuffix(File)
     * @since 5.3.8
     */
    public static String getSuffix(File file) {
        return FileNameUtils.getSuffix(file);
    }

    /**
     * 获得文件后缀名，扩展名不带“.”
     *
     * @param fileName 文件名
     * @return 扩展名
     * @see FileNameUtils#getSuffix(String)
     * @since 5.3.8
     */
    public static String getSuffix(String fileName) {
        return FileNameUtils.getSuffix(fileName);
    }

    /**
     * 返回主文件名
     *
     * @param file 文件
     * @return 主文件名
     * @see FileNameUtils#getPrefix(File)
     * @since 5.3.8
     */
    public static String getPrefix(File file) {
        return FileNameUtils.getPrefix(file);
    }

    /**
     * 返回主文件名
     *
     * @param fileName 完整文件名
     * @return 主文件名
     * @see FileNameUtils#getPrefix(String)
     * @since 5.3.8
     */
    public static String getPrefix(String fileName) {
        return FileNameUtils.getPrefix(fileName);
    }

    /**
     * 返回主文件名
     *
     * @param file 文件
     * @return 主文件名
     * @see FileNameUtils#mainName(File)
     */
    public static String mainName(File file) {
        return FileNameUtils.mainName(file);
    }

    /**
     * 返回主文件名
     *
     * @param fileName 完整文件名
     * @return 主文件名
     * @see FileNameUtils#mainName(String)
     */
    public static String mainName(String fileName) {
        return FileNameUtils.mainName(fileName);
    }

    /**
     * 获取文件扩展名（后缀名），扩展名不带“.”
     *
     * @param file 文件
     * @return 扩展名
     * @see FileNameUtils#extName(File)
     */
    public static String extName(File file) {
        return FileNameUtils.extName(file);
    }

    /**
     * 获得文件的扩展名（后缀名），扩展名不带“.”
     *
     * @param fileName 文件名
     * @return 扩展名
     * @see FileNameUtils#extName(String)
     */
    public static String extName(String fileName) {
        return FileNameUtils.extName(fileName);
    }
    // --------------------------------------------------------------------------------------------
    // name end

    /**
     * 判断文件路径是否有指定后缀，忽略大小写<br>
     * 常用语判断扩展名
     *
     * @param file   文件或目录
     * @param suffix 后缀
     * @return 是否有指定后缀
     */
    public static boolean pathEndsWith(File file, String suffix) {
        return file.getPath().toLowerCase().endsWith(suffix);
    }

    // --------------------------------------------------------------------------------------------
    // in start

    /**
     * 获得输入流
     *
     * @param file 文件
     * @return 输入流
     * @throws TaraException 文件未找到
     */
    public static BufferedInputStream getInputStream(File file) {
        try {
            return new BufferedInputStream(Files.newInputStream(file.toPath()));
        } catch (IOException e) {
            throw new TaraException(e);
        }
    }

    /**
     * 获得输入流
     *
     * @param path 文件路径
     * @return 输入流
     * @throws TaraException 文件未找到
     */
    public static BufferedInputStream getInputStream(String path) throws TaraException {
        return getInputStream(file(path));
    }

    /**
     * 获得BOM输入流，用于处理带BOM头的文件
     *
     * @param file 文件
     * @return 输入流
     * @throws TaraException 文件未找到
     */
    public static BOMInputStream getBOMInputStream(File file) throws TaraException {
        try {
            return new BOMInputStream(Files.newInputStream(file.toPath()));
        } catch (IOException e) {
            throw new TaraException(e);
        }
    }

    /**
     * 获得一个文件读取器
     *
     * @param file 文件
     * @return BufferedReader对象
     * @throws TaraException IO异常
     */
    public static BufferedReader getUtf8Reader(File file) throws TaraException {
        return getReader(file, StandardCharsets.UTF_8);
    }

    /**
     * 获得一个文件读取器
     *
     * @param path 文件路径
     * @return BufferedReader对象
     * @throws TaraException IO异常
     */
    public static BufferedReader getUtf8Reader(String path) throws TaraException {
        return getReader(path, StandardCharsets.UTF_8);
    }

    /**
     * 获得一个文件读取器
     *
     * @param file        文件
     * @param charsetName 字符集
     * @return BufferedReader对象
     * @throws TaraException IO异常
     * @deprecated 请使用 {@link #getReader(File, Charset)}
     */
    @Deprecated
    public static BufferedReader getReader(File file, String charsetName) {
        try {
            return new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), charsetName));
        } catch (Exception e) {
            throw new TaraException(e);
        }
    }

    /**
     * 获得一个文件读取器
     *
     * @param file    文件
     * @param charset 字符集
     * @return BufferedReader对象
     * @throws TaraException IO异常
     */
    public static BufferedReader getReader(File file, Charset charset) {
        try {
            return new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), charset));
        } catch (IOException e) {
            throw new TaraException(e);
        }
    }

    /**
     * 获得一个文件读取器
     *
     * @param path        绝对路径
     * @param charsetName 字符集
     * @return BufferedReader对象
     * @throws TaraException IO异常
     * @deprecated 请使用 {@link #getReader(String, Charset)}
     */
    @Deprecated
    public static BufferedReader getReader(String path, String charsetName) throws TaraException {
        return getReader(path, Charset.forName(charsetName));
    }

    /**
     * 获得一个文件读取器
     *
     * @param path    绝对路径
     * @param charset 字符集
     * @return BufferedReader对象
     * @throws TaraException IO异常
     */
    public static BufferedReader getReader(String path, Charset charset) throws TaraException {
        return getReader(file(path), charset);
    }

    /**
     * 获取当前系统的换行分隔符
     *
     * <pre>
     * Windows: \r\n
     * Mac: \r
     * Linux: \n
     * </pre>
     *
     * @return 换行符
     * @since 4.0.5
     */
    public static String getLineSeparator() {
        return System.lineSeparator();
        // return System.getProperty("line.separator");
    }

    /**
     * 清除文件名中的在Windows下不支持的非法字符，包括： \ / : * ? " &lt; &gt; |
     *
     * @param fileName 文件名（必须不包括路径，否则路径符将被替换）
     * @return 清理后的文件名
     * @see FileNameUtils#cleanInvalid(String)
     * @since 3.3.1
     */
    public static String cleanInvalid(String fileName) {
        return FileNameUtils.cleanInvalid(fileName);
    }

    /**
     * 文件名中是否包含在Windows下不支持的非法字符，包括： \ / : * ? " &lt; &gt; |
     *
     * @param fileName 文件名（必须不包括路径，否则路径符将被替换）
     * @return 是否包含非法字符
     * @see FileNameUtils#containsInvalid(String)
     * @since 3.3.1
     */
    public static boolean containsInvalid(String fileName) {
        return FileNameUtils.containsInvalid(fileName);
    }

    /**
     * 计算文件CRC32校验码
     *
     * @param file 文件，不能为目录
     * @return CRC32值
     * @throws TaraException IO异常
     * @since 4.0.6
     */
    public static long checksumCRC32(File file) throws TaraException {
        return checksum(file, new CRC32()).getValue();
    }

    /**
     * 计算文件校验码
     *
     * @param file     文件，不能为目录
     * @param checksum {@link Checksum}
     * @return Checksum
     * @throws TaraException IO异常
     * @since 4.0.6
     */
    public static Checksum checksum(File file, Checksum checksum) throws TaraException {
        Assert.notNull(file, "File is null !");
        if (file.isDirectory()) {
            throw new IllegalArgumentException("Checksums can't be computed on directories");
        }
        try {
            return org.apache.commons.io.FileUtils.checksum(file, checksum);
        } catch (Exception e) {
            throw new TaraException(e);
        }
    }

    /**
     * 获取Web项目下的web root路径<br>
     * 原理是首先获取ClassPath路径，由于在web项目中ClassPath位于 WEB-INF/classes/下，故向上获取两级目录即可。
     *
     * @return web root路径
     * @since 4.0.13
     */
    public static File getWebRoot() {
        final String classPath = ClassUtils.getClassPath();
        if (StringUtils.isNotBlank(classPath)) {
            return getParent(file(classPath), 2);
        }
        return null;
    }

    /**
     * 获取指定层级的父路径
     *
     * <pre>
     * getParent("d:/aaa/bbb/cc/ddd", 0) -》 "d:/aaa/bbb/cc/ddd"
     * getParent("d:/aaa/bbb/cc/ddd", 2) -》 "d:/aaa/bbb"
     * getParent("d:/aaa/bbb/cc/ddd", 4) -》 "d:/"
     * getParent("d:/aaa/bbb/cc/ddd", 5) -》 null
     * </pre>
     *
     * @param filePath 目录或文件路径
     * @param level    层级
     * @return 路径File，如果不存在返回null
     * @since 4.1.2
     */
    public static String getParent(String filePath, int level) {
        final File parent = getParent(file(filePath), level);
        try {
            return null == parent ? null : parent.getCanonicalPath();
        } catch (IOException e) {
            throw new TaraException(e);
        }
    }

    /**
     * 获取指定层级的父路径
     *
     * <pre>
     * getParent(file("d:/aaa/bbb/cc/ddd", 0)) -》 "d:/aaa/bbb/cc/ddd"
     * getParent(file("d:/aaa/bbb/cc/ddd", 2)) -》 "d:/aaa/bbb"
     * getParent(file("d:/aaa/bbb/cc/ddd", 4)) -》 "d:/"
     * getParent(file("d:/aaa/bbb/cc/ddd", 5)) -》 null
     * </pre>
     *
     * @param file  目录或文件
     * @param level 层级
     * @return 路径File，如果不存在返回null
     * @since 4.1.2
     */
    public static File getParent(File file, int level) {
        if (level < 1 || null == file) {
            return file;
        }

        File parentFile;
        try {
            parentFile = file.getCanonicalFile().getParentFile();
        } catch (IOException e) {
            throw new TaraException(e);
        }
        if (1 == level) {
            return parentFile;
        }
        return getParent(parentFile, level - 1);
    }

    /**
     * 检查父完整路径是否为自路径的前半部分，如果不是说明不是子路径，可能存在slip注入。
     * <p>
     * 见http://blog.nsfocus.net/zip-slip-2/
     *
     * @param parentFile 父文件或目录
     * @param file       子文件或目录
     * @return 子文件或目录
     * @throws IllegalArgumentException 检查创建的子文件不在父目录中抛出此异常
     */
    public static File checkSlip(File parentFile, File file) throws IllegalArgumentException {
        if (null != parentFile && null != file) {
            String parentCanonicalPath;
            String canonicalPath;
            try {
                parentCanonicalPath = parentFile.getCanonicalPath();
                canonicalPath = file.getCanonicalPath();
            } catch (IOException e) {
                // issue#I4CWMO@Gitee
                // getCanonicalPath有时会抛出奇怪的IO异常，此时忽略异常，使用AbsolutePath判断。
                parentCanonicalPath = parentFile.getAbsolutePath();
                canonicalPath = file.getAbsolutePath();
            }
            if (!canonicalPath.startsWith(parentCanonicalPath)) {
                throw new IllegalArgumentException("New file is outside of the parent dir: " + file.getName());
            }
        }
        return file;
    }

    /**
     * 根据文件扩展名获得MimeType
     *
     * @param filePath 文件路径或文件名
     * @return MimeType
     * @since 4.1.15
     */
    public static String getMimeType(String filePath) {
        String contentType = URLConnection.getFileNameMap().getContentTypeFor(filePath);
        if (null == contentType) {
            // 补充一些常用的mimeType
            if (StringUtils.endWithIgnoreCase(filePath, ".css")) {
                contentType = "text/css";
            } else if (StringUtils.endWithIgnoreCase(filePath, ".js")) {
                contentType = "application/x-javascript";
            } else if (StringUtils.endWithIgnoreCase(filePath, ".rar")) {
                contentType = "application/x-rar-compressed";
            } else if (StringUtils.endWithIgnoreCase(filePath, ".7z")) {
                contentType = "application/x-7z-compressed";
            }
        }

        // 补充
        if (null == contentType) {
            contentType = getMimeType(Paths.get(filePath));
        }

        return contentType;
    }

    /**
     * 判断是否为符号链接文件
     *
     * @param file 被检查的文件
     * @return 是否为符号链接文件
     * @since 4.4.2
     */
    public static boolean isSymlink(File file) {
        return isSymlink(file.toPath());
    }

    /**
     * 判断给定的目录是否为给定文件或文件夹的子目录
     *
     * @param parent 父目录
     * @param sub    子目录
     * @return 子目录是否为父目录的子目录
     * @since 4.5.4
     */
    public static boolean isSub(File parent, File sub) {
        Assert.notNull(parent);
        Assert.notNull(sub);
        return isSub(parent.toPath(), sub.toPath());
    }

    /**
     * 根据压缩包中的路径构建目录结构，在Win下直接构建，在Linux下拆分路径单独构建
     *
     * @param outFile  最外部路径
     * @param fileName 文件名，可以包含路径
     * @return 文件或目录
     * @since 5.0.5
     */
    private static File buildFile(File outFile, String fileName) {
        // 替换Windows路径分隔符为Linux路径分隔符，便于统一处理
        fileName = fileName.replace('\\', '/');
        if (!isWindows()
                // 检查文件名中是否包含"/"，不考虑以"/"结尾的情况
                && fileName.lastIndexOf(CharUtils.SLASH, fileName.length() - 2) > 0) {
            // 在Linux下多层目录创建存在问题，/会被当成文件名的一部分，此处做处理
            // 使用/拆分路径（zip中无\），级联创建父目录
            final List<String> pathParts = StringUtils.split(fileName, '/', false, true);
            final int lastPartIndex = pathParts.size() - 1;// 目录个数
            for (int i = 0; i < lastPartIndex; i++) {
                // 由于路径拆分，slip不检查，在最后一步检查
                outFile = new File(outFile, pathParts.get(i));
            }
            // noinspection ResultOfMethodCallIgnored
            outFile.mkdirs();
            // 最后一个部分如果非空，作为文件名
            fileName = pathParts.get(lastPartIndex);
        }
        return new File(outFile, fileName);
    }

    /**
     * 目录是否为空
     *
     * @param dirPath 目录
     * @return 是否为空
     * @throws TaraException IOException
     */
    public static boolean isDirEmpty(Path dirPath) {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dirPath)) {
            return !dirStream.iterator().hasNext();
        } catch (IOException e) {
            throw new TaraException(e);
        }
    }

    /**
     * 递归遍历目录以及子目录中的所有文件<br>
     * 如果提供path为文件，直接返回过滤结果
     *
     * @param path       当前遍历文件或目录
     * @param fileFilter 文件过滤规则对象，选择要保留的文件，只对文件有效，不过滤目录，null表示接收全部文件
     * @return 文件列表
     * @since 5.4.1
     */
    public static List<File> loopFiles(Path path, FileFilter fileFilter) {
        return loopFiles(path, -1, fileFilter);
    }

    /**
     * 递归遍历目录以及子目录中的所有文件<br>
     * 如果提供path为文件，直接返回过滤结果
     *
     * @param path       当前遍历文件或目录
     * @param maxDepth   遍历最大深度，-1表示遍历到没有目录为止
     * @param fileFilter 文件过滤规则对象，选择要保留的文件，只对文件有效，不过滤目录，null表示接收全部文件
     * @return 文件列表
     * @since 5.4.1
     */
    public static List<File> loopFiles(Path path, int maxDepth, FileFilter fileFilter) {
        final List<File> fileList = new ArrayList<>();

        if (null == path || !Files.exists(path)) {
            return fileList;
        } else if (!isDirectory(path)) {
            final File file = path.toFile();
            if (null == fileFilter || fileFilter.accept(file)) {
                fileList.add(file);
            }
            return fileList;
        }

        walkFiles(path, maxDepth, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                final File file = path.toFile();
                if (null == fileFilter || fileFilter.accept(file)) {
                    fileList.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return fileList;
    }

    /**
     * 遍历指定path下的文件并做处理
     *
     * @param start   起始路径，必须为目录
     * @param visitor {@link FileVisitor} 接口，用于自定义在访问文件时，访问目录前后等节点做的操作
     * @see Files#walkFileTree(Path, java.util.Set, int, FileVisitor)
     * @since 5.5.2
     */
    public static void walkFiles(Path start, FileVisitor<? super Path> visitor) {
        walkFiles(start, -1, visitor);
    }

    /**
     * 遍历指定path下的文件并做处理
     *
     * @param start    起始路径，必须为目录
     * @param maxDepth 最大遍历深度，-1表示不限制深度
     * @param visitor  {@link FileVisitor} 接口，用于自定义在访问文件时，访问目录前后等节点做的操作
     * @see Files#walkFileTree(Path, java.util.Set, int, FileVisitor)
     * @since 4.6.3
     */
    public static void walkFiles(Path start, int maxDepth, FileVisitor<? super Path> visitor) {
        if (maxDepth < 0) {
            // < 0 表示遍历到最底层
            maxDepth = Integer.MAX_VALUE;
        }

        try {
            Files.walkFileTree(start, EnumSet.noneOf(FileVisitOption.class), maxDepth, visitor);
        } catch (IOException e) {
            throw new TaraException(e);
        }
    }

    /**
     * 删除文件或者文件夹，不追踪软链<br>
     * 注意：删除文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param path 文件对象
     * @return 成功与否
     * @throws TaraException IO异常
     * @since 4.4.2
     */
    public static boolean del(Path path) throws TaraException {
        if (Files.notExists(path)) {
            return true;
        }

        try {
            if (isDirectory(path)) {
                Files.walkFileTree(path, DelVisitor.INSTANCE);
            } else {
                delFile(path);
            }
        } catch (IOException e) {
            throw new TaraException(e);
        }
        return true;
    }

    /**
     * 通过JDK7+的 {@link Files#copy(Path, Path, CopyOption...)} 方法拷贝文件<br>
     * 此方法不支持递归拷贝目录，如果src传入是目录，只会在目标目录中创建空目录
     *
     * @param src     源文件路径，如果为目录只在目标中创建新目录
     * @param dest    目标文件或目录，如果为目录使用与源文件相同的文件名
     * @param options {@link StandardCopyOption}
     * @return Path
     * @throws TaraException IO异常
     */
    public static Path copyFile(Path src, Path dest, StandardCopyOption... options) throws TaraException {
        return copyFile(src, dest, (CopyOption[]) options);
    }

    /**
     * 通过JDK7+的 {@link Files#copy(Path, Path, CopyOption...)} 方法拷贝文件<br>
     * 此方法不支持递归拷贝目录，如果src传入是目录，只会在目标目录中创建空目录
     *
     * @param src     源文件路径，如果为目录只在目标中创建新目录
     * @param target  目标文件或目录，如果为目录使用与源文件相同的文件名
     * @param options {@link StandardCopyOption}
     * @return Path
     * @throws TaraException IO异常
     * @since 5.4.1
     */
    public static Path copyFile(Path src, Path target, CopyOption... options) throws TaraException {
        Assert.notNull(src, "Source File is null !");
        Assert.notNull(target, "Destination File or directory is null !");

        final Path targetPath = isDirectory(target) ? target.resolve(src.getFileName()) : target;
        // 创建级联父目录
        mkParentDirs(targetPath);
        try {
            return Files.copy(src, targetPath, options);
        } catch (IOException e) {
            throw new TaraException(e);
        }
    }

    /**
     * 拷贝文件或目录，拷贝规则为：
     *
     * <ul>
     * <li>源文件为目录，目标也为目录或不存在，则拷贝整个目录到目标目录下</li>
     * <li>源文件为文件，目标为目录或不存在，则拷贝文件到目标目录下</li>
     * <li>源文件为文件，目标也为文件，则在{@link StandardCopyOption#REPLACE_EXISTING}情况下覆盖之</li>
     * </ul>
     *
     * @param src     源文件路径，如果为目录会在目标中创建新目录
     * @param target  目标文件或目录，如果为目录使用与源文件相同的文件名
     * @param options {@link StandardCopyOption}
     * @return Path
     * @throws TaraException IO异常
     * @since 5.5.1
     */
    public static Path copy(Path src, Path target, CopyOption... options) throws TaraException {
        Assert.notNull(src, "Src path must be not null !");
        Assert.notNull(target, "Target path must be not null !");

        if (isDirectory(src)) {
            return copyContent(src, target.resolve(src.getFileName()), options);
        }
        return copyFile(src, target, options);
    }

    /**
     * 拷贝目录下的所有文件或目录到目标目录中，此方法不支持文件对文件的拷贝。
     * <ul>
     * <li>源文件为目录，目标也为目录或不存在，则拷贝目录下所有文件和目录到目标目录下</li>
     * <li>源文件为文件，目标为目录或不存在，则拷贝文件到目标目录下</li>
     * </ul>
     *
     * @param src     源文件路径，如果为目录只在目标中创建新目录
     * @param target  目标目录，如果为目录使用与源文件相同的文件名
     * @param options {@link StandardCopyOption}
     * @return Path
     * @throws TaraException IO异常
     * @since 5.5.1
     */
    public static Path copyContent(Path src, Path target, CopyOption... options) throws TaraException {
        Assert.notNull(src, "Src path must be not null !");
        Assert.notNull(target, "Target path must be not null !");

        try {
            Files.walkFileTree(src, new CopyVisitor(src, target, options));
        } catch (IOException e) {
            throw new TaraException(e);
        }
        return target;
    }

    /**
     * 判断是否为目录，如果file为null，则返回false<br>
     * 此方法不会追踪到软链对应的真实地址，即软链被当作文件
     *
     * @param path {@link Path}
     * @return 如果为目录true
     * @since 5.5.1
     */
    public static boolean isDirectory(Path path) {
        return isDirectory(path, false);
    }

    /**
     * 判断是否为目录，如果file为null，则返回false
     *
     * @param path          {@link Path}
     * @param isFollowLinks 是否追踪到软链对应的真实地址
     * @return 如果为目录true
     * @since 3.1.0
     */
    public static boolean isDirectory(Path path, boolean isFollowLinks) {
        if (null == path) {
            return false;
        }
        final LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
        return Files.isDirectory(path, options);
    }

    /**
     * 获取指定位置的子路径部分，支持负数，例如index为-1表示从后数第一个节点位置
     *
     * @param path  路径
     * @param index 路径节点位置，支持负数（负数从后向前计数）
     * @return 获取的子路径
     * @since 3.1.2
     */
    public static Path getPathEle(Path path, int index) {
        return subPath(path, index, index == -1 ? path.getNameCount() : index + 1);
    }

    /**
     * 获取指定位置的最后一个子路径部分
     *
     * @param path 路径
     * @return 获取的最后一个子路径
     * @since 3.1.2
     */
    public static Path getLastPathEle(Path path) {
        return getPathEle(path, path.getNameCount() - 1);
    }

    /**
     * 获取指定位置的子路径部分，支持负数，例如起始为-1表示从后数第一个节点位置
     *
     * @param path      路径
     * @param fromIndex 起始路径节点（包括）
     * @param toIndex   结束路径节点（不包括）
     * @return 获取的子路径
     * @since 3.1.2
     */
    public static Path subPath(Path path, int fromIndex, int toIndex) {
        if (null == path) {
            return null;
        }
        final int len = path.getNameCount();

        if (fromIndex < 0) {
            fromIndex = len + fromIndex;
            if (fromIndex < 0) {
                fromIndex = 0;
            }
        } else if (fromIndex > len) {
            fromIndex = len;
        }

        if (toIndex < 0) {
            toIndex = len + toIndex;
            if (toIndex < 0) {
                toIndex = len;
            }
        } else if (toIndex > len) {
            toIndex = len;
        }

        if (toIndex < fromIndex) {
            int tmp = fromIndex;
            fromIndex = toIndex;
            toIndex = tmp;
        }

        if (fromIndex == toIndex) {
            return null;
        }
        return path.subpath(fromIndex, toIndex);
    }

    /**
     * 获取文件属性
     *
     * @param path          文件路径{@link Path}
     * @param isFollowLinks 是否跟踪到软链对应的真实路径
     * @return {@link BasicFileAttributes}
     * @throws TaraException IO异常
     */
    public static BasicFileAttributes getAttributes(Path path, boolean isFollowLinks) throws TaraException {
        if (null == path) {
            return null;
        }

        final LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
        try {
            return Files.readAttributes(path, BasicFileAttributes.class, options);
        } catch (IOException e) {
            throw new TaraException(e);
        }
    }

    /**
     * 读取文件的所有内容为byte数组
     *
     * @param path 文件
     * @return byte数组
     * @since 5.5.4
     */
    public static byte[] readBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new TaraException(e);
        }
    }

    /**
     * 修改文件或目录的文件名，不变更路径，只是简单修改文件名<br>
     *
     * <pre>
     * ArrayUtils.rename(file, "aaa.jpg", false) xx/xx.png =》xx/aaa.jpg
     * </pre>
     *
     * @param path       被修改的文件
     * @param newName    新的文件名，包括扩展名
     * @param isOverride 是否覆盖目标文件
     * @return 目标文件Path
     * @since 5.4.1
     */
    public static Path rename(Path path, String newName, boolean isOverride) {
        return move(path, path.resolveSibling(newName), isOverride);
    }

    /**
     * 移动文件或目录<br>
     * 当目标是目录时，会将源文件或文件夹整体移动至目标目录下<br>
     * 例如：
     * <ul>
     * <li>move("/usr/aaa/abc.txt", "/usr/bbb")结果为："/usr/bbb/abc.txt"</li>
     * <li>move("/usr/aaa", "/usr/bbb")结果为："/usr/bbb/aaa"</li>
     * </ul>
     *
     * @param src        源文件或目录路径
     * @param target     目标路径，如果为目录，则移动到此目录下
     * @param isOverride 是否覆盖目标文件
     * @return 目标文件Path
     * @since 5.5.1
     */
    public static Path move(Path src, Path target, boolean isOverride) {
        Assert.notNull(src, "Src path must be not null !");
        Assert.notNull(target, "Target path must be not null !");

        if (isDirectory(target)) {
            target = target.resolve(src.getFileName());
        }
        return moveContent(src, target, isOverride);
    }

    /**
     * 移动文件或目录内容到目标目录中，例如：
     * <ul>
     * <li>moveContent("/usr/aaa/abc.txt", "/usr/bbb")结果为："/usr/bbb/abc.txt"</li>
     * <li>moveContent("/usr/aaa", "/usr/bbb")结果为："/usr/bbb"</li>
     * </ul>
     *
     * @param src        源文件或目录路径
     * @param target     目标路径，如果为目录，则移动到此目录下
     * @param isOverride 是否覆盖目标文件
     * @return 目标文件Path
     * @since 5.7.9
     */
    public static Path moveContent(Path src, Path target, boolean isOverride) {
        Assert.notNull(src, "Src path must be not null !");
        Assert.notNull(target, "Target path must be not null !");
        final CopyOption[] options = isOverride ? new CopyOption[]{StandardCopyOption.REPLACE_EXISTING} : new CopyOption[]{};

        // 自动创建目标的父目录
        mkParentDirs(target);
        try {
            return Files.move(src, target, options);
        } catch (IOException e) {
            if (e instanceof FileAlreadyExistsException) {
                // 目标文件已存在，直接抛出异常
                // issue#I4QV0L@Gitee
                throw new TaraException(e);
            }
            // 移动失败，可能是跨分区移动导致的，采用递归移动方式
            try {
                Files.walkFileTree(src, new MoveVisitor(src, target, options));
                // 移动后空目录没有删除，
                del(src);
            } catch (IOException e2) {
                throw new TaraException(e2);
            }
            return target;
        }
    }

    /**
     * 检查两个文件是否是同一个文件<br>
     * 所谓文件相同，是指Path对象是否指向同一个文件或文件夹
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 是否相同
     * @throws TaraException IO异常
     * @see Files#isSameFile(Path, Path)
     * @since 5.4.1
     */
    public static boolean equals(Path file1, Path file2) throws TaraException {
        try {
            return Files.isSameFile(file1, file2);
        } catch (IOException e) {
            throw new TaraException(e);
        }
    }

    /**
     * 判断是否为文件，如果file为null，则返回false
     *
     * @param path          文件
     * @param isFollowLinks 是否跟踪软链（快捷方式）
     * @return 如果为文件true
     * @see Files#isRegularFile(Path, LinkOption...)
     */
    public static boolean isFile(Path path, boolean isFollowLinks) {
        if (null == path) {
            return false;
        }
        final LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
        return Files.isRegularFile(path, options);
    }

    /**
     * 判断是否为符号链接文件
     *
     * @param path 被检查的文件
     * @return 是否为符号链接文件
     * @since 4.4.2
     */
    public static boolean isSymlink(Path path) {
        return Files.isSymbolicLink(path);
    }

    /**
     * 判断文件或目录是否存在
     *
     * @param path          文件
     * @param isFollowLinks 是否跟踪软链（快捷方式）
     * @return 是否存在
     * @since 5.5.3
     */
    public static boolean exists(Path path, boolean isFollowLinks) {
        final LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
        return Files.exists(path, options);
    }

    /**
     * 判断给定的目录是否为给定文件或文件夹的子目录
     *
     * @param parent 父目录
     * @param sub    子目录
     * @return 子目录是否为父目录的子目录
     * @since 5.5.5
     */
    public static boolean isSub(Path parent, Path sub) {
        return toAbsNormal(sub).startsWith(toAbsNormal(parent));
    }

    /**
     * 将Path路径转换为标准的绝对路径
     *
     * @param path 文件或目录Path
     * @return 转换后的Path
     * @since 5.5.5
     */
    public static Path toAbsNormal(Path path) {
        Assert.notNull(path);
        return path.toAbsolutePath().normalize();
    }

    /**
     * 获得文件的MimeType
     *
     * @param file 文件
     * @return MimeType
     * @see Files#probeContentType(Path)
     * @since 5.5.5
     */
    public static String getMimeType(Path file) {
        try {
            return Files.probeContentType(file);
        } catch (IOException e) {
            throw new TaraException(e);
        }
    }

    /**
     * 创建所给目录及其父目录
     *
     * @param dir 目录
     * @return 目录
     * @since 5.5.7
     */
    public static Path mkdir(Path dir) {
        if (null != dir && !exists(dir, false)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new TaraException(e);
            }
        }
        return dir;
    }

    /**
     * 创建所给文件或目录的父目录
     *
     * @param path 文件或目录
     * @return 父目录
     * @since 5.5.7
     */
    public static Path mkParentDirs(Path path) {
        return mkdir(path.getParent());
    }

    /**
     * 获取{@link Path}文件名
     *
     * @param path {@link Path}
     * @return 文件名
     * @since 5.7.15
     */
    public static String getName(Path path) {
        if (null == path) {
            return null;
        }
        return path.getFileName().toString();
    }

    /**
     * 删除文件或空目录，不追踪软链
     *
     * @param path 文件对象
     * @throws IOException IO异常
     * @since 5.7.7
     */
    protected static void delFile(Path path) throws IOException {
        try {
            Files.delete(path);
        } catch (AccessDeniedException e) {
            // 可能遇到只读文件，无法删除.使用 file 方法删除
            if (!path.toFile().delete()) {
                throw e;
            }
        }
    }

    public void createFile(File file) {
        try {
            org.apache.commons.io.FileUtils.createParentDirectories(file);
            if (!file.exists()) {
                file.createNewFile();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Runtime.getRuntime().exec("chmod 777 " + file.getAbsolutePath());
        } catch (IOException ignored) {

        }
    }
}
