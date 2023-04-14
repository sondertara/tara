package com.sondertara.common.util;

import com.sondertara.common.io.CharacterReader;
import com.sondertara.common.lang.Assert;
import com.sondertara.common.lang.Pair;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 字符串工具类
 *
 * @author huangxiaohu
 */
public class StringUtils {

    public static final int INDEX_NOT_FOUND = -1;

    public static final char C_SLASH = CharUtils.SLASH;
    public static final char C_CR = CharUtils.CR;
    public static final char C_LF = CharUtils.LF;
    public static final String DOT = ".";
    public static final String DOUBLE_DOT = "..";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String SPACE = " ";

    public static final String EMPTY = "";
    public static final String SLASH = "/";
    public static final String NULL = "null";
    public static final String COLON = ":";

    /**
     * 字符串是否为空白 空白的定义如下： <br>
     * 1、为null <br>
     * 2、为不可见字符（如空格）<br>
     * 3、""<br>
     *
     * @param str 被检测的字符串
     * @return 是否为空
     */
    public static boolean isBlank(CharSequence str) {
        int length;

        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            // 只要有一个非空字符即为非空字符串
            if (!CharUtils.isBlankChar(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 如果对象是字符串是否为空白，空白的定义如下： <br>
     * 1、为null <br>
     * 2、为不可见字符（如空格）<br>
     * 3、""<br>
     *
     * @param obj 对象
     * @return 如果为字符串是否为空串
     * @since 3.3.0
     */
    public static boolean isBlankIfStr(Object obj) {
        if (null == obj) {
            return true;
        } else if (obj instanceof CharSequence) {
            return isBlank((CharSequence) obj);
        }
        return false;
    }

    /**
     * 字符串是否为非空白 空白的定义如下： <br>
     * 1、不为null <br>
     * 2、不为不可见字符（如空格）<br>
     * 3、不为""<br>
     *
     * @param str 被检测的字符串
     * @return 是否为非空
     */
    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }

    /**
     * 是否包含空字符串
     *
     * @param strs 字符串列表
     * @return 是否包含空字符串
     */
    public static boolean hasBlank(CharSequence... strs) {
        if (ArrayUtils.isEmpty(strs)) {
            return true;
        }

        for (CharSequence str : strs) {
            if (isBlank(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 给定所有字符串是否为空白
     *
     * @param strs 字符串
     * @return 所有字符串是否为空白
     */
    public static boolean isAllBlank(CharSequence... strs) {
        if (ArrayUtils.isEmpty(strs)) {
            return true;
        }

        for (CharSequence str : strs) {
            if (isNotBlank(str)) {
                return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------------------------
    // Empty

    /**
     * 字符串是否为空，空的定义如下:<br>
     * 1、为null <br>
     * 2、为""<br>
     *
     * @param str 被检测的字符串
     * @return 是否为空
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 如果对象是字符串是否为空串空的定义如下:<br>
     * 1、为null <br>
     * 2、为""<br>
     *
     * @param obj 对象
     * @return 如果为字符串是否为空串
     * @since 3.3.0
     */
    public static boolean isEmptyIfStr(Object obj) {
        if (null == obj) {
            return true;
        } else if (obj instanceof CharSequence) {
            return 0 == ((CharSequence) obj).length();
        }
        return false;
    }

    /**
     * 字符串是否为非空白 空白的定义如下： <br>
     * 1、不为null <br>
     * 2、不为""<br>
     *
     * @param str 被检测的字符串
     * @return 是否为非空
     */
    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    /**
     * 当给定字符串为null时，转换为Empty
     *
     * @param str 被转换的字符串
     * @return 转换后的字符串
     */
    public static String nullToEmpty(CharSequence str) {
        return nullToDefault(str, EMPTY);
    }

    /**
     * 如果字符串是<code>null</code>，则返回指定默认字符串，否则返回字符串本身。
     *
     * <pre>
     * nullToDefault(null, &quot;default&quot;)  = &quot;default&quot;
     * nullToDefault(&quot;&quot;, &quot;default&quot;)    = &quot;&quot;
     * nullToDefault(&quot;  &quot;, &quot;default&quot;)  = &quot;  &quot;
     * nullToDefault(&quot;bat&quot;, &quot;default&quot;) = &quot;bat&quot;
     * </pre>
     *
     * @param str        要转换的字符串
     * @param defaultStr 默认字符串
     * @return 字符串本身或指定的默认字符串
     */
    public static String nullToDefault(CharSequence str, String defaultStr) {
        return (str == null) ? defaultStr : str.toString();
    }

    /**
     * 如果字符串是<code>null</code>或者&quot;&quot;，则返回指定默认字符串，否则返回字符串本身。
     *
     * <pre>
     * emptyToDefault(null, &quot;default&quot;)  = &quot;default&quot;
     * emptyToDefault(&quot;&quot;, &quot;default&quot;)    = &quot;default&quot;
     * emptyToDefault(&quot;  &quot;, &quot;default&quot;)  = &quot;  &quot;
     * emptyToDefault(&quot;bat&quot;, &quot;default&quot;) = &quot;bat&quot;
     * </pre>
     *
     * @param str        要转换的字符串
     * @param defaultStr 默认字符串
     * @return 字符串本身或指定的默认字符串
     * @since 4.1.0
     */
    public static String emptyToDefault(CharSequence str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str.toString();
    }

    /**
     * 如果字符串是<code>null</code>或者&quot;&quot;或者空白，则返回指定默认字符串，否则返回字符串本身。
     *
     * <pre>
     * emptyToDefault(null, &quot;default&quot;)  = &quot;default&quot;
     * emptyToDefault(&quot;&quot;, &quot;default&quot;)    = &quot;default&quot;
     * emptyToDefault(&quot;  &quot;, &quot;default&quot;)  = &quot;default&quot;
     * emptyToDefault(&quot;bat&quot;, &quot;default&quot;) = &quot;bat&quot;
     * </pre>
     *
     * @param str        要转换的字符串
     * @param defaultStr 默认字符串
     * @return 字符串本身或指定的默认字符串
     * @since 4.1.0
     */
    public static String blankToDefault(CharSequence str, String defaultStr) {
        return isBlank(str) ? defaultStr : str.toString();
    }

    /**
     * 当给定字符串为空字符串时，转换为<code>null</code>
     *
     * @param str 被转换的字符串
     * @return 转换后的字符串
     */
    public static String emptyToNull(CharSequence str) {
        return isEmpty(str) ? null : str.toString();
    }

    /**
     * 是否包含空字符串
     *
     * @param strs 字符串列表
     * @return 是否包含空字符串
     */
    public static boolean hasEmpty(CharSequence... strs) {
        if (ArrayUtils.isEmpty(strs)) {
            return true;
        }

        for (CharSequence str : strs) {
            if (isEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否全部为空字符串
     *
     * @param strs 字符串列表
     * @return 是否全部为空字符串
     */
    public static boolean isAllEmpty(CharSequence... strs) {
        if (ArrayUtils.isEmpty(strs)) {
            return true;
        }

        for (CharSequence str : strs) {
            if (isNotEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查字符串是否为null、“null”、“undefined”
     *
     * @param str 被检查的字符串
     * @return 是否为null、“null”、“undefined”
     * @since 4.0.10
     */
    public static boolean isNullOrUndefined(CharSequence str) {
        if (null == str) {
            return true;
        }
        return isNullOrUndefinedStr(str);
    }

    /**
     * 检查字符串是否为null、“”、“null”、“undefined”
     *
     * @param str 被检查的字符串
     * @return 是否为null、“”、“null”、“undefined”
     * @since 4.0.10
     */
    public static boolean isEmptyOrUndefined(CharSequence str) {
        if (isEmpty(str)) {
            return true;
        }
        return isNullOrUndefinedStr(str);
    }

    /**
     * 检查字符串是否为null、空白串、“null”、“undefined”
     *
     * @param str 被检查的字符串
     * @return 是否为null、空白串、“null”、“undefined”
     * @since 4.0.10
     */
    public static boolean isBlankOrUndefined(CharSequence str) {
        if (isBlank(str)) {
            return true;
        }
        return isNullOrUndefinedStr(str);
    }

    /**
     * 是否为“null”、“undefined”，不做空指针检查
     *
     * @param str 字符串
     * @return 是否为“null”、“undefined”
     */
    private static boolean isNullOrUndefinedStr(CharSequence str) {
        String strString = str.toString().trim();
        return "null".equals(strString) || "undefined".equals(strString);
    }

    // ------------------------------------------------------------------------ Trim

    /**
     * 除去字符串头尾部的空白，如果字符串是<code>null</code>，依然返回<code>null</code>。
     *
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>NumberUtil.isBlankChar</code>
     * 来判定空白， 因而可以除去英文字符集之外的其它空白，如中文空格。
     *
     * <pre>
     * trim(null)          = null
     * trim(&quot;&quot;)            = &quot;&quot;
     * trim(&quot;     &quot;)       = &quot;&quot;
     * trim(&quot;abc&quot;)         = &quot;abc&quot;
     * trim(&quot;    abc    &quot;) = &quot;abc&quot;
     * </pre>
     *
     * @param str 要处理的字符串
     * @return 除去头尾空白的字符串，如果原字串为<code>null</code>，则返回<code>null</code>
     */
    public static String trim(CharSequence str) {
        return (null == str) ? null : trim(str, 0);
    }

    /**
     * 给定字符串数组全部做去首尾空格
     *
     * @param strs 字符串数组
     */
    public static void trim(String[] strs) {
        if (null == strs) {
            return;
        }
        String str;
        for (int i = 0; i < strs.length; i++) {
            str = strs[i];
            if (null != str) {
                strs[i] = str.trim();
            }
        }
    }

    /**
     * 除去字符串头尾部的空白，如果字符串是{@code null}，返回<code>""</code>。
     *
     * <pre>
     * StringUtils.trimToEmpty(null)          = ""
     * StringUtils.trimToEmpty("")            = ""
     * StringUtils.trimToEmpty("     ")       = ""
     * StringUtils.trimToEmpty("abc")         = "abc"
     * StringUtils.trimToEmpty("    abc    ") = "abc"
     * </pre>
     *
     * @param str 字符串
     * @return 去除两边空白符后的字符串, 如果为null返回""
     * @since 3.1.1
     */
    public static String trimToEmpty(CharSequence str) {
        return str == null ? EMPTY : trim(str);
    }

    /**
     * 除去字符串头尾部的空白，如果字符串是{@code null}，返回<code>""</code>。
     *
     * <pre>
     * StringUtils.trimToNull(null)          = null
     * StringUtils.trimToNull("")            = null
     * StringUtils.trimToNull("     ")       = null
     * StringUtils.trimToNull("abc")         = "abc"
     * StringUtils.trimToEmpty("    abc    ") = "abc"
     * </pre>
     *
     * @param str 字符串
     * @return 去除两边空白符后的字符串, 如果为空返回null
     * @since 3.2.1
     */
    public static String trimToNull(CharSequence str) {
        final String trimStr = trim(str);
        return EMPTY.equals(trimStr) ? null : trimStr;
    }

    /**
     * 除去字符串头部的空白，如果字符串是<code>null</code>，则返回<code>null</code>。
     *
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>CharUtils.isBlankChar</code> 来判定空白，
     * 因而可以除去英文字符集之外的其它空白，如中文空格。
     *
     * <pre>
     * trimStart(null)         = null
     * trimStart(&quot;&quot;)           = &quot;&quot;
     * trimStart(&quot;abc&quot;)        = &quot;abc&quot;
     * trimStart(&quot;  abc&quot;)      = &quot;abc&quot;
     * trimStart(&quot;abc  &quot;)      = &quot;abc  &quot;
     * trimStart(&quot; abc &quot;)      = &quot;abc &quot;
     * </pre>
     *
     * @param str 要处理的字符串
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回
     * <code>null</code>
     */
    public static String trimStart(CharSequence str) {
        return trim(str, -1);
    }

    /**
     * 除去字符串尾部的空白，如果字符串是<code>null</code>，则返回<code>null</code>。
     *
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>CharUtils.isBlankChar</code> 来判定空白，
     * 因而可以除去英文字符集之外的其它空白，如中文空格。
     *
     * <pre>
     * trimEnd(null)       = null
     * trimEnd(&quot;&quot;)         = &quot;&quot;
     * trimEnd(&quot;abc&quot;)      = &quot;abc&quot;
     * trimEnd(&quot;  abc&quot;)    = &quot;  abc&quot;
     * trimEnd(&quot;abc  &quot;)    = &quot;abc&quot;
     * trimEnd(&quot; abc &quot;)    = &quot; abc&quot;
     * </pre>
     *
     * @param str 要处理的字符串
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回
     * <code>null</code>
     */
    public static String trimEnd(CharSequence str) {
        return trim(str, 1);
    }

    /**
     * 除去字符串头尾部的空白符，如果字符串是<code>null</code>，依然返回<code>null</code>。
     *
     * @param str  要处理的字符串
     * @param mode <code>-1</code>表示trimStart，<code>0</code>表示trim全部，
     *             <code>1</code>表示trimEnd
     * @return 除去指定字符后的的字符串，如果原字串为<code>null</code>，则返回<code>null</code>
     */
    public static String trim(CharSequence str, int mode) {
        if (str == null) {
            return null;
        }

        int length = str.length();
        int start = 0;
        int end = length;

        // 扫描字符串头部
        if (mode <= 0) {
            while ((start < end) && (CharUtils.isBlankChar(str.charAt(start)))) {
                start++;
            }
        }

        // 扫描字符串尾部
        if (mode >= 0) {
            while ((start < end) && (CharUtils.isBlankChar(str.charAt(end - 1)))) {
                end--;
            }
        }

        if ((start > 0) || (end < length)) {
            return str.toString().substring(start, end);
        }

        return str.toString();
    }

    /**
     * 字符串是否以给定字符开始
     *
     * @param str 字符串
     * @param c   字符
     * @return 是否开始
     */
    public static boolean startWith(CharSequence str, char c) {
        return c == str.charAt(0);
    }

    /**
     * 是否以指定字符串开头<br>
     * 如果给定的字符串和开头字符串都为null则返回true，否则任意一个值为null返回false
     *
     * @param str          被监测字符串
     * @param prefix       开头字符串
     * @param isIgnoreCase 是否忽略大小写
     * @return 是否以指定字符串开头
     */
    public static boolean startWith(CharSequence str, CharSequence prefix, boolean isIgnoreCase) {
        if (null == str || null == prefix) {
            return null == str && null == prefix;
        }

        if (isIgnoreCase) {
            return str.toString().toLowerCase().startsWith(prefix.toString().toLowerCase());
        } else {
            return str.toString().startsWith(prefix.toString());
        }
    }

    /**
     * 是否以指定字符串开头
     *
     * @param str    被监测字符串
     * @param prefix 开头字符串
     * @return 是否以指定字符串开头
     */
    public static boolean startWith(CharSequence str, CharSequence prefix) {
        return startWith(str, prefix, false);
    }

    /**
     * 是否以指定字符串开头，忽略大小写
     *
     * @param str    被监测字符串
     * @param prefix 开头字符串
     * @return 是否以指定字符串开头
     */
    public static boolean startWithIgnoreCase(CharSequence str, CharSequence prefix) {
        return startWith(str, prefix, true);
    }

    /**
     * 给定字符串是否以任何一个字符串开始<br>
     * 给定字符串和数组为空都返回false
     *
     * @param str      给定字符串
     * @param prefixes 需要检测的开始字符串
     * @return 给定字符串是否以任何一个字符串开始
     * @since 3.0.6
     */
    public static boolean startWithAny(CharSequence str, CharSequence... prefixes) {
        if (isEmpty(str) || ArrayUtils.isEmpty(prefixes)) {
            return false;
        }

        for (CharSequence suffix : prefixes) {
            if (startWith(str, suffix, false)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 字符串是否以给定字符结尾
     *
     * @param str 字符串
     * @param c   字符
     * @return 是否结尾
     */
    public static boolean endWith(CharSequence str, char c) {
        return c == str.charAt(str.length() - 1);
    }

    /**
     * 是否以指定字符串结尾<br>
     * 如果给定的字符串和开头字符串都为null则返回true，否则任意一个值为null返回false
     *
     * @param str          被监测字符串
     * @param suffix       结尾字符串
     * @param isIgnoreCase 是否忽略大小写
     * @return 是否以指定字符串结尾
     */
    public static boolean endWith(CharSequence str, CharSequence suffix, boolean isIgnoreCase) {
        if (null == str || null == suffix) {
            return null == str && null == suffix;
        }

        if (isIgnoreCase) {
            return str.toString().toLowerCase().endsWith(suffix.toString().toLowerCase());
        } else {
            return str.toString().endsWith(suffix.toString());
        }
    }

    /**
     * 是否以指定字符串结尾
     *
     * @param str    被监测字符串
     * @param suffix 结尾字符串
     * @return 是否以指定字符串结尾
     */
    public static boolean endWith(CharSequence str, CharSequence suffix) {
        return endWith(str, suffix, false);
    }

    /**
     * 是否以指定字符串结尾，忽略大小写
     *
     * @param str    被监测字符串
     * @param suffix 结尾字符串
     * @return 是否以指定字符串结尾
     */
    public static boolean endWithIgnoreCase(CharSequence str, CharSequence suffix) {
        return endWith(str, suffix, true);
    }

    /**
     * 给定字符串是否以任何一个字符串结尾<br>
     * 给定字符串和数组为空都返回false
     *
     * @param str      给定字符串
     * @param suffixes 需要检测的结尾字符串
     * @return 给定字符串是否以任何一个字符串结尾
     * @since 3.0.6
     */
    public static boolean endWithAny(CharSequence str, CharSequence... suffixes) {
        if (isEmpty(str) || ArrayUtils.isEmpty(suffixes)) {
            return false;
        }

        for (CharSequence suffix : suffixes) {
            if (endWith(str, suffix, false)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 指定字符是否在字符串中出现过
     *
     * @param str        字符串
     * @param searchChar 被查找的字符
     * @return 是否包含
     * @since 3.1.2
     */
    public static boolean contains(CharSequence str, char searchChar) {
        return indexOf(str, searchChar) > -1;
    }

    /**
     * 指定字符串是否在字符串中出现过
     *
     * @param str       字符串
     * @param searchStr 被查找的字符串
     * @return 是否包含
     * @since 5.1.1
     */
    public static boolean contains(CharSequence str, CharSequence searchStr) {
        if (null == str || null == searchStr) {
            return false;
        }
        return str.toString().contains(searchStr);
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串
     *
     * @param str      指定字符串
     * @param testStrs 需要检查的字符串数组
     * @return 是否包含任意一个字符串
     * @since 3.2.0
     */
    public static boolean containsAny(CharSequence str, CharSequence... testStrs) {
        return null != getContainsStr(str, testStrs);
    }

    /**
     * 查找指定字符串是否包含指定字符列表中的任意一个字符
     *
     * @param str       指定字符串
     * @param testChars 需要检查的字符数组
     * @return 是否包含任意一个字符
     * @since 4.1.11
     */
    public static boolean containsAny(CharSequence str, char... testChars) {
        if (!isEmpty(str)) {
            int len = str.length();
            for (int i = 0; i < len; i++) {
                if (ArrayUtils.contains(testChars, str.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检查指定字符串中是否只包含给定的字符
     *
     * @param str       字符串
     * @param testChars 检查的字符
     * @return 字符串含有非检查的字符，返回false
     * @since 4.4.1
     */
    public static boolean containsOnly(CharSequence str, char... testChars) {
        if (isNotEmpty(str)) {
            int len = str.length();
            for (int i = 0; i < len; i++) {
                if (!ArrayUtils.contains(testChars, str.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 给定字符串是否包含空白符（空白符包括空格、制表符、全角空格和不间断空格）<br>
     * 如果给定字符串为null或者""，则返回false
     *
     * @param str 字符串
     * @return 是否包含空白符
     * @since 4.0.8
     */
    public static boolean containsBlank(CharSequence str) {
        if (null == str) {
            return false;
        }
        final int length = str.length();
        if (0 == length) {
            return false;
        }

        for (int i = 0; i < length; i += 1) {
            if (CharUtils.isBlankChar(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串，如果包含返回找到的第一个字符串
     *
     * @param str      指定字符串
     * @param testStrs 需要检查的字符串数组
     * @return 被包含的第一个字符串
     * @since 3.2.0
     */
    public static String getContainsStr(CharSequence str, CharSequence... testStrs) {
        if (isEmpty(str) || ArrayUtils.isEmpty(testStrs)) {
            return null;
        }
        for (CharSequence checkStr : testStrs) {
            if (str.toString().contains(checkStr)) {
                return checkStr.toString();
            }
        }
        return null;
    }

    /**
     * 是否包含特定字符，忽略大小写，如果给定两个参数都为<code>null</code>，返回true
     *
     * @param str     被检测字符串
     * @param testStr 被测试是否包含的字符串
     * @return 是否包含
     */
    public static boolean containsIgnoreCase(CharSequence str, CharSequence testStr) {
        if (null == str) {
            // 如果被监测字符串和
            return null == testStr;
        }
        return str.toString().toLowerCase().contains(testStr.toString().toLowerCase());
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串<br>
     * 忽略大小写
     *
     * @param str      指定字符串
     * @param testStrs 需要检查的字符串数组
     * @return 是否包含任意一个字符串
     * @since 3.2.0
     */
    public static boolean containsAnyIgnoreCase(CharSequence str, CharSequence... testStrs) {
        return null != getContainsStrIgnoreCase(str, testStrs);
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串，如果包含返回找到的第一个字符串<br>
     * 忽略大小写
     *
     * @param str      指定字符串
     * @param testStrs 需要检查的字符串数组
     * @return 被包含的第一个字符串
     * @since 3.2.0
     */
    public static String getContainsStrIgnoreCase(CharSequence str, CharSequence... testStrs) {
        if (isEmpty(str) || ArrayUtils.isEmpty(testStrs)) {
            return null;
        }
        for (CharSequence testStr : testStrs) {
            if (containsIgnoreCase(str, testStr)) {
                return testStr.toString();
            }
        }
        return null;
    }

    /**
     * 获得set或get或is方法对应的标准属性名<br>
     * 例如：setName 返回 name
     *
     * <pre>
     * getName =》name
     * setName =》name
     * isName  =》name
     * </pre>
     *
     * @param getOrSetMethodName Get或Set方法名
     * @return 如果是set或get方法名，返回field， 否则null
     */
    public static String getGeneralField(CharSequence getOrSetMethodName) {
        final String getOrSetMethodNameStr = getOrSetMethodName.toString();
        if (getOrSetMethodNameStr.startsWith("get") || getOrSetMethodNameStr.startsWith("set")) {
            return removePreAndLowerFirst(getOrSetMethodName, 3);
        } else if (getOrSetMethodNameStr.startsWith("is")) {
            return removePreAndLowerFirst(getOrSetMethodName, 2);
        }
        return null;
    }

    /**
     * 生成set方法名<br>
     * 例如：name 返回 setName
     *
     * @param fieldName 属性名
     * @return setXxx
     */
    public static String genSetter(CharSequence fieldName) {
        return upperFirstAndAddPre(fieldName, "set");
    }

    /**
     * 生成get方法名
     *
     * @param fieldName 属性名
     * @return getXxx
     */
    public static String genGetter(CharSequence fieldName) {
        return upperFirstAndAddPre(fieldName, "get");
    }

    /**
     * 移除字符串中所有给定字符串<br>
     * 例：removeAll("aa-bb-cc-dd", "-") =》 aabbccdd
     *
     * @param str         字符串
     * @param strToRemove 被移除的字符串
     * @return 移除后的字符串
     */
    public static String removeAll(CharSequence str, CharSequence strToRemove) {
        if (isEmpty(str)) {
            return str(str);
        }
        return str.toString().replace(strToRemove, EMPTY);
    }

    /**
     * 去除字符串中指定的多个字符，如有多个则全部去除
     *
     * @param str   字符串
     * @param chars 字符列表
     * @return 去除后的字符
     * @since 4.2.2
     */
    public static String removeAll(CharSequence str, char... chars) {
        if (null == str || ArrayUtils.isEmpty(chars)) {
            return str(str);
        }
        final int len = str.length();
        if (0 == len) {
            return str(str);
        }
        final StringBuilder builder = builder(len);
        char c;
        for (int i = 0; i < len; i++) {
            c = str.charAt(i);
            if (!ArrayUtils.contains(chars, c)) {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    /**
     * 去除所有换行符，包括：
     *
     * <pre>
     * 1. \r
     * 1. \n
     * </pre>
     *
     * @param str 字符串
     * @return 处理后的字符串
     * @since 4.2.2
     */
    public static String removeAllLineBreaks(CharSequence str) {
        return removeAll(str, C_CR, C_LF);
    }

    /**
     * 去掉首部指定长度的字符串并将剩余字符串首字母小写<br>
     * 例如：str=setName, preLength=3 =》 return name
     *
     * @param str       被处理的字符串
     * @param preLength 去掉的长度
     * @return 处理后的字符串，不符合规范返回null
     */
    public static String removePreAndLowerFirst(CharSequence str, int preLength) {
        if (str == null) {
            return null;
        }
        if (str.length() > preLength) {
            char first = Character.toLowerCase(str.charAt(preLength));
            if (str.length() > preLength + 1) {
                return first + str.toString().substring(preLength + 1);
            }
            return String.valueOf(first);
        } else {
            return str.toString();
        }
    }

    /**
     * 去掉首部指定长度的字符串并将剩余字符串首字母小写<br>
     * 例如：str=setName, prefix=set =》 return name
     *
     * @param str    被处理的字符串
     * @param prefix 前缀
     * @return 处理后的字符串，不符合规范返回null
     */
    public static String removePreAndLowerFirst(CharSequence str, CharSequence prefix) {
        return lowerFirst(removePrefix(str, prefix));
    }

    /**
     * 原字符串首字母大写并在其首部添加指定字符串 例如：str=name, preString=get =》 return getName
     *
     * @param str       被处理的字符串
     * @param preString 添加的首部
     * @return 处理后的字符串
     */
    public static String upperFirstAndAddPre(CharSequence str, String preString) {
        if (str == null || preString == null) {
            return null;
        }
        return preString + upperFirst(str);
    }

    /**
     * 大写首字母<br>
     * 例如：str = name, return Name
     *
     * @param str 字符串
     * @return 字符串
     */
    public static String upperFirst(CharSequence str) {
        if (null == str) {
            return null;
        }
        if (str.length() > 0) {
            char firstChar = str.charAt(0);
            if (Character.isLowerCase(firstChar)) {
                return Character.toUpperCase(firstChar) + subSuf(str, 1);
            }
        }
        return str.toString();
    }

    /**
     * 小写首字母<br>
     * 例如：str = Name, return name
     *
     * @param str 字符串
     * @return 字符串
     */
    public static String lowerFirst(CharSequence str) {
        if (null == str) {
            return null;
        }
        if (str.length() > 0) {
            char firstChar = str.charAt(0);
            if (Character.isUpperCase(firstChar)) {
                return Character.toLowerCase(firstChar) + subSuf(str, 1);
            }
        }
        return str.toString();
    }

    /**
     * 去掉指定前缀
     *
     * @param str    字符串
     * @param prefix 前缀
     * @return 切掉后的字符串，若前缀不是 preffix， 返回原字符串
     */
    public static String removePrefix(CharSequence str, CharSequence prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return str(str);
        }

        final String str2 = str.toString();
        if (str2.startsWith(prefix.toString())) {
            return subSuf(str2, prefix.length());// 截取后半段
        }
        return str2;
    }

    /**
     * 忽略大小写去掉指定前缀
     *
     * @param str    字符串
     * @param prefix 前缀
     * @return 切掉后的字符串，若前缀不是 prefix， 返回原字符串
     */
    public static String removePrefixIgnoreCase(CharSequence str, CharSequence prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return str(str);
        }

        final String str2 = str.toString();
        if (str2.toLowerCase().startsWith(prefix.toString().toLowerCase())) {
            return subSuf(str2, prefix.length());// 截取后半段
        }
        return str2;
    }

    /**
     * 去掉指定后缀
     *
     * @param str    字符串
     * @param suffix 后缀
     * @return 切掉后的字符串，若后缀不是 suffix， 返回原字符串
     */
    public static String removeSuffix(CharSequence str, CharSequence suffix) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return str(str);
        }

        final String str2 = str.toString();
        if (str2.endsWith(suffix.toString())) {
            return subPre(str2, str2.length() - suffix.length());// 截取前半段
        }
        return str2;
    }

    /**
     * 去掉指定后缀，并小写首字母
     *
     * @param str    字符串
     * @param suffix 后缀
     * @return 切掉后的字符串，若后缀不是 suffix， 返回原字符串
     */
    public static String removeSufAndLowerFirst(CharSequence str, CharSequence suffix) {
        return lowerFirst(removeSuffix(str, suffix));
    }

    /**
     * 忽略大小写去掉指定后缀
     *
     * @param str    字符串
     * @param suffix 后缀
     * @return 切掉后的字符串，若后缀不是 suffix， 返回原字符串
     */
    public static String removeSuffixIgnoreCase(CharSequence str, CharSequence suffix) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return str(str);
        }

        final String str2 = str.toString();
        if (str2.toLowerCase().endsWith(suffix.toString().toLowerCase())) {
            return subPre(str2, str2.length() - suffix.length());
        }
        return str2;
    }

    /**
     * 去除两边的指定字符串
     *
     * @param str            被处理的字符串
     * @param prefixOrSuffix 前缀或后缀
     * @return 处理后的字符串
     * @since 3.1.2
     */
    public static String strip(CharSequence str, CharSequence prefixOrSuffix) {
        if (equals(str, prefixOrSuffix)) {
            // 对于去除相同字符的情况单独处理
            return EMPTY;
        }
        return strip(str, prefixOrSuffix, prefixOrSuffix);
    }

    /**
     * 去除两边的指定字符串
     *
     * @param str    被处理的字符串
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 处理后的字符串
     * @since 3.1.2
     */
    public static String strip(CharSequence str, CharSequence prefix, CharSequence suffix) {
        if (isEmpty(str)) {
            return str(str);
        }

        int from = 0;
        int to = str.length();

        String str2 = str.toString();
        if (startWith(str2, prefix)) {
            from = prefix.length();
        }
        if (endWith(str2, suffix)) {
            to -= suffix.length();
        }

        return str2.substring(Math.min(from, to), Math.max(from, to));
    }

    /**
     * 去除两边的指定字符串，忽略大小写
     *
     * @param str            被处理的字符串
     * @param prefixOrSuffix 前缀或后缀
     * @return 处理后的字符串
     * @since 3.1.2
     */
    public static String stripIgnoreCase(CharSequence str, CharSequence prefixOrSuffix) {
        return stripIgnoreCase(str, prefixOrSuffix, prefixOrSuffix);
    }

    /**
     * 去除两边的指定字符串，忽略大小写
     *
     * @param str    被处理的字符串
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 处理后的字符串
     * @since 3.1.2
     */
    public static String stripIgnoreCase(CharSequence str, CharSequence prefix, CharSequence suffix) {
        if (isEmpty(str)) {
            return str(str);
        }
        int from = 0;
        int to = str.length();

        String str2 = str.toString();
        if (startWithIgnoreCase(str2, prefix)) {
            from = prefix.length();
        }
        if (endWithIgnoreCase(str2, suffix)) {
            to -= suffix.length();
        }
        return str2.substring(from, to);
    }

    /**
     * 如果给定字符串不是以prefix开头的，在开头补充 prefix
     *
     * @param str    字符串
     * @param prefix 前缀
     * @return 补充后的字符串
     */
    public static String addPrefixIfNot(CharSequence str, CharSequence prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return str(str);
        }

        final String str2 = str.toString();
        final String prefix2 = prefix.toString();
        if (!str2.startsWith(prefix2)) {
            return prefix2.concat(str2);
        }
        return str2;
    }

    /**
     * 如果给定字符串不是以suffix结尾的，在尾部补充 suffix
     *
     * @param str    字符串
     * @param suffix 后缀
     * @return 补充后的字符串
     */
    public static String addSuffixIfNot(CharSequence str, CharSequence suffix) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return str(str);
        }

        final String str2 = str.toString();
        final String suffix2 = suffix.toString();
        if (!str2.endsWith(suffix2)) {
            return str2.concat(suffix2);
        }
        return str2;
    }

    /**
     * 清理空白字符
     *
     * @param str 被清理的字符串
     * @return 清理后的字符串
     */
    public static String cleanBlank(CharSequence str) {
        if (str == null) {
            return null;
        }

        int len = str.length();
        final StringBuilder sb = new StringBuilder(len);
        char c;
        for (int i = 0; i < len; i++) {
            c = str.charAt(i);
            if (!CharUtils.isBlankChar(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 改进JDK subString<br>
     * index从0开始计算，最后一个字符为-1<br>
     * 如果from和to位置一样，返回 "" <br>
     * 如果from或to为负数，则按照length从后向前数位置，如果绝对值大于字符串长度，则from归到0，to归到length<br>
     * 如果经过修正的index中from大于to，则互换from和to example: <br>
     * abcdefgh 2 3 =》 c <br>
     * abcdefgh 2 -3 =》 cde <br>
     *
     * @param str       String
     * @param fromIndex 开始的index（包括）
     * @param toIndex   结束的index（不包括）
     * @return 字串
     */
    public static String sub(CharSequence str, int fromIndex, int toIndex) {
        if (isEmpty(str)) {
            return str(str);
        }
        int len = str.length();

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
            return EMPTY;
        }

        return str.toString().substring(fromIndex, toIndex);
    }

    /**
     * 限制字符串长度，如果超过指定长度，截取指定长度并在末尾加"..."
     *
     * @param string 字符串
     * @param length 最大长度
     * @return 切割后的剩余的前半部分字符串+"..."
     * @since 4.0.10
     */
    public static String maxLength(CharSequence string, int length) {
        Assert.isTrue(length > 0, "empty charSequence");
        if (null == string) {
            return null;
        }
        if (string.length() <= length) {
            return string.toString();
        }
        return sub(string, 0, length) + "...";
    }

    /**
     * 切割指定位置之前部分的字符串
     *
     * @param string  字符串
     * @param toIndex 切割到的位置（不包括）
     * @return 切割后的剩余的前半部分字符串
     */
    public static String subPre(CharSequence string, int toIndex) {
        return sub(string, 0, toIndex);
    }

    /**
     * 切割指定位置之后部分的字符串
     *
     * @param string    字符串
     * @param fromIndex 切割开始的位置（包括）
     * @return 切割后后剩余的后半部分字符串
     */
    public static String subSuf(CharSequence string, int fromIndex) {
        if (isEmpty(string)) {
            return null;
        }
        return sub(string, fromIndex, string.length());
    }

    /**
     * 切割指定长度的后部分的字符串
     *
     * <pre>
     * StringUtils.subSufByLength("abcde", 3)      =    "cde"
     * StringUtils.subSufByLength("abcde", 0)      =    ""
     * StringUtils.subSufByLength("abcde", -5)     =    ""
     * StringUtils.subSufByLength("abcde", -1)     =    ""
     * StringUtils.subSufByLength("abcde", 5)       =    "abcde"
     * StringUtils.subSufByLength("abcde", 10)     =    "abcde"
     * StringUtils.subSufByLength(null, 3)               =    null
     * </pre>
     *
     * @param string 字符串
     * @param length 切割长度
     * @return 切割后后剩余的后半部分字符串
     * @since 4.0.1
     */
    public static String subSufByLength(CharSequence string, int length) {
        if (isEmpty(string)) {
            return null;
        }
        if (length <= 0) {
            return EMPTY;
        }
        return sub(string, -length, string.length());
    }

    /**
     * 截取字符串,从指定位置开始,截取指定长度的字符串<br>
     * author weibaohui
     *
     * @param input     原始字符串
     * @param fromIndex 开始的index,包括
     * @param length    要截取的长度
     * @return 截取后的字符串
     */
    public static String subWithLength(String input, int fromIndex, int length) {
        return sub(input, fromIndex, fromIndex + length);
    }

    /**
     * 截取分隔字符串之前的字符串，不包括分隔字符串<br>
     * 如果给定的字符串为空串（null或""）或者分隔字符串为null，返回原字符串<br>
     * 如果分隔字符串为空串""，则返回空串，如果分隔字符串未找到，返回原字符串，举例如下：
     *
     * <pre>
     * StringUtils.subBefore(null, *)      = null
     * StringUtils.subBefore("", *)        = ""
     * StringUtils.subBefore("abc", "a")   = ""
     * StringUtils.subBefore("abcba", "b") = "a"
     * StringUtils.subBefore("abc", "c")   = "ab"
     * StringUtils.subBefore("abc", "d")   = "abc"
     * StringUtils.subBefore("abc", "")    = ""
     * StringUtils.subBefore("abc", null)  = "abc"
     * </pre>
     *
     * @param string          被查找的字符串
     * @param separator       分隔字符串（不包括）
     * @param isLastSeparator 是否查找最后一个分隔字符串（多次出现分隔字符串时选取最后一个），true为选取最后一个
     * @return 切割后的字符串
     * @since 3.1.1
     */
    public static String subBefore(CharSequence string, CharSequence separator, boolean isLastSeparator) {
        if (isEmpty(string) || separator == null) {
            return null == string ? null : string.toString();
        }

        final String str = string.toString();
        final String sep = separator.toString();
        if (sep.isEmpty()) {
            return EMPTY;
        }
        final int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
        if (INDEX_NOT_FOUND == pos) {
            return str;
        }
        if (0 == pos) {
            return EMPTY;
        }
        return str.substring(0, pos);
    }

    /**
     * 截取分隔字符串之前的字符串，不包括分隔字符串<br>
     * 如果给定的字符串为空串（null或""）或者分隔字符串为null，返回原字符串<br>
     * 如果分隔字符串未找到，返回原字符串，举例如下：
     *
     * <pre>
     * StringUtils.subBefore(null, *)      = null
     * StringUtils.subBefore("", *)        = ""
     * StringUtils.subBefore("abc", 'a')   = ""
     * StringUtils.subBefore("abcba", 'b') = "a"
     * StringUtils.subBefore("abc", 'c')   = "ab"
     * StringUtils.subBefore("abc", 'd')   = "abc"
     * </pre>
     *
     * @param string          被查找的字符串
     * @param separator       分隔字符串（不包括）
     * @param isLastSeparator 是否查找最后一个分隔字符串（多次出现分隔字符串时选取最后一个），true为选取最后一个
     * @return 切割后的字符串
     * @since 4.1.15
     */
    public static String subBefore(CharSequence string, char separator, boolean isLastSeparator) {
        if (isEmpty(string)) {
            return null == string ? null : string.toString();
        }

        final String str = string.toString();
        final int pos = isLastSeparator ? str.lastIndexOf(separator) : str.indexOf(separator);
        if (INDEX_NOT_FOUND == pos) {
            return str;
        }
        if (0 == pos) {
            return EMPTY;
        }
        return str.substring(0, pos);
    }

    /**
     * 截取分隔字符串之后的字符串，不包括分隔字符串<br>
     * 如果给定的字符串为空串（null或""），返回原字符串<br>
     * 如果分隔字符串为空串（null或""），则返回空串，如果分隔字符串未找到，返回空串，举例如下：
     *
     * <pre>
     * StringUtils.subAfter(null, *)      = null
     * StringUtils.subAfter("", *)        = ""
     * StringUtils.subAfter(*, null)      = ""
     * StringUtils.subAfter("abc", "a")   = "bc"
     * StringUtils.subAfter("abcba", "b") = "cba"
     * StringUtils.subAfter("abc", "c")   = ""
     * StringUtils.subAfter("abc", "d")   = ""
     * StringUtils.subAfter("abc", "")    = "abc"
     * </pre>
     *
     * @param string          被查找的字符串
     * @param separator       分隔字符串（不包括）
     * @param isLastSeparator 是否查找最后一个分隔字符串（多次出现分隔字符串时选取最后一个），true为选取最后一个
     * @return 切割后的字符串
     * @since 3.1.1
     */
    public static String subAfter(CharSequence string, CharSequence separator, boolean isLastSeparator) {
        if (isEmpty(string)) {
            return null == string ? null : string.toString();
        }
        if (separator == null) {
            return EMPTY;
        }
        final String str = string.toString();
        final String sep = separator.toString();
        final int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
        if (INDEX_NOT_FOUND == pos || (string.length() - 1) == pos) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    /**
     * 截取分隔字符串之后的字符串，不包括分隔字符串<br>
     * 如果给定的字符串为空串（null或""），返回原字符串<br>
     * 如果分隔字符串为空串（null或""），则返回空串，如果分隔字符串未找到，返回空串，举例如下：
     *
     * <pre>
     * StringUtils.subAfter(null, *)      = null
     * StringUtils.subAfter("", *)        = ""
     * StringUtils.subAfter("abc", 'a')   = "bc"
     * StringUtils.subAfter("abcba", 'b') = "cba"
     * StringUtils.subAfter("abc", 'c')   = ""
     * StringUtils.subAfter("abc", 'd')   = ""
     * </pre>
     *
     * @param string          被查找的字符串
     * @param separator       分隔字符串（不包括）
     * @param isLastSeparator 是否查找最后一个分隔字符串（多次出现分隔字符串时选取最后一个），true为选取最后一个
     * @return 切割后的字符串
     * @since 4.1.15
     */
    public static String subAfter(CharSequence string, char separator, boolean isLastSeparator) {
        if (isEmpty(string)) {
            return null == string ? null : string.toString();
        }
        final String str = string.toString();
        final int pos = isLastSeparator ? str.lastIndexOf(separator) : str.indexOf(separator);
        if (INDEX_NOT_FOUND == pos) {
            return EMPTY;
        }
        return str.substring(pos + 1);
    }

    /**
     * 截取指定字符串中间部分，不包括标识字符串<br>
     * <p>
     * 栗子：
     *
     * <pre>
     * StringUtils.subBetween("wx[b]yz", "[", "]") = "b"
     * StringUtils.subBetween(null, *, *)          = null
     * StringUtils.subBetween(*, null, *)          = null
     * StringUtils.subBetween(*, *, null)          = null
     * StringUtils.subBetween("", "", "")          = ""
     * StringUtils.subBetween("", "", "]")         = null
     * StringUtils.subBetween("", "[", "]")        = null
     * StringUtils.subBetween("yabcz", "", "")     = ""
     * StringUtils.subBetween("yabcz", "y", "z")   = "abc"
     * StringUtils.subBetween("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     *
     * @param str    被切割的字符串
     * @param before 截取开始的字符串标识
     * @param after  截取到的字符串标识
     * @return 截取后的字符串
     * @since 3.1.1
     */
    public static String subBetween(CharSequence str, CharSequence before, CharSequence after) {
        if (str == null || before == null || after == null) {
            return null;
        }

        final String str2 = str.toString();
        final String before2 = before.toString();
        final String after2 = after.toString();

        final int start = str2.indexOf(before2);
        if (start != INDEX_NOT_FOUND) {
            final int end = str2.indexOf(after2, start + before2.length());
            if (end != INDEX_NOT_FOUND) {
                return str2.substring(start + before2.length(), end);
            }
        }
        return null;
    }

    /**
     * 截取指定字符串中间部分，不包括标识字符串<br>
     * <p>
     * 栗子：
     *
     * <pre>
     * StringUtils.subBetween(null, *)            = null
     * StringUtils.subBetween("", "")             = ""
     * StringUtils.subBetween("", "tag")          = null
     * StringUtils.subBetween("tagabctag", null)  = null
     * StringUtils.subBetween("tagabctag", "")    = ""
     * StringUtils.subBetween("tagabctag", "tag") = "abc"
     * </pre>
     *
     * @param str            被切割的字符串
     * @param beforeAndAfter 截取开始和结束的字符串标识
     * @return 截取后的字符串
     * @since 3.1.1
     */
    public static String subBetween(CharSequence str, CharSequence beforeAndAfter) {
        return subBetween(str, beforeAndAfter, beforeAndAfter);
    }

    /**
     * 给定字符串是否被字符包围
     *
     * @param str    字符串
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 是否包围，空串不包围
     */
    public static boolean isSurround(CharSequence str, CharSequence prefix, CharSequence suffix) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        if (str.length() < (prefix.length() + suffix.length())) {
            return false;
        }

        final String str2 = str.toString();
        return str2.startsWith(prefix.toString()) && str2.endsWith(suffix.toString());
    }

    /**
     * 给定字符串是否被字符包围
     *
     * @param str    字符串
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 是否包围，空串不包围
     */
    public static boolean isSurround(CharSequence str, char prefix, char suffix) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        if (str.length() < 2) {
            return false;
        }

        return str.charAt(0) == prefix && str.charAt(str.length() - 1) == suffix;
    }

    public static List<String> split(CharSequence str, char separator) {
        return split(str, separator, 0);
    }

    /**
     * 切分字符串
     *
     * @param str       被切分的字符串
     * @param separator 分隔符字符
     * @param limit     限制分片数
     * @return 切分后的数组
     */
    public static String[] splitToArray(CharSequence str, char separator, int limit) {
        if (null == str) {
            return new String[]{};
        }
        return StringSplitUtils.splitToArray(str.toString(), separator, limit, false, false);
    }
    /**
     * 切分字符串，如果分隔符不存在则返回原字符串
     *
     * @param str       被切分的字符串
     * @param separator 分隔符
     * @return 字符串
     * @since 5.6.7
     */
    public static String[] splitToArray(CharSequence str, CharSequence separator) {
        if (str == null) {
            return new String[]{};
        }

        return StringSplitUtils.splitToArray(str.toString(), str(separator), 0, false, false);
    }

    /**
     * 切分字符串
     *
     * @param str       被切分的字符串
     * @param separator 分隔符字符
     * @return 切分后的数组
     */
    public static String[] splitToArray(CharSequence str, char separator) {
        return splitToArray(str, separator, 0);
    }

    /**
     * 切分字符串，不去除切分后每个元素两边的空白符，不去除空白项
     *
     * @param str       被切分的字符串
     * @param separator 分隔符字符
     * @param limit     限制分片数，-1不限制
     * @return 切分后的集合
     */
    public static List<String> split(CharSequence str, char separator, int limit) {
        return split(str, separator, limit, false, false);
    }

    /**
     * 切分字符串，去除切分后每个元素两边的空白符，去除空白项
     *
     * @param str       被切分的字符串
     * @param separator 分隔符字符
     * @return 切分后的集合
     * @since 3.1.2
     */
    public static List<String> splitTrim(CharSequence str, char separator) {
        return splitTrim(str, separator, -1);
    }

    /**
     * 切分字符串，去除切分后每个元素两边的空白符，去除空白项
     *
     * @param str       被切分的字符串
     * @param separator 分隔符字符
     * @return 切分后的集合
     * @since 3.2.0
     */
    public static List<String> splitTrim(CharSequence str, CharSequence separator) {
        return splitTrim(str, separator, -1);
    }

    /**
     * 切分字符串，去除切分后每个元素两边的空白符，去除空白项
     *
     * @param str       被切分的字符串
     * @param separator 分隔符字符
     * @param limit     限制分片数，-1不限制
     * @return 切分后的集合
     * @since 3.1.0
     */
    public static List<String> splitTrim(CharSequence str, char separator, int limit) {
        return split(str, separator, limit, true, true);
    }

    /**
     * 切分字符串，去除切分后每个元素两边的空白符，去除空白项
     *
     * @param str       被切分的字符串
     * @param separator 分隔符字符
     * @param limit     限制分片数，-1不限制
     * @return 切分后的集合
     * @since 3.2.0
     */
    public static List<String> splitTrim(CharSequence str, CharSequence separator, int limit) {
        return split(str, separator, limit, true, true);
    }

    /**
     * 切分字符串，不限制分片数量
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static List<String> split(CharSequence str, char separator, boolean isTrim, boolean ignoreEmpty) {
        return split(str, separator, 0, isTrim, ignoreEmpty);
    }

    /**
     * 切分字符串
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符
     * @param limit       限制分片数，-1不限制
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static List<String> split(CharSequence str, char separator, int limit, boolean isTrim, boolean ignoreEmpty) {
        if (null == str) {
            return new ArrayList<>(0);
        }
        return StringSplitUtils.split(str.toString(), separator, limit, isTrim, ignoreEmpty);
    }

    /**
     * 切分字符串
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符
     * @param limit       限制分片数，-1不限制
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.2.0
     */
    public static List<String> split(CharSequence str, CharSequence separator, int limit, boolean isTrim, boolean ignoreEmpty) {
        if (null == str) {
            return new ArrayList<>(0);
        }
        final String separatorStr = (null == separator) ? null : separator.toString();
        return StringSplitUtils.split(str.toString(), separatorStr, limit, isTrim, ignoreEmpty);
    }

    /**
     * 切分字符串
     *
     * @param str       被切分的字符串
     * @param separator 分隔符
     * @return 字符串
     */
    public static String[] split(CharSequence str, CharSequence separator) {
        if (str == null) {
            return new String[]{};
        }

        final String separatorStr = (null == separator) ? null : separator.toString();
        return StringSplitUtils.splitToArray(str.toString(), separatorStr, 0, false, false);
    }

    /**
     * 根据给定长度，将给定字符串截取为多个部分
     *
     * @param str 字符串
     * @param len 每一个小节的长度
     * @return 截取后的字符串数组
     * @see StringSplitUtils#splitByLength(String, int)
     */
    public static String[] split(CharSequence str, int len) {
        if (null == str) {
            return new String[]{};
        }
        return StringSplitUtils.splitByLength(str.toString(), len);
    }

    /**
     * 重复某个字符
     *
     * @param c     被重复的字符
     * @param count 重复的数目，如果小于等于0则返回""
     * @return 重复字符字符串
     */
    public static String repeat(char c, int count) {
        if (count <= 0) {
            return EMPTY;
        }

        char[] result = new char[count];
        for (int i = 0; i < count; i++) {
            result[i] = c;
        }
        return new String(result);
    }

    /**
     * 重复某个字符串
     *
     * @param str   被重复的字符
     * @param count 重复的数目
     * @return 重复字符字符串
     */
    public static String repeat(CharSequence str, int count) {
        if (null == str) {
            return null;
        }
        if (count <= 0) {
            return EMPTY;
        }
        if (count == 1 || str.length() == 0) {
            return str.toString();
        }

        // 检查
        final int len = str.length();
        final long longSize = (long) len * (long) count;
        final int size = (int) longSize;
        if (size != longSize) {
            throw new ArrayIndexOutOfBoundsException("Required String length is too large: " + longSize);
        }

        final char[] array = new char[size];
        str.toString().getChars(0, len, array, 0);
        int n;
        for (n = len; n < size - n; n <<= 1) {// n <<= 1相当于n *2
            System.arraycopy(array, 0, array, n, n);
        }
        System.arraycopy(array, 0, array, n, size - n);
        return new String(array);
    }

    /**
     * 重复某个字符串到指定长度
     *
     * @param str    被重复的字符
     * @param padLen 指定长度
     * @return 重复字符字符串
     * @since 4.3.2
     */
    public static String repeatByLength(CharSequence str, int padLen) {
        if (null == str) {
            return null;
        }
        if (padLen <= 0) {
            return StringUtils.EMPTY;
        }
        final int strLen = str.length();
        if (strLen == padLen) {
            return str.toString();
        } else if (strLen > padLen) {
            return subPre(str, padLen);
        }

        // 重复，直到达到指定长度
        final char[] padding = new char[padLen];
        for (int i = 0; i < padLen; i++) {
            padding[i] = str.charAt(i % strLen);
        }
        return new String(padding);
    }

    /**
     * 比较两个字符串（大小写敏感）。
     *
     * <pre>
     * equals(null, null)   = true
     * equals(null, &quot;abc&quot;)  = false
     * equals(&quot;abc&quot;, null)  = false
     * equals(&quot;abc&quot;, &quot;abc&quot;) = true
     * equals(&quot;abc&quot;, &quot;ABC&quot;) = false
     * </pre>
     *
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     * @return 如果两个字符串相同，或者都是<code>null</code>，则返回<code>true</code>
     */
    public static boolean equals(CharSequence str1, CharSequence str2) {
        return equals(str1, str2, false);
    }

    /**
     * 比较两个字符串（大小写不敏感）。
     *
     * <pre>
     * equalsIgnoreCase(null, null)   = true
     * equalsIgnoreCase(null, &quot;abc&quot;)  = false
     * equalsIgnoreCase(&quot;abc&quot;, null)  = false
     * equalsIgnoreCase(&quot;abc&quot;, &quot;abc&quot;) = true
     * equalsIgnoreCase(&quot;abc&quot;, &quot;ABC&quot;) = true
     * </pre>
     *
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     * @return 如果两个字符串相同，或者都是<code>null</code>，则返回<code>true</code>
     */
    public static boolean equalsIgnoreCase(CharSequence str1, CharSequence str2) {
        return equals(str1, str2, true);
    }

    /**
     * 比较两个字符串是否相等。
     *
     * @param str1       要比较的字符串1
     * @param str2       要比较的字符串2
     * @param ignoreCase 是否忽略大小写
     * @return 如果两个字符串相同，或者都是<code>null</code>，则返回<code>true</code>
     * @since 3.2.0
     */
    public static boolean equals(CharSequence str1, CharSequence str2, boolean ignoreCase) {
        if (null == str1) {
            // 只有两个都为null才判断相等
            return str2 == null;
        }
        if (null == str2) {
            // 字符串2空，字符串1非空，直接false
            return false;
        }

        if (ignoreCase) {
            return str1.toString().equalsIgnoreCase(str2.toString());
        } else {
            return str1.equals(str2);
        }
    }

    /**
     * 给定字符串是否与提供的中任一字符串相同（忽略大小写），相同则返回{@code true}，没有相同的返回{@code false}<br>
     * 如果参与比对的字符串列表为空，返回{@code false}
     *
     * @param str1 给定需要检查的字符串
     * @param strs 需要参与比对的字符串列表
     * @return 是否相同
     * @since 4.3.2
     */
    public static boolean equalsAnyIgnoreCase(CharSequence str1, CharSequence... strs) {
        return equalsAny(str1, true, strs);
    }

    /**
     * 给定字符串是否与提供的中任一字符串相同，相同则返回{@code true}，没有相同的返回{@code false}<br>
     * 如果参与比对的字符串列表为空，返回{@code false}
     *
     * @param str1 给定需要检查的字符串
     * @param strs 需要参与比对的字符串列表
     * @return 是否相同
     * @since 4.3.2
     */
    public static boolean equalsAny(CharSequence str1, CharSequence... strs) {
        return equalsAny(str1, false, strs);
    }

    /**
     * 给定字符串是否与提供的中任一字符串相同，相同则返回{@code true}，没有相同的返回{@code false}<br>
     * 如果参与比对的字符串列表为空，返回{@code false}
     *
     * @param str1       给定需要检查的字符串
     * @param ignoreCase 是否忽略大小写
     * @param strs       需要参与比对的字符串列表
     * @return 是否相同
     * @since 4.3.2
     */
    public static boolean equalsAny(CharSequence str1, boolean ignoreCase, CharSequence... strs) {
        if (ArrayUtils.isEmpty(strs)) {
            return false;
        }

        for (CharSequence str : strs) {
            if (equals(str1, str, ignoreCase)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 格式化文本, {} 表示占位符<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") =》 this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
     *
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param params   参数值
     * @return 格式化后的文本
     */
    public static String format(CharSequence template, Object... params) {
        if (null == template) {
            return null;
        }
        if (ArrayUtils.isEmpty(params) || isBlank(template)) {
            return template.toString();
        }
        return StringFormatter.format(template.toString(), params);
    }

    /**
     * 有序的格式化文本，使用{number}做为占位符<br>
     * 例：<br>
     * 通常使用：format("this is {0} for {1}", "a", "b") =》 this is a for b<br>
     *
     * @param pattern   文本格式
     * @param arguments 参数
     * @return 格式化后的文本
     */
    public static String indexedFormat(CharSequence pattern, Object... arguments) {
        return MessageFormat.format(pattern.toString(), arguments);
    }

    /**
     * 编码字符串，编码为UTF-8
     *
     * @param str 字符串
     * @return 编码后的字节码
     */
    public static byte[] utf8Bytes(CharSequence str) {
        return bytes(str, StandardCharsets.UTF_8);
    }

    /**
     * 编码字符串<br>
     * 使用系统默认编码
     *
     * @param str 字符串
     * @return 编码后的字节码
     */
    public static byte[] bytes(CharSequence str) {
        return bytes(str, Charset.defaultCharset());
    }

    /**
     * 编码字符串
     *
     * @param str     字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 编码后的字节码
     */
    public static byte[] bytes(CharSequence str, String charset) {
        return bytes(str, isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }

    /**
     * 编码字符串
     *
     * @param str     字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 编码后的字节码
     */
    public static byte[] bytes(CharSequence str, Charset charset) {
        if (str == null) {
            return null;
        }

        if (null == charset) {
            return str.toString().getBytes();
        }
        return str.toString().getBytes(charset);
    }

    /**
     * 将对象转为字符串<br>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
     *
     * @param obj 对象
     * @return 字符串
     */
    public static String utf8Str(Object obj) {
        return str(obj, StandardCharsets.UTF_8);
    }

    /**
     * 将对象转为字符串<br>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
     *
     * @param obj         对象
     * @param charsetName 字符集
     * @return 字符串
     */
    public static String str(Object obj, String charsetName) {
        return str(obj, Charset.forName(charsetName));
    }

    /**
     * 将对象转为字符串<br>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
     *
     * @param obj     对象
     * @param charset 字符集
     * @return 字符串
     */
    public static String str(Object obj, Charset charset) {
        if (null == obj) {
            return null;
        }

        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof byte[]) {
            return str((byte[]) obj, charset);
        } else if (obj instanceof Byte[]) {
            return str((Byte[]) obj, charset);
        } else if (obj instanceof ByteBuffer) {
            return str((ByteBuffer) obj, charset);
        } else if (ArrayUtils.isArray(obj)) {
            return ArrayUtils.toString(obj);
        }

        return obj.toString();
    }

    /**
     * 将byte数组转为字符串
     *
     * @param bytes   byte数组
     * @param charset 字符集
     * @return 字符串
     */
    public static String str(byte[] bytes, String charset) {
        return str(bytes, isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }

    /**
     * 解码字节码
     *
     * @param data    字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 解码后的字符串
     */
    public static String str(byte[] data, Charset charset) {
        if (data == null) {
            return null;
        }

        if (null == charset) {
            return new String(data);
        }
        return new String(data, charset);
    }

    /**
     * 将Byte数组转为字符串
     *
     * @param bytes   byte数组
     * @param charset 字符集
     * @return 字符串
     */
    public static String str(Byte[] bytes, String charset) {
        return str(bytes, isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }

    /**
     * 解码字节码
     *
     * @param data    字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 解码后的字符串
     */
    public static String str(Byte[] data, Charset charset) {
        if (data == null) {
            return null;
        }

        byte[] bytes = new byte[data.length];
        Byte dataByte;
        for (int i = 0; i < data.length; i++) {
            dataByte = data[i];
            bytes[i] = (null == dataByte) ? -1 : dataByte;
        }

        return str(bytes, charset);
    }

    /**
     * 将编码的byteBuffer数据转换为字符串
     *
     * @param data    数据
     * @param charset 字符集，如果为空使用当前系统字符集
     * @return 字符串
     */
    public static String str(ByteBuffer data, String charset) {
        if (data == null) {
            return null;
        }

        return str(data, Charset.forName(charset));
    }

    /**
     * 将编码的byteBuffer数据转换为字符串
     *
     * @param data    数据
     * @param charset 字符集，如果为空使用当前系统字符集
     * @return 字符串
     */
    public static String str(ByteBuffer data, Charset charset) {
        if (null == charset) {
            charset = Charset.defaultCharset();
        }
        return charset.decode(data).toString();
    }

    /**
     * {@link CharSequence} 转为字符串，null安全
     *
     * @param cs {@link CharSequence}
     * @return 字符串
     */
    public static String str(CharSequence cs) {
        return null == cs ? null : cs.toString();
    }

    /**
     * 调用对象的toString方法，null会返回“null”
     *
     * @param obj 对象
     * @return 字符串
     * @since 4.1.3
     */
    public static String toString(Object obj) {
        return null == obj ? "null" : obj.toString();
    }

    /**
     * 字符串转换为byteBuffer
     *
     * @param str     字符串
     * @param charset 编码
     * @return byteBuffer
     */
    public static ByteBuffer byteBuffer(CharSequence str, String charset) {
        return ByteBuffer.wrap(bytes(str, charset));
    }

    /**
     * 以 conjunction 为分隔符将多个对象转换为字符串
     *
     * @param conjunction 分隔符
     * @param objs        数组
     * @return 连接后的字符串
     * @see ArrayUtils#join(Object, CharSequence)
     */
    public static String join(CharSequence conjunction, Object... objs) {
        return ArrayUtils.join(objs, conjunction);
    }

    /**
     * 将驼峰式命名的字符串转换为下划线方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。<br>
     * 例如：
     *
     * <pre>
     * HelloWorld=》hello_world
     * Hello_World=》hello_world
     * HelloWorld_test=》hello_world_test
     * </pre>
     *
     * @param str 转换前的驼峰式命名的字符串，也可以为下划线形式
     * @return 转换后下划线方式命名的字符串
     */
    public static String toUnderlineCase(CharSequence str) {
        return toSymbolCase(str, CharUtils.UNDERLINE);
    }

    /**
     * 将驼峰式命名的字符串转换为使用符号连接方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。<br>
     *
     * @param str    转换前的驼峰式命名的字符串，也可以为符号连接形式
     * @param symbol 连接符
     * @return 转换后符号连接方式命名的字符串
     * @since 4.0.10
     */
    public static String toSymbolCase(CharSequence str, char symbol) {
        if (str == null) {
            return null;
        }

        final int length = str.length();
        final StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < length; i++) {
            c = str.charAt(i);
            final Character preChar = (i > 0) ? str.charAt(i - 1) : null;
            if (Character.isUpperCase(c)) {
                // 遇到大写字母处理
                final Character nextChar = (i < str.length() - 1) ? str.charAt(i + 1) : null;
                if (null != preChar && Character.isUpperCase(preChar)) {
                    // 前一个字符为大写，则按照一个词对待
                    sb.append(c);
                } else if (null != nextChar && Character.isUpperCase(nextChar)) {
                    // 后一个为大写字母，按照一个词对待
                    if (null != preChar && symbol != preChar) {
                        // 前一个是非大写时按照新词对待，加连接符
                        sb.append(symbol);
                    }
                    sb.append(c);
                } else {
                    // 前后都为非大写按照新词对待
                    if (null != preChar && symbol != preChar) {
                        // 前一个非连接符，补充连接符
                        sb.append(symbol);
                    }
                    sb.append(Character.toLowerCase(c));
                }
            } else {
                if (sb.length() > 0 && Character.isUpperCase(sb.charAt(sb.length() - 1)) && symbol != c) {
                    // 当结果中前一个字母为大写，当前为小写，说明此字符为新词开始（连接符也表示新词）
                    sb.append(symbol);
                }
                // 小写或符号
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static final char EMPTY_CHAR = 0;

    public static String removeAllSpace(String s) {
        return org.apache.commons.lang3.StringUtils.join(s.split("\\s"), "").trim();
    }

    public static String trimAllSpace(String s) {
        return org.apache.commons.lang3.StringUtils.join(s.split("\\s+"), " ");
    }

    public static String camelToText(String s) {
        StringBuilder buf = new StringBuilder();
        char lastChar = ' ';
        for (char c : s.toCharArray()) {
            char nc = c;
            if (Character.isUpperCase(nc) && Character.isLowerCase(lastChar)) {
                buf.append(" ");
                nc = Character.toLowerCase(c);
            } else if (Character.isDigit(lastChar) && Character.isLetter(c) || Character.isDigit(c) && Character.isLetter(lastChar)) {
                if (lastChar != ' ') {
                    buf.append(" ");
                }
                nc = Character.toLowerCase(c);
            }

            if (lastChar != ' ' || c != ' ') {
                buf.append(nc);
            }
            lastChar = c;
        }
        return buf.toString();
    }

    /**
     * inspired by org.apache.commons.text.WordUtils#capitalize
     */
    public static String capitalizeFirstWord(String str, char[] delimiters) {
        if (isEmpty(str)) {
            return str;
        } else {
            boolean done = false;
            Set<Integer> delimiterSet = generateDelimiterSet(delimiters);
            int strLen = str.length();
            int[] newCodePoints = new int[strLen];
            int outOffset = 0;
            boolean capitalizeNext = true;
            int index = 0;

            while (index < strLen) {
                int codePoint = str.codePointAt(index);
                if (delimiterSet.contains(codePoint)) {
                    capitalizeNext = true;
                    newCodePoints[outOffset++] = codePoint;
                    index += Character.charCount(codePoint);
                } else if (!done && capitalizeNext && Character.isLowerCase(codePoint)) {
                    int titleCaseCodePoint = Character.toTitleCase(codePoint);
                    newCodePoints[outOffset++] = titleCaseCodePoint;
                    index += Character.charCount(titleCaseCodePoint);
                    capitalizeNext = false;
                    done = true;
                } else {
                    newCodePoints[outOffset++] = codePoint;
                    index += Character.charCount(codePoint);
                }
            }

            return new String(newCodePoints, 0, outOffset);
        }
    }

    public static String capitalizeFirstWord(String str) {
        if (isEmpty(str)) {
            return str;
        } else {
            StringBuilder buf = new StringBuilder();
            boolean upperNext = true;
            char[] chars = str.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                if (Character.isLetter(c) && upperNext) {
                    buf.append(Character.toUpperCase(c));
                    upperNext = false;
                } else {
                    if (!Character.isLetterOrDigit(c)) {
                        upperNext = true;
                    }
                    buf.append(c);
                }

            }

            return buf.toString();
        }
    }

    /**
     * org.apache.commons.text.WordUtils.generateDelimiterSet
     */
    public static Set<Integer> generateDelimiterSet(char[] delimiters) {
        Set<Integer> delimiterHashSet = new HashSet<>();
        if (delimiters != null && delimiters.length != 0) {
            for (int index = 0; index < delimiters.length; ++index) {
                delimiterHashSet.add(Character.codePointAt(delimiters, index));
            }

        } else {
            if (delimiters == null) {
                delimiterHashSet.add(Character.codePointAt(new char[]{' '}, 0));
            }

        }
        return delimiterHashSet;
    }

    public static String toSoftCamelCase(String s) {
        String[] words = s.split("[\\s_]");

        for (int i = 0; i < words.length; i++) {
            words[i] = StringUtils.capitalizeFirstWord(words[i]);
        }

        return org.apache.commons.lang3.StringUtils.join(words);
    }

    /**
     * 格式化为驼峰
     *
     * @param s str
     * @return str
     */
    public static String toCamelCase(String s) {
        String[] words = org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase(s);

        boolean firstWord = true;
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (firstWord && startsWithLetter(word)) {
                words[i] = word.toLowerCase();
                firstWord = false;
                if (i > 1 && StringUtils.isBlank(words[i - 1]) && isAllLetterOrDigit(words[i - 2])) {
                    words[i - 1] = "";
                }
            } else if (specialWord(word)) { // multiple camelCases
                firstWord = true;
            } else {
                words[i] = org.apache.commons.lang3.StringUtils.capitalize(word.toLowerCase());
                if (i > 1 && StringUtils.isBlank(words[i - 1]) && isAllLetterOrDigit(words[i - 2])) {
                    words[i - 1] = "";
                }
            }
        }
        String join = org.apache.commons.lang3.StringUtils.join(words);
        join = StringUtils.replaceSeparatorBetweenLetters(join, '_', EMPTY_CHAR);
        join = StringUtils.replaceSeparatorBetweenLetters(join, '-', EMPTY_CHAR);
        join = StringUtils.replaceSeparatorBetweenLetters(join, '.', EMPTY_CHAR);
        return join;
    }

    private static boolean isAllLetterOrDigit(String word) {
        for (char c : word.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * CameCase is converted to underline form
     *
     * @param val       CameCase String
     * @param separator the separator
     * @return the target String
     */
    public static String unCameCase(String val, char separator) {
        try {
            if (StringUtils.isNotEmpty(val)) {
                CharacterReader charReader = new CharacterReader(val);
                StringBuilder result = new StringBuilder();
                while (charReader.hasMore()) {
                    char ch = charReader.next();
                    if (ch >= 'A' && ch <= 'Z') {
                        result.append(separator).append((char) (ch + 32));
                    } else if (ch != separator) {
                        result.append(ch);
                    }
                }
                return result.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 下划线形式组合的字符串转大驼峰
     */

    public static String toCapitalizeCamelCase(String str) {

        if (null == str) {

            return null;

        }

        str = toCamelCase(str);

        return str.substring(0, 1).toUpperCase() + str.substring(1);

    }

    private static boolean specialWord(String word) {
        if (isBlank(word)) {
            return false;
        }
        for (char c : word.toCharArray()) {
            if (Character.isDigit(c) || Character.isLetter(c) || isSeparator(c)) {
                return false;
            }
        }
        return true;
    }

    private static boolean startsWithLetter(String word) {
        return word.length() > 0 && Character.isLetter(word.charAt(0));
    }

    private static boolean isNotQuote(String word) {
        return !"\"".equals(word) && !"'".equals(word);
    }

    public static String wordsToConstantCase(String s) {
        StringBuilder buf = new StringBuilder();

        char lastChar = 'a';
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(lastChar) && (!Character.isWhitespace(c) && '_' != c) && buf.length() > 0 && buf.charAt(buf.length() - 1) != '_') {
                buf.append("_");
            }
            if (!Character.isWhitespace(c)) {
                buf.append(Character.toUpperCase(c));

            }
            lastChar = c;
        }
        if (Character.isWhitespace(lastChar)) {
            buf.append("_");
        }

        return buf.toString();

    }

    public static String wordsAndHyphenAndCamelToConstantCase(String s) {

        StringBuilder buf = new StringBuilder();
        char previousChar = ' ';
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            boolean isUpperCaseAndPreviousIsUpperCase = Character.isUpperCase(previousChar) && Character.isUpperCase(c);
            boolean isUpperCaseAndPreviousIsLowerCase = Character.isLowerCase(previousChar) && Character.isUpperCase(c);
            if (Character.isLetter(c) && Character.isLetter(previousChar) && (isUpperCaseAndPreviousIsLowerCase || isUpperCaseAndPreviousIsUpperCase)) {
                buf.append("_");
                // extra _ after number
            } else if (Character.isDigit(previousChar) && Character.isLetter(c) || Character.isDigit(c) && Character.isLetter(previousChar)) {
                buf.append('_');
            }

            // replace separators by _
            if ((isSeparator(c) || Character.isWhitespace(c)) && Character.isLetterOrDigit(previousChar) && nextIsLetterOrDigit(s, i)) {
                buf.append('_');
            } else {
                buf.append(Character.toUpperCase(c));
            }

            previousChar = c;
        }
        return buf.toString();
    }

    private static boolean betweenLettersOrDigits(char[] chars, int i) {
        for (int j = i; j < chars.length; j++) {
            char aChar = chars[j];
            if (!Character.isLetterOrDigit(aChar) && !Character.isWhitespace(aChar)) {
                return false;
            }
            if (Character.isLetterOrDigit(aChar)) {
                break;
            }
        }
        for (int j = i; j >= 0; j--) {
            char aChar = chars[j];
            if (!Character.isLetterOrDigit(aChar) && !Character.isWhitespace(aChar)) {
                return false;
            }
            if (Character.isLetterOrDigit(aChar)) {
                break;
            }
        }
        return true;
    }

    private static boolean isSlash(char c) {
        return c == '\\' || c == '/';
    }

    private static boolean isNotBorderQuote(char actualChar, int i, char[] chars) {
        if (chars.length - 1 == i) {
            char firstChar = chars[0];
            return isQuote(actualChar) && isQuote(firstChar);
        }
        return false;
    }

    private static boolean isQuote(char actualChar) {
        return actualChar == '\'' || actualChar == '\"';
    }

    /**
     * 中划线
     *
     * @param s str
     * @return str
     */
    public static String toDotCase(String s) {
        StringBuilder buf = new StringBuilder();

        char lastChar = ' ';
        for (char c : s.toCharArray()) {
            boolean isUpperCaseAndPreviousIsLowerCase = Character.isLowerCase(lastChar) && Character.isUpperCase(c);
            boolean previousIsWhitespace = Character.isWhitespace(lastChar);
            boolean lastOneIsNotUnderscore = buf.length() > 0 && buf.charAt(buf.length() - 1) != '.';
            if (lastOneIsNotUnderscore && (isUpperCaseAndPreviousIsLowerCase || previousIsWhitespace)) {
                buf.append(".");
            } else if (Character.isDigit(lastChar) && Character.isLetter(c) || Character.isDigit(c) && Character.isLetter(lastChar)) {
                buf.append(".");
            }

            if (c == '.') {
                buf.append('.');
            } else if (c == '-') {
                buf.append('.');
            } else if (c == '_') {
                buf.append('.');
            } else if (!Character.isWhitespace(c)) {
                buf.append(Character.toLowerCase(c));
            }

            lastChar = c;
        }
        if (Character.isWhitespace(lastChar)) {
            buf.append(".");
        }

        return buf.toString();
    }

    public static String replaceSeparator(String s1, char s, char s2) {
        return s1.replace(s, s2);
    }

    public static String replaceSeparatorBetweenLetters(String s, char from, char to) {
        StringBuilder buf = new StringBuilder();
        char lastChar = ' ';
        char[] charArray = s.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (c == from) {
                boolean lastDigit = Character.isDigit(lastChar);
                boolean lastLetterOrDigit = Character.isLetterOrDigit(lastChar);
                boolean nextDigit = nextIsDigit(s, i);
                boolean nextLetterOrDigit = nextIsLetterOrDigit(s, i);

                if (lastDigit && nextDigit) {
                    buf.append(c);
                } else if (lastLetterOrDigit && nextLetterOrDigit) {
                    if (to != EMPTY_CHAR) {
                        buf.append(to);
                    }
                } else {
                    buf.append(c);
                }
            } else {
                buf.append(c);
            }
            lastChar = c;
        }

        return buf.toString();
    }

    private static boolean nextIsDigit(String s, int i) {
        if (i + 1 >= s.length()) {
            return false;
        } else {
            return Character.isDigit(s.charAt(i + 1));
        }
    }

    private static boolean nextIsLetterOrDigit(String s, int i) {
        if (i + 1 >= s.length()) {
            return false;
        } else {
            return Character.isLetterOrDigit(s.charAt(i + 1));
        }
    }

    private static boolean nextIsLetter(String s, int i) {
        if (i + 1 >= s.length()) {
            return false;
        } else {
            return Character.isLetter(s.charAt(i + 1));
        }
    }

    /**
     * <p>
     * Splits the given input sequence around matches of this pattern.
     * <p/>
     * <p/>
     * <p>
     * The array returned by this method contains each substring of the input
     * sequence
     * that is terminated by another subsequence that matches this pattern or is
     * terminated by
     * the end of the input sequence.
     * The substrings in the array are in the order in which they occur in the
     * input.
     * If this pattern does not match any subsequence of the input then the
     * resulting array
     * has just one element, namely the input sequence in string form.
     * <p/>
     * <p/>
     *
     * <pre>
     * splitPreserveAllTokens("boo:and:foo", ":") =  { "boo", ":", "and", ":", "foo"}
     * splitPreserveAllTokens("boo:and:foo", "o") =  { "b", "o", "o", ":and:f", "o", "o"}
     * </pre>
     *
     * @param input The character sequence to be split
     * @return The array of strings computed by splitting the input around matches
     * of this pattern
     */
    public static String[] splitPreserveAllTokens(String input, String regex) {
        int index = 0;
        Pattern p = Pattern.compile(regex);
        ArrayList<String> result = new ArrayList<>();
        Matcher m = p.matcher(input);

        // Add segments before each match found
        while (m.find()) {
            if (isNotEmpty(m.group())) {
                String match = input.subSequence(index, m.start()).toString();
                if (isNotEmpty(match)) {
                    result.add(match);
                }
                result.add(input.subSequence(m.start(), m.end()).toString());
                index = m.end();
            }
        }

        // If no match was found, return this
        if (index == 0) {
            return new String[]{input};
        }

        final String remaining = input.subSequence(index, input.length()).toString();
        if (isNotEmpty(remaining)) {
            result.add(remaining);
        }

        // Construct result
        return result.toArray(new String[0]);

    }

    public static String nonAsciiToUnicode(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (Character c : s.toCharArray()) {
            if (!CharUtils.isAscii(c)) {
                sb.append(org.apache.commons.lang3.CharUtils.unicodeEscaped(c));
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static String escapedUnicodeToString(String s) {
        String[] parts = splitPreserveAllTokens(s, "\\\\u[0-9a-fA-F]{4}");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].startsWith("\\u")) {
                int v = Integer.parseInt(parts[i].substring(2), 16);
                parts[i] = "" + ((char) v);
            }
        }

        return org.apache.commons.lang3.StringUtils.join(parts);
    }

    public static String wordsToHyphenCase(String s) {
        StringBuilder buf = new StringBuilder();
        char lastChar = 'a';
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(lastChar) && (!Character.isWhitespace(c) && '-' != c) && buf.length() > 0 && buf.charAt(buf.length() - 1) != '-') {
                buf.append("-");
            }
            if ('_' == c) {
                buf.append('-');
            } else if ('.' == c) {
                buf.append('-');
            } else if (!Character.isWhitespace(c)) {
                buf.append(Character.toLowerCase(c));
            }
            lastChar = c;
        }
        if (Character.isWhitespace(lastChar)) {
            buf.append("-");
        }
        return buf.toString();
    }

    public static boolean containsLowerCase(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    public static int indexOfAnyButWhitespace(String cs) {
        if (isEmpty(cs)) {
            return cs.length();
        }
        final int csLen = cs.length();
        for (int i = 0; i < csLen; i++) {
            final char ch = cs.charAt(i);
            if (Character.isWhitespace(ch)) {
                continue;
            }
            return i;
        }
        return cs.length();
    }

    public static String substringUntilSpecialCharacter(String s) {
        int firstLetterOrDigitOrSeparator = -1;
        char[] charArray = s.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (!Character.isLetterOrDigit(c) && !Character.isWhitespace(c) && !isSeparator(c) && firstLetterOrDigitOrSeparator != -1) {
                return s.substring(firstLetterOrDigitOrSeparator, i);
            }
            if (Character.isLetterOrDigit(c) || Character.isWhitespace(c) || isSeparator(c)) {
                if (firstLetterOrDigitOrSeparator == -1) {
                    firstLetterOrDigitOrSeparator = i;
                }
            }
        }
        return s;
    }

    public static boolean isSeparator(char c) {
        return c == '.' || c == '-' || c == '_';
    }

    public static boolean containsOnlyLettersAndDigits(String s) {
        for (char c : s.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean noUpperCase(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return false;
            }
        }
        return true;
    }

    public static String removeBorderQuotes(String s) {
        if (isQuoted(s)) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static boolean isQuoted(String selectedText) {
        return selectedText != null && selectedText.length() > 2 && (isBorderChar(selectedText, "\"") || isBorderChar(selectedText, "'"));
    }

    public static boolean isBorderChar(String s, String borderChar) {
        return s.startsWith(borderChar) && s.endsWith(borderChar);
    }

    public static boolean noLowerCase(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isLowerCase(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsUpperCase(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsUpperCaseAfterLowerCase(String s) {
        char previous = ' ';
        char[] charArray = s.toCharArray();
        for (char c : charArray) {
            if (Character.isUpperCase(c) && Character.isLetter(previous) && Character.isLowerCase(previous)) {
                return true;
            }
            previous = c;
        }
        return false;
    }

    public static boolean isCapitalizedFirstButNotAll(String str) {
        if (str.length() == 0) {
            return false;
        }
        Set<Integer> delimiterSet = generateDelimiterSet(new char[]{' '});
        int strLen = str.length();
        int index = 0;

        int firstCapitalizedIndex = -1;
        boolean someUncapitalized = false;
        boolean afterSeparatorOrFirst = true;
        while (index < strLen) {
            int codePoint = str.codePointAt(index);
            if (delimiterSet.contains(codePoint)) {
                afterSeparatorOrFirst = true;
            } else {
                if (Character.isLowerCase(codePoint) && afterSeparatorOrFirst) {
                    if (firstCapitalizedIndex == -1) {
                        return false;
                    }
                    someUncapitalized = true;
                    afterSeparatorOrFirst = false;
                } else if (Character.isUpperCase(codePoint) && afterSeparatorOrFirst) {
                    if (firstCapitalizedIndex == -1) {
                        firstCapitalizedIndex = index;
                    }
                    afterSeparatorOrFirst = false;
                }
            }
            index += Character.charCount(codePoint);
        }
        return firstCapitalizedIndex != -1 && someUncapitalized;
    }

    public static boolean startsWithUppercase(String s) {
        char[] charArray = s.toCharArray();
        for (char c : charArray) {
            if (Character.isLetter(c) && Character.isLowerCase(c)) {
                return false;
            }
            if (Character.isLetter(c) && Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    public static boolean noSeparators(String s, char... delimiters) {
        if (s.length() == 0) {
            return true;
        }
        Set<Integer> delimiterSet = generateDelimiterSet(delimiters);
        boolean letterFound = false;
        char[] charArray = s.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (Character.isLetterOrDigit(c)) {
                letterFound = true;
                continue;
            }
            if (letterFound && delimiterSet.contains((int) c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsSeparatorBetweenLetters(String s, char separator) {
        char previous = '?';
        char[] charArray = s.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (c == separator && Character.isLetterOrDigit(previous) && nextIsLetterOrDigit(s, i)) {
                return true;
            }
            previous = c;
        }
        return false;
    }

    public static List<String> splitToTokensBySpace(String originalText) {
        char[] chars = originalText.toCharArray();
        List<String> result = new ArrayList<>();

        int whiteSpaceBeginning = -1;
        int tokenBeginning = -1;
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            if (aChar == ' ') {
                if (whiteSpaceBeginning == -1) {
                    whiteSpaceBeginning = i;
                }
                if (tokenBeginning != -1) {
                    result.add(new String(Arrays.copyOfRange(chars, tokenBeginning, i)));
                    tokenBeginning = -1;
                }
            } else {
                if (whiteSpaceBeginning != -1) {
                    result.add(new String(Arrays.copyOfRange(chars, whiteSpaceBeginning, i)));
                    whiteSpaceBeginning = -1;
                }
                if (tokenBeginning == -1) {
                    tokenBeginning = i;
                }
            }
        }

        if (tokenBeginning != -1) {
            result.add(new String(Arrays.copyOfRange(chars, tokenBeginning, chars.length)));
        }
        if (whiteSpaceBeginning != -1) {
            result.add(new String(Arrays.copyOfRange(chars, whiteSpaceBeginning, chars.length)));
        }
        return result;
    }

    public static String toSpringEnvVariable(String s) {
        return Arrays.stream(split(s, ".")).map(StringUtils::trim).map(str -> StringUtils.replaceChars(str, "-", "")).map(str -> StringUtils.replaceChars(str, "_", "")).collect(Collectors.joining("_")).toUpperCase();
    }

    /**
     * 包装指定字符串<br>
     * 当前缀和后缀一致时使用此方法
     *
     * @param str             被包装的字符串
     * @param prefixAndSuffix 前缀和后缀
     * @return 包装后的字符串
     * @since 3.1.0
     */
    public static String wrap(CharSequence str, CharSequence prefixAndSuffix) {
        return wrap(str, prefixAndSuffix, prefixAndSuffix);
    }

    /**
     * 包装指定字符串
     *
     * @param str    被包装的字符串
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 包装后的字符串
     */
    public static String wrap(CharSequence str, CharSequence prefix, CharSequence suffix) {
        return nullToEmpty(prefix).concat(nullToEmpty(str)).concat(nullToEmpty(suffix));
    }

    /**
     * 包装多个字符串
     *
     * @param prefixAndSuffix 前缀和后缀
     * @param strs            多个字符串
     * @return 包装的字符串数组
     * @since 4.0.7
     */
    public static String[] wrapAll(CharSequence prefixAndSuffix, CharSequence... strs) {
        return wrapAll(prefixAndSuffix, prefixAndSuffix, strs);
    }

    /**
     * 包装多个字符串
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @param strs   多个字符串
     * @return 包装的字符串数组
     * @since 4.0.7
     */
    public static String[] wrapAll(CharSequence prefix, CharSequence suffix, CharSequence... strs) {
        final String[] results = new String[strs.length];
        for (int i = 0; i < strs.length; i++) {
            results[i] = wrap(strs[i], prefix, suffix);
        }
        return results;
    }

    /**
     * 包装指定字符串，如果前缀或后缀已经包含对应的字符串，则不再包装
     *
     * @param str    被包装的字符串
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 包装后的字符串
     */
    public static String wrapIfMissing(CharSequence str, CharSequence prefix, CharSequence suffix) {
        int len = 0;
        if (isNotEmpty(str)) {
            len += str.length();
        }
        if (isNotEmpty(prefix)) {
            len += str.length();
        }
        if (isNotEmpty(suffix)) {
            len += str.length();
        }
        StringBuilder sb = new StringBuilder(len);
        if (isNotEmpty(prefix) && !startWith(str, prefix)) {
            sb.append(prefix);
        }
        if (isNotEmpty(str)) {
            sb.append(str);
        }
        if (isNotEmpty(suffix) && !endWith(str, suffix)) {
            sb.append(suffix);
        }
        return sb.toString();
    }

    /**
     * 包装多个字符串，如果已经包装，则不再包装
     *
     * @param prefixAndSuffix 前缀和后缀
     * @param strs            多个字符串
     * @return 包装的字符串数组
     * @since 4.0.7
     */
    public static String[] wrapAllIfMissing(CharSequence prefixAndSuffix, CharSequence... strs) {
        return wrapAllIfMissing(prefixAndSuffix, prefixAndSuffix, strs);
    }

    /**
     * 包装多个字符串，如果已经包装，则不再包装
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @param strs   多个字符串
     * @return 包装的字符串数组
     * @since 4.0.7
     */
    public static String[] wrapAllIfMissing(CharSequence prefix, CharSequence suffix, CharSequence... strs) {
        final String[] results = new String[strs.length];
        for (int i = 0; i < strs.length; i++) {
            results[i] = wrapIfMissing(strs[i], prefix, suffix);
        }
        return results;
    }

    /**
     * 去掉字符包装，如果未被包装则返回原字符串
     *
     * @param str    字符串
     * @param prefix 前置字符串
     * @param suffix 后置字符串
     * @return 去掉包装字符的字符串
     * @since 4.0.1
     */
    public static String unWrap(CharSequence str, String prefix, String suffix) {
        if (isWrap(str, prefix, suffix)) {
            return sub(str, prefix.length(), str.length() - suffix.length());
        }
        return str.toString();
    }

    /**
     * 去掉字符包装，如果未被包装则返回原字符串
     *
     * @param str    字符串
     * @param prefix 前置字符
     * @param suffix 后置字符
     * @return 去掉包装字符的字符串
     * @since 4.0.1
     */
    public static String unWrap(CharSequence str, char prefix, char suffix) {
        if (isEmpty(str)) {
            return str(str);
        }
        if (str.charAt(0) == prefix && str.charAt(str.length() - 1) == suffix) {
            return sub(str, 1, str.length() - 1);
        }
        return str.toString();
    }

    /**
     * 去掉字符包装，如果未被包装则返回原字符串
     *
     * @param str             字符串
     * @param prefixAndSuffix 前置和后置字符
     * @return 去掉包装字符的字符串
     * @since 4.0.1
     */
    public static String unWrap(CharSequence str, char prefixAndSuffix) {
        return unWrap(str, prefixAndSuffix, prefixAndSuffix);
    }

    /**
     * 指定字符串是否被包装
     *
     * @param str    字符串
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 是否被包装
     */
    public static boolean isWrap(CharSequence str, String prefix, String suffix) {
        if (ArrayUtils.hasNull(str, prefix, suffix)) {
            return false;
        }
        final String str2 = str.toString();
        return str2.startsWith(prefix) && str2.endsWith(suffix);
    }

    /**
     * 指定字符串是否被同一字符包装（前后都有这些字符串）
     *
     * @param str     字符串
     * @param wrapper 包装字符串
     * @return 是否被包装
     */
    public static boolean isWrap(CharSequence str, String wrapper) {
        return isWrap(str, wrapper, wrapper);
    }

    /**
     * 指定字符串是否被同一字符包装（前后都有这些字符串）
     *
     * @param str     字符串
     * @param wrapper 包装字符
     * @return 是否被包装
     */
    public static boolean isWrap(CharSequence str, char wrapper) {
        return isWrap(str, wrapper, wrapper);
    }

    /**
     * 指定字符串是否被包装
     *
     * @param str        字符串
     * @param prefixChar 前缀
     * @param suffixChar 后缀
     * @return 是否被包装
     */
    public static boolean isWrap(CharSequence str, char prefixChar, char suffixChar) {
        if (null == str) {
            return false;
        }

        return str.charAt(0) == prefixChar && str.charAt(str.length() - 1) == suffixChar;
    }

    /**
     * 补充字符串以满足最小长度
     *
     * <pre>
     * StringUtils.padPre(null, *, *);//null
     * StringUtils.padPre("1", 3, "ABC");//"AB1"
     * StringUtils.padPre("123", 2, "ABC");//"12"
     * </pre>
     *
     * @param str       字符串
     * @param minLength 最小长度
     * @param padStr    补充的字符
     * @return 补充后的字符串
     */
    public static String padPre(CharSequence str, int minLength, CharSequence padStr) {
        if (null == str) {
            return null;
        }
        final int strLen = str.length();
        if (strLen == minLength) {
            return str.toString();
        } else if (strLen > minLength) {
            return subPre(str, minLength);
        }

        return repeatByLength(padStr, minLength - strLen).concat(str.toString());
    }

    /**
     * 补充字符串以满足最小长度
     *
     * <pre>
     * StringUtils.padPre(null, *, *);//null
     * StringUtils.padPre("1", 3, '0');//"001"
     * StringUtils.padPre("123", 2, '0');//"12"
     * </pre>
     *
     * @param str       字符串
     * @param minLength 最小长度
     * @param padChar   补充的字符
     * @return 补充后的字符串
     */
    public static String padPre(CharSequence str, int minLength, char padChar) {
        if (null == str) {
            return null;
        }
        final int strLen = str.length();
        if (strLen == minLength) {
            return str.toString();
        } else if (strLen > minLength) {
            return subPre(str, minLength);
        }

        return repeat(padChar, minLength - strLen).concat(str.toString());
    }

    /**
     * 补充字符串以满足最小长度
     *
     * <pre>
     * StringUtils.padAfter(null, *, *);//null
     * StringUtils.padAfter("1", 3, '0');//"100"
     * StringUtils.padAfter("123", 2, '0');//"23"
     * </pre>
     *
     * @param str       字符串，如果为<code>null</code>，按照空串处理
     * @param minLength 最小长度
     * @param padChar   补充的字符
     * @return 补充后的字符串
     */
    public static String padAfter(CharSequence str, int minLength, char padChar) {
        if (null == str) {
            return null;
        }
        final int strLen = str.length();
        if (strLen == minLength) {
            return str.toString();
        } else if (strLen > minLength) {
            return sub(str, strLen - minLength, strLen);
        }

        return str.toString().concat(repeat(padChar, minLength - strLen));
    }

    /**
     * 补充字符串以满足最小长度
     *
     * <pre>
     * StringUtils.padAfter(null, *, *);//null
     * StringUtils.padAfter("1", 3, "ABC");//"1AB"
     * StringUtils.padAfter("123", 2, "ABC");//"23"
     * </pre>
     *
     * @param str       字符串，如果为<code>null</code>，按照空串处理
     * @param minLength 最小长度
     * @param padStr    补充的字符
     * @return 补充后的字符串
     * @since 4.3.2
     */
    public static String padAfter(CharSequence str, int minLength, CharSequence padStr) {
        if (null == str) {
            return null;
        }
        final int strLen = str.length();
        if (strLen == minLength) {
            return str.toString();
        } else if (strLen > minLength) {
            return subSufByLength(str, minLength);
        }

        return str.toString().concat(repeatByLength(padStr, minLength - strLen));
    }

    /**
     * 居中字符串，两边补充指定字符串，如果指定长度小于字符串，则返回原字符串
     *
     * <pre>
     * StringUtils.center(null, *)   = null
     * StringUtils.center("", 4)     = "    "
     * StringUtils.center("ab", -1)  = "ab"
     * StringUtils.center("ab", 4)   = " ab "
     * StringUtils.center("abcd", 2) = "abcd"
     * StringUtils.center("a", 4)    = " a  "
     * </pre>
     *
     * @param str  字符串
     * @param size 指定长度
     * @return 补充后的字符串
     * @since 4.3.2
     */
    public static String center(CharSequence str, final int size) {
        return center(str, size, CharUtils.SPACE);
    }

    /**
     * 居中字符串，两边补充指定字符串，如果指定长度小于字符串，则返回原字符串
     *
     * <pre>
     * StringUtils.center(null, *, *)     = null
     * StringUtils.center("", 4, ' ')     = "    "
     * StringUtils.center("ab", -1, ' ')  = "ab"
     * StringUtils.center("ab", 4, ' ')   = " ab "
     * StringUtils.center("abcd", 2, ' ') = "abcd"
     * StringUtils.center("a", 4, ' ')    = " a  "
     * StringUtils.center("a", 4, 'y')   = "yayy"
     * StringUtils.center("abc", 7, ' ')   = "  abc  "
     * </pre>
     *
     * @param str     字符串
     * @param size    指定长度
     * @param padChar 两边补充的字符
     * @return 补充后的字符串
     * @since 4.3.2
     */
    public static String center(CharSequence str, final int size, char padChar) {
        if (str == null || size <= 0) {
            return str(str);
        }
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str.toString();
        }
        str = padPre(str, strLen + pads / 2, padChar);
        str = padAfter(str, size, padChar);
        return str.toString();
    }

    /**
     * 居中字符串，两边补充指定字符串，如果指定长度小于字符串，则返回原字符串
     *
     * <pre>
     * StringUtils.center(null, *, *)     = null
     * StringUtils.center("", 4, " ")     = "    "
     * StringUtils.center("ab", -1, " ")  = "ab"
     * StringUtils.center("ab", 4, " ")   = " ab "
     * StringUtils.center("abcd", 2, " ") = "abcd"
     * StringUtils.center("a", 4, " ")    = " a  "
     * StringUtils.center("a", 4, "yz")   = "yayz"
     * StringUtils.center("abc", 7, null) = "  abc  "
     * StringUtils.center("abc", 7, "")   = "  abc  "
     * </pre>
     *
     * @param str    字符串
     * @param size   指定长度
     * @param padStr 两边补充的字符串
     * @return 补充后的字符串
     */
    public static String center(CharSequence str, final int size, CharSequence padStr) {
        if (str == null || size <= 0) {
            return str(str);
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str.toString();
        }
        str = padPre(str, strLen + pads / 2, padStr);
        str = padAfter(str, size, padStr);
        return str.toString();
    }

    /**
     * 创建StringBuilder对象
     *
     * @return StringBuilder对象
     */
    public static StringBuilder builder() {
        return new StringBuilder();
    }

    /**
     * 创建StringBuilder对象
     *
     * @param capacity 初始大小
     * @return StringBuilder对象
     */
    public static StringBuilder builder(int capacity) {
        return new StringBuilder(capacity);
    }

    /**
     * 创建StringBuilder对象
     *
     * @param strs 初始字符串列表
     * @return StringBuilder对象
     */
    public static StringBuilder builder(CharSequence... strs) {
        final StringBuilder sb = new StringBuilder();
        for (CharSequence str : strs) {
            sb.append(str);
        }
        return sb;
    }

    /**
     * 获得StringReader
     *
     * @param str 字符串
     * @return StringReader
     */
    public static StringReader getReader(CharSequence str) {
        if (null == str) {
            return null;
        }
        return new StringReader(str.toString());
    }

    /**
     * 获得StringWriter
     *
     * @return StringWriter
     */
    public static StringWriter getWriter() {
        return new StringWriter();
    }

    /**
     * 统计指定内容中包含指定字符串的数量<br>
     * 参数为 {@code null} 或者 "" 返回 {@code 0}.
     *
     * <pre>
     * StringUtils.count(null, *)       = 0
     * StringUtils.count("", *)         = 0
     * StringUtils.count("abba", null)  = 0
     * StringUtils.count("abba", "")    = 0
     * StringUtils.count("abba", "a")   = 2
     * StringUtils.count("abba", "ab")  = 1
     * StringUtils.count("abba", "xxx") = 0
     * </pre>
     *
     * @param content      被查找的字符串
     * @param strForSearch 需要查找的字符串
     * @return 查找到的个数
     */
    public static int count(CharSequence content, CharSequence strForSearch) {
        if (hasEmpty(content, strForSearch) || strForSearch.length() > content.length()) {
            return 0;
        }

        int count = 0;
        int idx = 0;
        final String content2 = content.toString();
        final String strForSearch2 = strForSearch.toString();
        while ((idx = content2.indexOf(strForSearch2, idx)) > -1) {
            count++;
            idx += strForSearch.length();
        }
        return count;
    }

    /**
     * 统计指定内容中包含指定字符串的数量<br>
     * 参数为 {@code null} 或者 "" 返回 {@code 0}.
     *
     * <pre>
     * StringUtils.count(null, *)       = 0
     * StringUtils.count("", *)         = 0
     * StringUtils.count("abba", null)  = 0
     * StringUtils.count("abba", "")    = 0
     * StringUtils.count("abba", "a")   = 2
     * StringUtils.count("abba", "ab")  = 1
     * StringUtils.count("abba", "xxx") = 0
     * </pre>
     *
     * @param content      被查找的字符串
     * @param strForSearch 需要查找的字符串
     * @return 查找到的个数
     */
    public static Pair<Integer, Integer> countAndIndex(CharSequence content, CharSequence strForSearch) {
        if (hasEmpty(content, strForSearch) || strForSearch.length() > content.length()) {
            return Pair.of(0, -1);
        }

        int count = 0;
        int idx = 0;
        int lastIdx = -1;
        final String content2 = content.toString();
        final String strForSearch2 = strForSearch.toString();
        while ((idx = content2.indexOf(strForSearch2, idx)) > -1) {
            count++;
            idx += strForSearch.length();
            lastIdx = idx;
        }
        return Pair.of(count, lastIdx);
    }

    /**
     * 统计指定内容中包含指定字符的数量
     *
     * @param content       内容
     * @param charForSearch 被统计的字符
     * @return 包含数量
     */
    public static int count(CharSequence content, char charForSearch) {
        int count = 0;
        if (isEmpty(content)) {
            return 0;
        }
        int contentLength = content.length();
        for (int i = 0; i < contentLength; i++) {
            if (charForSearch == content.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 将给定字符串，变成 "xxx...xxx" 形式的字符串
     *
     * @param str       字符串
     * @param maxLength 最大长度
     * @return 截取后的字符串
     */
    public static String brief(CharSequence str, int maxLength) {
        if (null == str) {
            return null;
        }
        if ((str.length() + 3) <= maxLength) {
            return str.toString();
        }
        int w = maxLength / 2;
        int l = str.length();

        final String str2 = str.toString();
        return format("{}...{}", str2.substring(0, maxLength - w), str2.substring(l - w));
    }

    /**
     * 比较两个字符串，用于排序
     *
     * <pre>
     * StringUtils.compare(null, null, *)     = 0
     * StringUtils.compare(null , "a", true)  &lt; 0
     * StringUtils.compare(null , "a", false) &gt; 0
     * StringUtils.compare("a", null, true)   &gt; 0
     * StringUtils.compare("a", null, false)  &lt; 0
     * StringUtils.compare("abc", "abc", *)   = 0
     * StringUtils.compare("a", "b", *)       &lt; 0
     * StringUtils.compare("b", "a", *)       &gt; 0
     * StringUtils.compare("a", "B", *)       &gt; 0
     * StringUtils.compare("ab", "abc", *)    &lt; 0
     * </pre>
     *
     * @param str1       字符串1
     * @param str2       字符串2
     * @param nullIsLess {@code null} 值是否排在前（null是否小于非空值）
     * @return 排序值。负数：str1 &lt; str2，正数：str1 &gt; str2, 0：str1 == str2
     */
    public static int compare(final CharSequence str1, final CharSequence str2, final boolean nullIsLess) {
        if (str1 == str2) {
            return 0;
        }
        if (str1 == null) {
            return nullIsLess ? -1 : 1;
        }
        if (str2 == null) {
            return nullIsLess ? 1 : -1;
        }
        return str1.toString().compareTo(str2.toString());
    }

    /**
     * 比较两个字符串，用于排序，大小写不敏感
     *
     * <pre>
     * StringUtils.compareIgnoreCase(null, null, *)     = 0
     * StringUtils.compareIgnoreCase(null , "a", true)  &lt; 0
     * StringUtils.compareIgnoreCase(null , "a", false) &gt; 0
     * StringUtils.compareIgnoreCase("a", null, true)   &gt; 0
     * StringUtils.compareIgnoreCase("a", null, false)  &lt; 0
     * StringUtils.compareIgnoreCase("abc", "abc", *)   = 0
     * StringUtils.compareIgnoreCase("abc", "ABC", *)   = 0
     * StringUtils.compareIgnoreCase("a", "b", *)       &lt; 0
     * StringUtils.compareIgnoreCase("b", "a", *)       &gt; 0
     * StringUtils.compareIgnoreCase("a", "B", *)       &lt; 0
     * StringUtils.compareIgnoreCase("A", "b", *)       &lt; 0
     * StringUtils.compareIgnoreCase("ab", "abc", *)    &lt; 0
     * </pre>
     *
     * @param str1       字符串1
     * @param str2       字符串2
     * @param nullIsLess {@code null} 值是否排在前（null是否小于非空值）
     * @return 排序值。负数：str1 &lt; str2，正数：str1 &gt; str2, 0：str1 == str2
     */
    public static int compareIgnoreCase(CharSequence str1, CharSequence str2, boolean nullIsLess) {
        if (str1 == str2) {
            return 0;
        }
        if (str1 == null) {
            return nullIsLess ? -1 : 1;
        }
        if (str2 == null) {
            return nullIsLess ? 1 : -1;
        }
        return str1.toString().compareToIgnoreCase(str2.toString());
    }

    /**
     * 指定范围内查找指定字符
     *
     * @param str        字符串
     * @param searchChar 被查找的字符
     * @return 位置
     */
    public static int indexOf(final CharSequence str, char searchChar) {
        return indexOf(str, searchChar, 0);
    }

    /**
     * 指定范围内查找指定字符
     *
     * @param str        字符串
     * @param searchChar 被查找的字符
     * @param start      起始位置，如果小于0，从0开始查找
     * @return 位置
     */
    public static int indexOf(final CharSequence str, char searchChar, int start) {
        if (str instanceof String) {
            return ((String) str).indexOf(searchChar, start);
        } else {
            return indexOf(str, searchChar, start, -1);
        }
    }

    /**
     * 指定范围内查找指定字符
     *
     * @param str        字符串
     * @param searchChar 被查找的字符
     * @param start      起始位置，如果小于0，从0开始查找
     * @param end        终止位置，如果超过str.length()则默认查找到字符串末尾
     * @return 位置
     */
    public static int indexOf(final CharSequence str, char searchChar, int start, int end) {
        final int len = str.length();
        if (start < 0 || start > len) {
            start = 0;
        }
        if (end > len || end < 0) {
            end = len;
        }
        for (int i = start; i < end; i++) {
            if (str.charAt(i) == searchChar) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 指定范围内查找字符串，忽略大小写<br>
     *
     * <pre>
     * StringUtils.indexOfIgnoreCase(null, *, *)          = -1
     * StringUtils.indexOfIgnoreCase(*, null, *)          = -1
     * StringUtils.indexOfIgnoreCase("", "", 0)           = 0
     * StringUtils.indexOfIgnoreCase("aabaabaa", "A", 0)  = 0
     * StringUtils.indexOfIgnoreCase("aabaabaa", "B", 0)  = 2
     * StringUtils.indexOfIgnoreCase("aabaabaa", "AB", 0) = 1
     * StringUtils.indexOfIgnoreCase("aabaabaa", "B", 3)  = 5
     * StringUtils.indexOfIgnoreCase("aabaabaa", "B", 9)  = -1
     * StringUtils.indexOfIgnoreCase("aabaabaa", "B", -1) = 2
     * StringUtils.indexOfIgnoreCase("aabaabaa", "", 2)   = 2
     * StringUtils.indexOfIgnoreCase("abc", "", 9)        = -1
     * </pre>
     *
     * @param str       字符串
     * @param searchStr 需要查找位置的字符串
     * @return 位置
     * @since 3.2.1
     */
    public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        return indexOfIgnoreCase(str, searchStr, 0);
    }

    /**
     * 指定范围内查找字符串
     *
     * <pre>
     * StringUtils.indexOfIgnoreCase(null, *, *)          = -1
     * StringUtils.indexOfIgnoreCase(*, null, *)          = -1
     * StringUtils.indexOfIgnoreCase("", "", 0)           = 0
     * StringUtils.indexOfIgnoreCase("aabaabaa", "A", 0)  = 0
     * StringUtils.indexOfIgnoreCase("aabaabaa", "B", 0)  = 2
     * StringUtils.indexOfIgnoreCase("aabaabaa", "AB", 0) = 1
     * StringUtils.indexOfIgnoreCase("aabaabaa", "B", 3)  = 5
     * StringUtils.indexOfIgnoreCase("aabaabaa", "B", 9)  = -1
     * StringUtils.indexOfIgnoreCase("aabaabaa", "B", -1) = 2
     * StringUtils.indexOfIgnoreCase("aabaabaa", "", 2)   = 2
     * StringUtils.indexOfIgnoreCase("abc", "", 9)        = -1
     * </pre>
     *
     * @param str       字符串
     * @param searchStr 需要查找位置的字符串
     * @param fromIndex 起始位置
     * @return 位置
     * @since 3.2.1
     */
    public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr, int fromIndex) {
        return indexOf(str, searchStr, fromIndex, true);
    }

    /**
     * 指定范围内查找字符串
     *
     * @param str        字符串
     * @param searchStr  需要查找位置的字符串
     * @param fromIndex  起始位置
     * @param ignoreCase 是否忽略大小写
     * @return 位置
     * @since 3.2.1
     */
    public static int indexOf(final CharSequence str, CharSequence searchStr, int fromIndex, boolean ignoreCase) {
        if (str == null || searchStr == null) {
            return INDEX_NOT_FOUND;
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }

        final int endLimit = str.length() - searchStr.length() + 1;
        if (fromIndex > endLimit) {
            return INDEX_NOT_FOUND;
        }
        if (searchStr.length() == 0) {
            return fromIndex;
        }

        if (!ignoreCase) {
            // 不忽略大小写调用JDK方法
            return str.toString().indexOf(searchStr.toString(), fromIndex);
        }

        for (int i = fromIndex; i < endLimit; i++) {
            if (isSubEquals(str, i, searchStr, 0, searchStr.length(), true)) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 指定范围内查找字符串，忽略大小写
     *
     * @param str       字符串
     * @param searchStr 需要查找位置的字符串
     * @return 位置
     * @since 3.2.1
     */
    public static int lastIndexOfIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        return lastIndexOfIgnoreCase(str, searchStr, str.length());
    }

    /**
     * 指定范围内查找字符串，忽略大小写<br>
     * fromIndex 为搜索起始位置，从后往前计数
     *
     * @param str       字符串
     * @param searchStr 需要查找位置的字符串
     * @param fromIndex 起始位置，从后往前计数
     * @return 位置
     * @since 3.2.1
     */
    public static int lastIndexOfIgnoreCase(final CharSequence str, final CharSequence searchStr, int fromIndex) {
        return lastIndexOf(str, searchStr, fromIndex, true);
    }

    /**
     * 指定范围内查找字符串<br>
     * fromIndex 为搜索起始位置，从后往前计数
     *
     * @param str        字符串
     * @param searchStr  需要查找位置的字符串
     * @param fromIndex  起始位置，从后往前计数
     * @param ignoreCase 是否忽略大小写
     * @return 位置
     * @since 3.2.1
     */
    public static int lastIndexOf(final CharSequence str, final CharSequence searchStr, int fromIndex, boolean ignoreCase) {
        if (str == null || searchStr == null) {
            return INDEX_NOT_FOUND;
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        fromIndex = Math.min(fromIndex, str.length());

        if (searchStr.length() == 0) {
            return fromIndex;
        }

        if (!ignoreCase) {
            // 不忽略大小写调用JDK方法
            return str.toString().lastIndexOf(searchStr.toString(), fromIndex);
        }

        for (int i = fromIndex; i > 0; i--) {
            if (isSubEquals(str, i, searchStr, 0, searchStr.length(), true)) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回字符串 searchStr 在字符串 str 中第 ordinal 次出现的位置。<br>
     * 如果 str=null 或 searchStr=null 或 ordinal<=0 则返回-1<br>
     * 此方法来自：Apache-Commons-Lang
     * <p>
     * 栗子（*代表任意字符）：
     *
     * <pre>
     * StringUtils.ordinalIndexOf(null, *, *)          = -1
     * StringUtils.ordinalIndexOf(*, null, *)          = -1
     * StringUtils.ordinalIndexOf("", "", *)           = 0
     * StringUtils.ordinalIndexOf("aabaabaa", "a", 1)  = 0
     * StringUtils.ordinalIndexOf("aabaabaa", "a", 2)  = 1
     * StringUtils.ordinalIndexOf("aabaabaa", "b", 1)  = 2
     * StringUtils.ordinalIndexOf("aabaabaa", "b", 2)  = 5
     * StringUtils.ordinalIndexOf("aabaabaa", "ab", 1) = 1
     * StringUtils.ordinalIndexOf("aabaabaa", "ab", 2) = 4
     * StringUtils.ordinalIndexOf("aabaabaa", "", 1)   = 0
     * StringUtils.ordinalIndexOf("aabaabaa", "", 2)   = 0
     * </pre>
     *
     * @param str       被检查的字符串，可以为null
     * @param searchStr 被查找的字符串，可以为null
     * @param ordinal   第几次出现的位置
     * @return 查找到的位置
     * @since 3.2.3
     */
    public static int ordinalIndexOf(String str, String searchStr, int ordinal) {
        if (str == null || searchStr == null || ordinal <= 0) {
            return INDEX_NOT_FOUND;
        }
        if (searchStr.length() == 0) {
            return 0;
        }
        int found = 0;
        int index = INDEX_NOT_FOUND;
        do {
            index = str.indexOf(searchStr, index + 1);
            if (index < 0) {
                return index;
            }
            found++;
        } while (found < ordinal);
        return index;
    }

    // ------------------------------------------------------------------------------------------------------------------
    // Append and prepend

    /**
     * 如果给定字符串不是以给定的一个或多个字符串为结尾，则在尾部添加结尾字符串<br>
     * 不忽略大小写
     *
     * @param str      被检查的字符串
     * @param suffix   需要添加到结尾的字符串
     * @param suffixes 需要额外检查的结尾字符串，如果以这些中的一个为结尾，则不再添加
     * @return 如果已经结尾，返回原字符串，否则返回添加结尾的字符串
     * @since 3.0.7
     */
    public static String appendIfMissing(final CharSequence str, final CharSequence suffix, final CharSequence... suffixes) {
        return appendIfMissing(str, suffix, false, suffixes);
    }

    /**
     * 如果给定字符串不是以给定的一个或多个字符串为结尾，则在尾部添加结尾字符串<br>
     * 忽略大小写
     *
     * @param str      被检查的字符串
     * @param suffix   需要添加到结尾的字符串
     * @param suffixes 需要额外检查的结尾字符串，如果以这些中的一个为结尾，则不再添加
     * @return 如果已经结尾，返回原字符串，否则返回添加结尾的字符串
     * @since 3.0.7
     */
    public static String appendIfMissingIgnoreCase(final CharSequence str, final CharSequence suffix, final CharSequence... suffixes) {
        return appendIfMissing(str, suffix, true, suffixes);
    }

    /**
     * 如果给定字符串不是以给定的一个或多个字符串为结尾，则在尾部添加结尾字符串
     *
     * @param str        被检查的字符串
     * @param suffix     需要添加到结尾的字符串
     * @param ignoreCase 检查结尾时是否忽略大小写
     * @param suffixes   需要额外检查的结尾字符串，如果以这些中的一个为结尾，则不再添加
     * @return 如果已经结尾，返回原字符串，否则返回添加结尾的字符串
     * @since 3.0.7
     */
    public static String appendIfMissing(final CharSequence str, final CharSequence suffix, final boolean ignoreCase, final CharSequence... suffixes) {
        if (str == null || isEmpty(suffix) || endWith(str, suffix, ignoreCase)) {
            return str(str);
        }
        if (suffixes != null && suffixes.length > 0) {
            for (final CharSequence s : suffixes) {
                if (endWith(str, s, ignoreCase)) {
                    return str.toString();
                }
            }
        }
        return str.toString().concat(suffix.toString());
    }

    /**
     * 如果给定字符串不是以给定的一个或多个字符串为开头，则在首部添加起始字符串<br>
     * 不忽略大小写
     *
     * @param str      被检查的字符串
     * @param prefix   需要添加到首部的字符串
     * @param prefixes 需要额外检查的首部字符串，如果以这些中的一个为起始，则不再添加
     * @return 如果已经结尾，返回原字符串，否则返回添加结尾的字符串
     * @since 3.0.7
     */
    public static String prependIfMissing(final CharSequence str, final CharSequence prefix, final CharSequence... prefixes) {
        return prependIfMissing(str, prefix, false, prefixes);
    }

    /**
     * 如果给定字符串不是以给定的一个或多个字符串为开头，则在首部添加起始字符串<br>
     * 忽略大小写
     *
     * @param str      被检查的字符串
     * @param prefix   需要添加到首部的字符串
     * @param prefixes 需要额外检查的首部字符串，如果以这些中的一个为起始，则不再添加
     * @return 如果已经结尾，返回原字符串，否则返回添加结尾的字符串
     * @since 3.0.7
     */
    public static String prependIfMissingIgnoreCase(final CharSequence str, final CharSequence prefix, final CharSequence... prefixes) {
        return prependIfMissing(str, prefix, true, prefixes);
    }

    /**
     * 如果给定字符串不是以给定的一个或多个字符串为开头，则在首部添加起始字符串
     *
     * @param str        被检查的字符串
     * @param prefix     需要添加到首部的字符串
     * @param ignoreCase 检查结尾时是否忽略大小写
     * @param prefixes   需要额外检查的首部字符串，如果以这些中的一个为起始，则不再添加
     * @return 如果已经结尾，返回原字符串，否则返回添加结尾的字符串
     * @since 3.0.7
     */
    public static String prependIfMissing(final CharSequence str, final CharSequence prefix, final boolean ignoreCase, final CharSequence... prefixes) {
        if (str == null || isEmpty(prefix) || startWith(str, prefix, ignoreCase)) {
            return str(str);
        }
        if (prefixes != null && prefixes.length > 0) {
            for (final CharSequence s : prefixes) {
                if (startWith(str, s, ignoreCase)) {
                    return str.toString();
                }
            }
        }
        return prefix.toString().concat(str.toString());
    }

    /**
     * 将已有字符串填充为规定长度，如果已有字符串超过这个长度则返回这个字符串<br>
     * 字符填充于字符串前
     *
     * @param str        被填充的字符串
     * @param filledChar 填充的字符
     * @param len        填充长度
     * @return 填充后的字符串
     * @since 3.1.2
     */
    public static String fillBefore(String str, char filledChar, int len) {
        return fill(str, filledChar, len, true);
    }

    /**
     * 将已有字符串填充为规定长度，如果已有字符串超过这个长度则返回这个字符串<br>
     * 字符填充于字符串后
     *
     * @param str        被填充的字符串
     * @param filledChar 填充的字符
     * @param len        填充长度
     * @return 填充后的字符串
     * @since 3.1.2
     */
    public static String fillAfter(String str, char filledChar, int len) {
        return fill(str, filledChar, len, false);
    }

    /**
     * 将已有字符串填充为规定长度，如果已有字符串超过这个长度则返回这个字符串
     *
     * @param str        被填充的字符串
     * @param filledChar 填充的字符
     * @param len        填充长度
     * @param isPre      是否填充在前
     * @return 填充后的字符串
     * @since 3.1.2
     */
    public static String fill(String str, char filledChar, int len, boolean isPre) {
        final int strLen = str.length();
        if (strLen > len) {
            return str;
        }

        String filledStr = StringUtils.repeat(filledChar, len - strLen);
        return isPre ? filledStr.concat(str) : str.concat(filledStr);
    }

    /**
     * 截取两个字符串的不同部分（长度一致），判断截取的子串是否相同<br>
     * 任意一个字符串为null返回false
     *
     * @param str1       第一个字符串
     * @param start1     第一个字符串开始的位置
     * @param str2       第二个字符串
     * @param start2     第二个字符串开始的位置
     * @param length     截取长度
     * @param ignoreCase 是否忽略大小写
     * @return 子串是否相同
     * @since 3.2.1
     */
    public static boolean isSubEquals(CharSequence str1, int start1, CharSequence str2, int start2, int length, boolean ignoreCase) {
        if (null == str1 || null == str2) {
            return false;
        }

        return str1.toString().regionMatches(ignoreCase, start1, str2.toString(), start2, length);
    }

    /**
     * 替换指定字符串的指定区间内字符为固定字符
     *
     * @param str          字符串
     * @param startInclude 开始位置（包含）
     * @param endExclude   结束位置（不包含）
     * @param replacedChar 被替换的字符
     * @return 替换后的字符串
     * @since 3.2.1
     */
    public static String replace(CharSequence str, int startInclude, int endExclude, char replacedChar) {
        if (isEmpty(str)) {
            return str(str);
        }
        final int strLength = str.length();
        if (startInclude > strLength) {
            return str(str);
        }
        if (endExclude > strLength) {
            endExclude = strLength;
        }
        if (startInclude > endExclude) {
            // 如果起始位置大于结束位置，不替换
            return str(str);
        }

        final char[] chars = new char[strLength];
        for (int i = 0; i < strLength; i++) {
            if (i >= startInclude && i < endExclude) {
                chars[i] = replacedChar;
            } else {
                chars[i] = str.charAt(i);
            }
        }
        return new String(chars);
    }

    /**
     * 替换指定字符串的指定区间内字符为"*"
     *
     * @param str          字符串
     * @param startInclude 开始位置（包含）
     * @param endExclude   结束位置（不包含）
     * @return 替换后的字符串
     * @since 4.1.14
     */
    public static String hide(CharSequence str, int startInclude, int endExclude) {
        return replace(str, startInclude, endExclude, '*');
    }

    /**
     * 替换字符字符数组中所有的字符为replacedStr<br>
     * 提供的chars为所有需要被替换的字符，例如："\r\n"，则"\r"和"\n"都会被替换，哪怕他们单独存在
     *
     * @param str         被检查的字符串
     * @param chars       需要替换的字符列表，用一个字符串表示这个字符列表
     * @param replacedStr 替换成的字符串
     * @return 新字符串
     * @since 3.2.2
     */
    public static String replaceChars(CharSequence str, String chars, CharSequence replacedStr) {
        if (isEmpty(str) || null == chars || chars.length() == 0) {
            return str(str);
        }
        return replaceChars(str, chars.toCharArray(), replacedStr);
    }

    /**
     * 替换字符字符数组中所有的字符为replacedStr
     *
     * @param str         被检查的字符串
     * @param chars       需要替换的字符列表
     * @param replacedStr 替换成的字符串
     * @return 新字符串
     * @since 3.2.2
     */
    public static String replaceChars(CharSequence str, char[] chars, CharSequence replacedStr) {
        if (isEmpty(str) || ArrayUtils.isEmpty(chars)) {
            return str(str);
        }

        final Set<Character> set = new HashSet<>(chars.length);
        for (char c : chars) {
            set.add(c);
        }
        int strLen = str.length();
        final StringBuilder builder = builder();
        char c;
        for (int i = 0; i < strLen; i++) {
            if (i > 0) {

            }
            c = str.charAt(i);
            builder.append(set.contains(c) ? replacedStr : c);
        }
        return builder.toString();
    }

    /**
     * 字符串指定位置的字符是否与给定字符相同<br>
     * 如果字符串为null，返回false<br>
     * 如果给定的位置大于字符串长度，返回false<br>
     * 如果给定的位置小于0，返回false
     *
     * @param str      字符串
     * @param position 位置
     * @param c        需要对比的字符
     * @return 字符串指定位置的字符是否与给定字符相同
     * @since 3.3.1
     */
    public static boolean equalsCharAt(CharSequence str, int position, char c) {
        if (null == str || position < 0) {
            return false;
        }
        return str.length() > position && c == str.charAt(position);
    }

    /**
     * 给定字符串数组的总长度<br>
     * null字符长度定义为0
     *
     * @param strs 字符串数组
     * @return 总长度
     * @since 4.0.1
     */
    public static int totalLength(CharSequence... strs) {
        int totalLength = 0;
        for (CharSequence str : strs) {
            totalLength += (null == str ? 0 : str.length());
        }
        return totalLength;
    }

    /**
     * 给定字符串中的字母是否全部为大写，判断依据如下：
     *
     * <pre>
     * 1. 大写字母包括A-Z
     * 2. 其它非字母的Unicode符都算作大写
     * </pre>
     *
     * @param str 被检查的字符串
     * @return 是否全部为大写
     * @since 4.2.2
     */
    public static boolean isUpperCase(CharSequence str) {
        if (null == str) {
            return false;
        }
        final int len = str.length();
        for (int i = 0; i < len; i++) {
            if (Character.isLowerCase(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 给定字符串中的字母是否全部为小写，判断依据如下：
     *
     * <pre>
     * 1. 小写字母包括a-z
     * 2. 其它非字母的Unicode符都算作小写
     * </pre>
     *
     * @param str 被检查的字符串
     * @return 是否全部为小写
     * @since 4.2.2
     */
    public static boolean isLowerCase(CharSequence str) {
        if (null == str) {
            return false;
        }
        final int len = str.length();
        for (int i = 0; i < len; i++) {
            if (Character.isUpperCase(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取字符串的长度，如果为null返回0
     *
     * @param cs a 字符串
     * @return 字符串的长度，如果为null返回0
     * @since 4.3.2
     */
    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    /**
     * 给定字符串转为bytes后的byte数（byte长度）
     *
     * @param cs      字符串
     * @param charset 编码
     * @return byte长度
     * @since 4.5.2
     */
    public static int byteLength(CharSequence cs, Charset charset) {
        return cs == null ? 0 : cs.toString().getBytes(charset).length;
    }

    /**
     * 切换给定字符串中的大小写。大写转小写，小写转大写。
     *
     * <pre>
     * StringUtils.swapCase(null)                 = null
     * StringUtils.swapCase("")                   = ""
     * StringUtils.swapCase("The dog has a BONE") = "tHE DOG HAS A bone"
     * </pre>
     *
     * @param str 字符串
     * @return 交换后的字符串
     * @since 4.3.2
     */
    public static String swapCase(final String str) {
        if (isEmpty(str)) {
            return str;
        }

        final char[] buffer = str.toCharArray();

        for (int i = 0; i < buffer.length; i++) {
            final char ch = buffer[i];
            if (Character.isUpperCase(ch)) {
                buffer[i] = Character.toLowerCase(ch);
            } else if (Character.isTitleCase(ch)) {
                buffer[i] = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
                buffer[i] = Character.toUpperCase(ch);
            }
        }
        return new String(buffer);
    }

    private static final String NULL_STR = "null";

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3456789][0-9]{9}$");

    /**
     * empty str
     */
    public static final String EMPTY_STRING = "";
    /**
     * dict
     */
    private final static String AS = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 判断字符串是否为空
     *
     * @param str original str
     * @return is blank
     */
    public static boolean isBlank(Object str) {
        return str == null || "".equals(str.toString().trim()) || NULL_STR.equalsIgnoreCase(str.toString().trim());
    }

    /**
     * 格式化null为空
     *
     * @param str original str
     * @return new str
     */
    public static String convertNullToEmpty(Object str) {
        if (str == null) {
            return "";
        }
        return str.toString();
    }

    /**
     * 格式化null为0
     *
     * @param str original str
     * @return new str
     */
    public static String convertNullToZero(Object str) {
        return convertToNumber(str, 0);
    }

    /**
     * 格式化null
     *
     * @param str original str
     * @return str maybe null
     */
    public static String convertNullToNull(Object str) {
        if (str == null || "".equals(str.toString().trim()) || NULL_STR.equalsIgnoreCase(str.toString().trim())) {
            return null;
        }
        return str.toString();
    }

    /**
     * 格式化字符串为数字
     * <p>
     * 可能会返回null
     * </p>
     *
     * @param str original str
     * @return number
     */
    public static String convertToNumber(Object str) {
        if (str == null || "".equals(str.toString().trim()) || NULL_STR.equalsIgnoreCase(str.toString().trim())) {
            return null;
        }
        String s = org.apache.commons.lang3.StringUtils.deleteWhitespace(str.toString());
        if (s.endsWith("%")) {
            final double v = Double.parseDouble(s.substring(0, s.length() - 1)) / 100;
            return String.valueOf(v);
        }
        return String.valueOf(str);
    }

    /**
     * 格式化字符串为数字
     * <p>
     * 如果字符串为空或者null，会返回默认值
     * </p>
     *
     * @param str str
     * @return number
     */
    public static String convertToNumber(Object str, Number defaultValue) {
        if (str == null || "".equals(str.toString().trim()) || NULL_STR.equalsIgnoreCase(str.toString().trim())) {
            return String.valueOf(defaultValue);
        }
        String s = org.apache.commons.lang3.StringUtils.deleteWhitespace(str.toString());
        if (s.endsWith("%")) {
            final double v = Double.parseDouble(s.substring(0, s.length() - 1)) / 100;
            return String.valueOf(v);
        }
        return String.valueOf(str);
    }

    public static boolean isNumeric(CharSequence cs) {
        if (isBlank(cs)) {
            return false;
        } else {
            int sz = cs.length();

            for (int i = 0; i < sz; ++i) {
                if (!Character.isDigit(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static int getStringLen(String str) {
        if (isEmpty(str)) {
            return 0;
        }
        return str.length();
    }

    public static String convertEncode(String strIn, String encoding, String targetEncoding) {
        String strOut = strIn;
        if (strIn == null) {
            return null;
        }

        try {
            if (encoding != null && targetEncoding != null) {
                strOut = new String(strIn.getBytes(encoding), targetEncoding);
            } else if (encoding != null) {
                strOut = new String(strIn.getBytes(encoding));
            } else if (targetEncoding != null) {
                strOut = new String(strIn.getBytes(), targetEncoding);
            } else {
                return strOut;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("Unsupported Encoding: " + encoding);
        }
        return strOut;
    }

    /**
     * 截取str中以startStr开头，endStr结束的字符串
     *
     * @param str      str
     * @param startStr str
     * @param endStr   end
     * @return 返回以以startStr开头，以endStr结束的字符串，如果startStr不存在，则有str为起始；如果endStr不存在，则以字符串结束为终结
     */
    public static String extractString(String str, String startStr, String endStr) {
        if (isEmpty(str)) {
            return str;
        }

        if (startStr == null) {
            startStr = "";
        }

        int startIdx;

        startIdx = str.indexOf(startStr);

        if (startIdx == -1) {
            startIdx = 0;
        } else {
            startIdx += startStr.length();
        }

        int endIdx = str.length();
        if (endStr != null) {
            endIdx = str.indexOf(endStr, startIdx);
            if (endIdx == -1) {
                endIdx = str.length();
            }
        }

        return str.substring(startIdx, endIdx);
    }

    /**
     * 替换指定的子串，替换所有出现的子串。
     *
     * <p>
     * 如果字符串为<code>null</code>则返回<code>null</code>，如果指定子串为<code>null</code>
     * ，则返回原字符串。
     * </p>
     *
     * @param text 要扫描的字符串
     * @param repl 要搜索的子串
     * @param with 替换字符串
     * @return 被替换后的字符串，如果原始字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String replace(String text, String repl, String with) {
        return replace(text, repl, with, -1);
    }

    public static String replaceSpace(String text) {
        return replace(text, " ", "");
    }

    /**
     * 替换指定的子串，替换指定的次数。
     *
     * <p>
     * 如果字符串为<code>null</code>则返回<code>null</code>，如果指定子串为<code>null</code>
     * ，则返回原字符串。
     * </p>
     *
     * @param text 要扫描的字符串
     * @param repl 要搜索的子串
     * @param with 替换字符串
     * @param max  maximum number of values to replace, or <code>-1</code> if no
     *             maximum
     * @return 被替换后的字符串，如果原始字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String replace(String text, String repl, String with, int max) {
        if ((text == null) || (repl == null) || (with == null) || (repl.length() == 0) || (max == 0)) {
            return text;
        }

        StringBuilder buf = new StringBuilder(text.length());
        int start = 0;
        int end;

        while ((end = text.indexOf(repl, start)) != -1) {
            buf.append(text, start, end).append(with);
            start = end + repl.length();

            if (--max == 0) {
                break;
            }
        }

        if (start == 0) {
            return text;
        }

        buf.append(text.substring(start));
        return buf.toString();
    }

    /**
     * 除去字符串头尾部的空白，如果字符串是<code>null</code>，依然返回<code>null</code>。
     *
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code>
     * 来判定空白， 因而可以除去英文字符集之外的其它空白，如中文空格。
     * </p>
     *
     * @param str 要处理的字符串
     * @return 除去空白的字符串，如果原字串为<code>null</code>，则返回<code>null</code>
     */
    public static String trim(String str) {
        return trim(str, null, 0);
    }

    /**
     * 除去字符串头尾部的指定字符，如果字符串是<code>null</code>，依然返回<code>null</code>。
     *
     * @param str        要处理的字符串
     * @param stripChars 要除去的字符，如果为<code>null</code>表示除去空白字符
     * @return 除去指定字符后的的字符串，如果原字串为<code>null</code>，则返回<code>null</code>
     */
    public static String trim(String str, String stripChars) {
        return trim(str, stripChars, 0);
    }

    /**
     * 除去字符串头部的空白，如果字符串是<code>null</code>，则返回<code>null</code>。
     *
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code>
     * 来判定空白， 因而可以除去英文字符集之外的其它空白，如中文空格。
     * </p>
     *
     * @param str 要处理的字符串
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回
     * <code>null</code>
     */
    public static String trimStart(String str) {
        return trim(str, null, -1);
    }

    /**
     * 除去字符串头部的指定字符，如果字符串是<code>null</code>，依然返回<code>null</code>。
     *
     * @param str        要处理的字符串
     * @param stripChars 要除去的字符，如果为<code>null</code>表示除去空白字符
     * @return 除去指定字符后的的字符串，如果原字串为<code>null</code>，则返回<code>null</code>
     */
    public static String trimStart(String str, String stripChars) {
        return trim(str, stripChars, -1);
    }

    /**
     * 除去字符串尾部的空白，如果字符串是<code>null</code>，则返回<code>null</code>。
     * </p>
     *
     * @param str 要处理的字符串
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回
     * <code>null</code>
     */
    public static String trimEnd(String str) {
        return trim(str, null, 1);
    }

    /**
     * 除去字符串尾部的指定字符，如果字符串是<code>null</code>，依然返回<code>null</code>。
     *
     * @param str        要处理的字符串
     * @param stripChars 要除去的字符，如果为<code>null</code>表示除去空白字符
     * @return 除去指定字符后的的字符串，如果原字串为<code>null</code>，则返回<code>null</code>
     */
    public static String trimEnd(String str, String stripChars) {
        return trim(str, stripChars, 1);
    }

    /**
     * 除去字符串头尾部的空白，如果结果字符串是空字符串<code>""</code>，则返回<code>null</code>。
     *
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code>
     * 来判定空白， 因而可以除去英文字符集之外的其它空白，如中文空格。
     * </p>
     *
     * @param str 要处理的字符串
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回
     * <code>null</code>
     */
    public static String trimToNull(String str) {
        return trimToNull(str, null);
    }

    /**
     * 除去字符串头尾部的空白，如果结果字符串是空字符串<code>""</code>，则返回<code>null</code>。
     *
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code>
     * 来判定空白， 因而可以除去英文字符集之外的其它空白，如中文空格。
     *
     * @param str        要处理的字符串
     * @param stripChars 要除去的字符，如果为<code>null</code>表示除去空白字符
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回
     * <code>null</code>
     */
    public static String trimToNull(String str, String stripChars) {
        String result = trim(str, stripChars);

        if ((result == null) || (result.length() == 0)) {
            return null;
        }

        return result;
    }

    /**
     * 除去字符串头尾部的空白，如果字符串是<code>null</code>，则返回空字符串<code>""</code>。
     *
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code>
     * 来判定空白， 因而可以除去英文字符集之外的其它空白，如中文空格。
     * </p>
     *
     * @param str 要处理的字符串
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回
     * <code>null</code>
     */
    public static String trimToEmpty(String str) {
        return trimToEmpty(str, null);
    }

    /**
     * 除去字符串头尾部的空白，如果字符串是<code>null</code>，则返回空字符串<code>""</code>。
     *
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code>
     * 来判定空白， 因而可以除去英文字符集之外的其它空白，如中文空格。
     * </p>
     *
     * @param str 要处理的字符串
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回
     * <code>null</code>
     */
    public static String trimToEmpty(String str, String stripChars) {
        String result = trim(str, stripChars);

        if (result == null) {
            return EMPTY_STRING;
        }

        return result;
    }

    /**
     * 除去字符串头尾部的指定字符，如果字符串是<code>null</code>，依然返回<code>null</code>。
     *
     * @param str        要处理的字符串
     * @param stripChars 要除去的字符，如果为<code>null</code>表示除去空白字符
     * @param mode       <code>-1</code>表示trimStart，<code>0</code>表示trim全部，
     *                   <code>1</code>表示trimEnd
     * @return 除去指定字符后的的字符串，如果原字串为<code>null</code>，则返回<code>null</code>
     */
    private static String trim(String str, String stripChars, int mode) {
        if (str == null) {
            return null;
        }

        int length = str.length();
        int start = 0;
        int end = length;

        // 扫描字符串头部
        if (mode <= 0) {
            if (stripChars == null) {
                while ((start < end) && (Character.isWhitespace(str.charAt(start)))) {
                    start++;
                }
            } else if (stripChars.length() == 0) {
                return str;
            } else {
                while ((start < end) && (stripChars.indexOf(str.charAt(start)) != -1)) {
                    start++;
                }
            }
        }

        // 扫描字符串尾部
        if (mode >= 0) {
            if (stripChars == null) {
                while ((start < end) && (Character.isWhitespace(str.charAt(end - 1)))) {
                    end--;
                }
            } else if (stripChars.length() == 0) {
                return str;
            } else {
                while ((start < end) && (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
                    end--;
                }
            }
        }

        if ((start > 0) || (end < length)) {
            return str.substring(start, end);
        }

        return str;
    }

    /**
     * Check if a String has length.
     * <p>
     *
     * @param str the String to check, may be <code>null</code>
     * @return <code>true</code> if the String is not null and has length
     */
    public static boolean hasLength(String str) {
        return (str != null && str.length() > 0);
    }

    /**
     * Check if a String has text. More specifically, returns <code>true</code>
     * if the string not <code>null<code>, it's <code>length is > 0</code>, and it
     * has at least one
     * non-whitespace character.
     * <p>
     *
     * @param str the String to check, may be <code>null</code>
     * @return <code>true</code> if the String is not null, length > 0, and not
     * whitespace only
     * @see Character#isWhitespace
     */
    public static boolean hasText(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a String has text. More specifically, returns <code>true</code>
     * if the string not <code>null<code>, it's <code>length is > 0</code>, and it
     * has at least one
     * non-whitespace character.
     * <p>
     *
     * @param str the String to check, may be <code>null</code>
     * @return <code>true</code> if the String is not null, length > 0, and not
     * whitespace only
     * @see Character#isWhitespace
     */
    public static boolean hasText(StringBuffer str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert a CSV list into an array of Strings.
     *
     * @param str CSV list
     * @return an array of Strings, or the empty array if s is null
     */
    public static String[] commaDelimitedListToStringArray(String str) {
        return delimitedListToStringArray(str, ",");
    }

    /**
     * Take a String which is a delimited list and convert it to a String array.
     * <p>
     * A single delimiter can consists of more than one character: It will still
     * be considered as single delimiter string, rather than as bunch of
     * potential delimiter characters - in contrast to
     * <code>tokenizeToStringArray</code>.
     *
     * @param str       the input String
     * @param delimiter the delimiter between elements (this is a single
     *                  delimiter, rather than a bunch individual delimiter
     *                  characters)
     * @return an array of the tokens in the list
     */
    public static String[] delimitedListToStringArray(String str, String delimiter) {
        if (str == null) {
            return new String[0];
        }
        if (delimiter == null) {
            return new String[]{str};
        }

        List<String> result = new ArrayList<>();
        if ("".equals(delimiter)) {
            for (int i = 0; i < str.length(); i++) {
                result.add(str.substring(i, i + 1));
            }
        } else {
            int pos = 0;
            int delPos;
            while ((delPos = str.indexOf(delimiter, pos)) != -1) {
                result.add(str.substring(pos, delPos));
                pos = delPos + delimiter.length();
            }
            if (str.length() > 0 && pos <= str.length()) {
                // Add rest of String, but not in case of empty input.
                result.add(str.substring(pos));
            }
        }
        return toStringArray(result);
    }

    /**
     * Copy the given Collection into a String array. The Collection must
     * contain String elements only.
     *
     * @param collection the Collection to copy
     * @return the String array (<code>null</code> if the Collection was
     * <code>null</code> as well)
     */
    public static String[] toStringArray(Collection<String> collection) {
        if (collection == null) {
            return null;
        }
        return collection.toArray(new String[0]);
    }

    /**
     * 判断字符串是否为空
     *
     * @param s str
     * @return if is null or str length is zero
     */
    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    /**
     * 其中任意一个是否为空
     *
     * @param s arrays
     * @return if the arrays has at least one empty element it returns true
     */
    public static boolean isAnyEmpty(String... s) {
        if (s == null) {
            return true;
        }
        for (String str : s) {
            if (isEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否全部为空
     *
     * @param s arrays
     * @return if all empty
     */
    public static boolean isAllEmpty(String... s) {
        if (s == null) {
            return true;
        }
        for (String str : s) {
            if (isNotEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 反转一个字符串
     *
     * @param s str
     * @return revert
     */
    public static String reverse(String s) {
        if (isEmpty(s)) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s);
        return sb.reverse().toString();
    }

    /**
     * 转义所有"<"和">"符号
     *
     * @param str str str
     * @return str
     */
    public static String escapeHtml(String str) {
        if (str == null) {
            return "";
        }
        str = str.replace(">", "&gt;");
        str = str.replace("<", "&lt;");
        return str;
    }

    /**
     * 将字符串截取一定的长度,末尾用omit补全
     *
     * @param s          s
     * @param byteLength g
     * @param omit       g
     * @return str
     */
    public static String limitString(String s, int byteLength, String omit) {
        if (s == null) {
            return null;
        }
        if (byteLength <= 0) {
            return "";
        }
        if (s.getBytes().length <= byteLength) {
            return s;
        }
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            String tmp = s.substring(i, i + 1);
            if (r.toString().getBytes().length + tmp.getBytes().length > byteLength) {
                break;
            }
            r.append(tmp);
        }
        if (omit != null) {
            r.append(omit);
        }
        return r.toString();
    }

    public static String getPatternMatchStr(String src, String pattern) {
        if (src == null) {
            return null;
        }
        try {
            Pattern p = Pattern.compile(pattern);
            Matcher matcher = p.matcher(src);
            if (matcher.find()) {
                return matcher.group();
            }
        } catch (Exception e) {
            return null;
        }
        return null;

    }

    /**
     * 获取固定长度的随机字符串
     *
     * @param length len
     * @return str
     */
    public static String getRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = AS.charAt((int) (Math.random() * (AS.length())));
            sb.append(c);
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 判断某字符串是否都在ascii的范围内
     *
     * @param str str
     * @return boolean
     */
    public static boolean isAsciiStr(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (str.charAt(i) > 255) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断某字符串是否数值型
     *
     * @param str str
     * @return str
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 将字符串转换成小写。
     *
     * <p>
     * 如果字符串是<code>null</code>则返回<code>null</code>。
     * </p>
     *
     * @param str 要转换的字符串
     * @return 大写字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String toLowerCase(String str) {
        if (str == null) {
            return null;
        }

        return str.toLowerCase();
    }

    /**
     * 取得指定子串在字符串中出现的次数。
     *
     * <p>
     * 如果字符串为<code>null</code>或空，则返回<code>0</code>。
     *
     * </p>
     *
     * @param str    要扫描的字符串
     * @param subStr 子字符串
     * @return 子串在字符串中出现的次数，如果字符串为<code>null</code>或空，则返回<code>0</code>
     */
    public static int countMatches(String str, String subStr) {
        if ((str == null) || (str.length() == 0) || (subStr == null) || (subStr.length() == 0)) {
            return 0;
        }

        int count = 0;
        int index = 0;

        while ((index = str.indexOf(subStr, index)) != -1) {
            count++;
            index += subStr.length();
        }

        return count;
    }

    /**
     * 从inputReader中读出一行
     *
     * @param inputReader reader
     * @return line
     * @throws IOException e
     */
    public static String readLine(InputStreamReader inputReader) throws IOException {
        StringBuilder sb = new StringBuilder();

        char c;
        int n;
        int num = 0;
        while ((n = inputReader.read()) > 0) {
            num++;
            c = (char) n;
            if (c == '\n' || c == '\r') {
                break;
            }
            sb.append(c);
        }
        if (num == 0) {
            return null;
        }

        return sb.toString();
    }

    /**
     * 过滤掉ascii的字符串，主要用于取出中文文字
     *
     * @param str str
     */
    public static String filterAsciiStr(String str) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c > 255) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 取出ascii的文本
     *
     * @param str str
     */
    public static String getAsciiStr(String str) {
        if (str == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < 255) {
                sb.append(c);
            }
        }

        return trim(sb.toString());
    }

    /**
     * 去除无用的字符串，只保留字母和数字
     *
     * @param str str
     * @return str
     */
    public static String getLetterOrDigit(String str) {
        if (str == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * is phone number
     *
     * @param phone stc
     * @return is phone number
     */
    public static boolean isPhone(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        try {
            boolean b;
            // 验证手机号
            Matcher m = PHONE_PATTERN.matcher(phone);
            b = m.matches();
            return b;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 转义字符串
     *
     * @param s str
     * @return str
     */
    public static String escapeQueryChars(String s) {
        if (isEmpty(s)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            // These characters are part of the query syntax and must be escaped
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':' || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~' || c == '*' || c == '?' || c == '|' || c == '&' || c == ';' || c == '/' || Character.isWhitespace(c)) {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 过滤ascii码为32以下的控制码
     *
     * @param str str
     * @return str
     */
    public static String trimCtrlChars(String str) {
        if (isEmpty(str)) {
            return str;
        }

        boolean containCtrlChar = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < 32 && c != '\n' && c != '\r') {
                containCtrlChar = true;
                break;
            }
        }

        if (containCtrlChar) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c < 32 && c != 10 && c != 13) {
                    continue;
                }
                sb.append(c);
            }

            str = sb.toString();
        }

        return str;

    }

    /**
     * 统计行数
     *
     * @param str str
     * @return line
     */
    public static int countLines(String str) {
        if (null == str || str.length() == 0) {
            return 0;
        }
        int line = 1;
        int len = str.length();
        for (int pos = 0; pos < len; pos++) {
            char c = str.charAt(pos);
            if ('\r' == c) {
                line++;
                if (pos + 1 < len && str.charAt(pos + 1) == '\n') {
                    pos++;
                }
            } else if ('\n' == c) {
                line++;
            }
        }
        return line;
    }

    public static String removeWhiteLines(String str) {
        if (null == str || str.length() == 0) {
            return str;
        }
        String all = str.replaceAll("(?m)^\\s*$(\\n|\\r\\n)", "");
        return trim(all);

    }

}
