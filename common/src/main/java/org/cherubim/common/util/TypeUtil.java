package org.cherubim.common.util;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author
 * @since 2017/6/8.
 */
public class TypeUtil {
    /**
     * 感叹号转换为逗号存储到数据库
     *
     * @return
     */
    public static String exclamaToComma(String src) {
        if (StringUtil.isEmpty(src)) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        String[] tems = src.split("!");
        result.append(tems[0]);
        for (int i = 1; i < tems.length; i++) {
            result.append(",");
            result.append(tems[i]);
        }
        return result.toString();
    }

    public static String getValue(String str) {
        if (str == null || "null".equalsIgnoreCase(str)) {
            return null;
        }
        return str;
    }


    /**
     * 专门用于解析json对应的string到界面回车的显示
     *
     * @return
     */
    public static String parseJsonShow(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean spaceFlag = false;
        boolean leftFlag = false;   //[]这个括号内部的逗号不替换
        char[] charList = str.toCharArray();
        int leftCout = 0;
        for (int i = 0; i < charList.length; i++) {
            char c = charList[i];
            if (c == ',') {
                if (leftFlag) {
                    stringBuilder.append(c);
                    continue;      //表示当前处于是数组中的逗号，这个时候不关心
                }
                stringBuilder.append(",\n");
                if (spaceFlag) {
                    stringBuilder.append(addSpace(leftCout));
                }
            } else if (c == '{') {
                stringBuilder.append("\n");
                if (spaceFlag) {
                    stringBuilder.append(addSpace(leftCout));
                }
                spaceFlag = true;
                stringBuilder.append("{\n");
                leftCout++;
                if (spaceFlag) {
                    stringBuilder.append(addSpace(leftCout));
                }
            } else if (c == '}') {
                leftCout--;
                stringBuilder.append("\n");
                if (spaceFlag) {
                    stringBuilder.append(addSpace(leftCout));
                }
                stringBuilder.append("}");
            } else if (c == '[') {
                leftFlag = true;
                stringBuilder.append(c);
            } else if (c == ']') {
                leftFlag = false;
                stringBuilder.append(c);
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    public static String addSpace(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            stringBuilder.append("     ");
        }
        return stringBuilder.toString();
    }

    /**
     * 匹配表达式，规则版本的格式：x.x.x
     *
     * @param ruleVersion
     * @return
     */
    public static boolean versionIsStandard(String ruleVersion) {
        String regEx = "^\\d+.\\d+.\\d$";
        return Pattern.matches(regEx, ruleVersion);
    }

    /**
     * 这里是对规则的版本号做的这种约束
     * 1.版本里面不能含有字母
     * 2.里面的点的个数必须为2
     *
     * @return
     */
    public static String versionStandard(String version) {
        if (haveStr(version)) {
            version = version.toLowerCase();
            version = version.replace("v", "");
        }

        Integer num = version.length() - version.replace(".", "").length();
        if (num == 0) {
            version = addStr(version);
            version = addStr(version);
        } else if (num == 1) {
            version = addStr(version);
        }
        return version;
    }

    public static String addStr(String version) {
        return version += ".0";
    }

    /**
     * 是否包含字母
     *
     * @param content
     * @return
     */
    public static boolean haveStr(String content) {
        String regex = ".*[a-zA-Z]+.*";
        Matcher m = Pattern.compile(regex).matcher(content);
        return m.matches();
    }

    /**
     * 全是字母
     *
     * @param content
     * @return
     */
    public static boolean allStr(String content) {
        String regex = "^[A-Za-z]+$";
        Matcher m = Pattern.compile(regex).matcher(content);
        return m.matches();
    }

    /**
     * 全是数字
     *
     * @param content
     * @return
     */
    public static boolean allNumber(String content) {
        String regex = "^[0-9]*$";
        Matcher m = Pattern.compile(regex).matcher(content);
        return m.matches();
    }

    /**
     * 含有中文
     *
     * @param content
     * @return
     */
    public static boolean haveChinaWord(String content) {
        String regex = "^.*[\\u4e00-\\u9fa5]+.*$";
        Matcher m = Pattern.compile(regex).matcher(content);
        return m.matches();
    }

    /**
     * 除最后一位全是数字
     *
     * @param content
     * @return
     */
    public static boolean allNumberExceptLast(String content) {
        String regex = "^[0-9]*$";
        String subContext = content.substring(0, content.length() - 1);
        Matcher m = Pattern.compile(regex).matcher(subContext);
        return m.matches();
    }

    /**
     * 布尔值转整型
     * @param b 布尔值
     * @return true返回1，false返回0，其余情况返回null
     */
    public static Integer booleanToInteger(Boolean b){
        if(b == null){
            return null;
        }
        if(b == true){
            return 1;
        }
        if(b == false){
            return 0;
        }
        return null;
    }

    /**
     * 整形转布尔值
     * @param i 整型
     * @return 1返回true，0返回false，其余情况返回null
     */
    public static Boolean integerToBoolean(Integer i){
        if(i == null){

        }
        if(i == 1){
            return true;
        }
        if(i == 0){
            return false;
        }
        return null;
    }

    /**
     * 对象转字符串
     * @param o 转换的对象
     * @return 对象为空时返回""，其余返回o.toString()
     */
    public static String toString(Object o){
        if(o == null){
            return "";
        }
        return o.toString();
    }

    /**
     * 字符串转整型
     * @param intString 整型数字字符串
     * @return 整型数字字符串对应的Integer值
     */
    public static Integer parseInt(String intString){
        Integer intValue = null;
        try{
            intValue = Integer.parseInt(intString);
        }catch(Exception e){

        }
        return intValue;
    }


}
