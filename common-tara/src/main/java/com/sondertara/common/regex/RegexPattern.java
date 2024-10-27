package com.sondertara.common.regex;


public class RegexPattern {
    private RegexPattern() {
    }


    /**
     * jdk 上可用, joni 上不可用
     * ref: https://unicode-table.com/en/blocks/
     * <p>
     * \u2E80 - \u2EFF 偏旁部首
     * \u2F00 - \u2FDF 康熙字典
     * \u3000 - \u303F 标点符号
     * \u31C0 - \u31EF 笔顺
     * \u3400 - \u4DBF 象形文字
     * \u4E00 - \u9FFF 常用文字
     * \uF900 - \uFAFF 兼容象形文字
     * \uFE30 - \uFE4F 纵向标点符号
     * \uFF01 - \uFF1F 标点符号
     */
    //
    public static final String CHINESE_CHAR_PATTERN = "["
            + "\\x{4E00}-\\x{9FFF}"
            + "\\x{2E80}-\\x{2EFF}"
            + "\\x{2F00}-\\x{2FDF}"
            + "\\x{3000}-\\x{303F}"
            + "\\x{31C0}-\\x{31EF}"
            + "\\x{3400}-\\x{4DBF}"
            + "\\x{F900}-\\x{FAFF}"
            + "\\x{FE30}-\\x{FE4F}"
            + "\\x{FF01}-\\x{FF1F}"
            + "\\x{20000}-\\x{2A6DF}"
            + "\\x{2A700}-\\x{2EBEF}"
            + "\\x{2F800}—\\x{2FA1F}"
            + "\\x{30000}—\\x{3134F}"
            + "]";
    /**
     * JONI ，JDK上均可用
     */
    public static final String CHINESE_CHAR_PATTERN_JONI = "["
            + "\\x4E00-\\x9FFF"   // joni OK
            //+ "\\x2E80-\\x2EFF"   // joni error
            //+ "\\x2F00-\\x2FDF"   // joni error
            + "\\x3000-\\x303F"   // joni OK
            + "\\x31C0-\\x31EF"   // joni OK
            + "\\x3400-\\x4DBF"   // joni OK
            + "\\xF900-\\xFAFF"   // joni OK
            + "\\xFE30-\\xFE4F"   // joni OK
            + "\\xFF01-\\xFF1F"  // joni OK
            //+ "\\x{20000}-\\x{2A6DF}"  // joni error
            // + "\\x{2A700}-\\x{2EBEF}" // joni error
            + "\\x{2F800}—\\x{2FA1F}" // joni OK
            + "\\x{30000}—\\x{3134F}" // joni OK
            + "]";


    public static final Regexp CHINESE_CHAR = RegexUtils.createRegexp("jdk", CHINESE_CHAR_PATTERN, null);
    public static final Regexp CHINESE_CHARS = RegexUtils.createRegexp("jdk", CHINESE_CHAR_PATTERN + "+", null);

    public static final Regexp COMMA_SPLIT_PATTERN = RegexUtils.createRegexp("\\s*[,]+\\s*");




    public static final Regexp PATTERN_LAMBDA_CLASS = RegexUtils.createRegexp(".*\\$\\$Lambda\\$[0-9]+/.*");


    /**
     * 英文字母 、数字和下划线
     */
    public static final String GENERAL = "^\\w+$";
    /**
     * 数字
     */
    public static final String NUMBERS = "-?(0|[1-9]\\d*)(\\.\\d+)?";
    /**
     * 字母
     */
    public static final String WORD = "[a-zA-Z]+";
    /**
     * 单个中文汉字
     */
    public static final String CHINESE = "[\u4E00-\u9FFF]";
    /**
     * 中文汉字
     */
    public static final String CHINESES = CHINESE + "+";
    /**
     * 分组
     */
    public static final String GROUP_VAR = "\\$(\\d+)";
    /**
     * IP v4
     */
    public static final String IPV4 = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
    /**
     * IP v6
     */
    public static final String IPV6 = "(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]+|::(ffff(:0{1,4})?:)?((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9]))";
    /**
     * 货币
     */
    public static final String MONEY = "^(\\d+(?:\\.\\d+)?)$";
    /**
     * 邮件，符合RFC 5322规范，正则来自：<a href="http://emailregex.com/">...</a>
     * What is the maximum length of a valid email address?
     * <a href="https://stackoverflow.com/questions/386294/what-is-the-maximum-length-of-a-valid-email-address/44317754">...</a>
     */
    public static final String EMAIL = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])";
    /**
     * 移动电话
     */
    public static final String MOBILE = "(?:0|86|\\+86)?1[3-9]\\d{9}";
    /**
     * 中国香港移动电话
     * eg: 中国香港： +852 5100 4810， 三位区域码+10位数字, 中国香港手机号码8位数
     * eg: 中国大陆： +86 180 4953 1399，2位区域码标示+13位数字
     * 中国大陆 +86 Mainland China
     * 中国香港 +852 Hong Kong
     * 中国澳门 +853 Macao
     * 中国台湾 +886 Taiwan
     */
    public static final String MOBILE_HK = "(?:0|852|\\+852)?\\d{8}";
    /**
     * 中国台湾移动电话
     * eg: 中国台湾： +886 09 60 000000， 三位区域码+号码以数字09开头 + 8位数字, 中国台湾手机号码10位数
     * 中国台湾 +886 Taiwan 国际域名缩写：TW
     */
    public static final String MOBILE_TW = "(?:0|886|\\+886)?(?:|-)09\\d{8}";
    /**
     * 中国澳门移动电话
     * eg: 中国台湾： +853 68 00000， 三位区域码 +号码以数字6开头 + 7位数字, 中国台湾手机号码8位数
     * 中国澳门 +853 Macao 国际域名缩写：MO
     */
    public static final String MOBILE_MO = "(?:0|853|\\+853)?(?:|-)6\\d{7}";
    /**
     * 座机号码<br>
     * pr#387@Gitee
     */
    public static final String TEL = "(010|02\\d|0[3-9]\\d{2})-?(\\d{6,8})";
    /**
     * 座机号码+400+800电话
     *
     * @see <a href="https://baike.baidu.com/item/800">800</a>
     */
    public static final String TEL_400_800 = "0\\d{2,3}[\\- ]?[1-9]\\d{6,7}|[48]00[\\- ]?[1-9]\\d{6}";
    /**
     * 18位身份证号码
     */
    public static final String CITIZEN_ID = "[1-9]\\d{5}[1-2]\\d{3}((0\\d)|(1[0-2]))(([012]\\d)|3[0-1])\\d{3}(\\d|X|x)";
    /**
     * 邮编，兼容港澳台
     */
    public static final String ZIP_CODE = "^(0[1-7]|1[0-356]|2[0-7]|3[0-6]|4[0-7]|5[0-7]|6[0-7]|7[0-5]|8[0-9]|9[0-8])\\d{4}|99907[78]$";
    /**
     * 生日
     */
    public static final String BIRTHDAY = "^(\\d{2,4})([/\\-.年]?)(\\d{1,2})([/\\-.月]?)(\\d{1,2})日?$";
    /**
     * URL
     */
    public static final String URL = "[a-zA-z]+://[^\\s]*";
    /**
     * Http URL
     */
    public static final String URL_HTTP = "(https://|http://)?([\\w-]+\\.)+[\\w-]+(:\\d+)*(/[\\w- ./?%&=]*)?";
    /**
     * 中文字、英文字母、数字和下划线
     */
    public static final String GENERAL_WITH_CHINESE = "^[\u4E00-\u9FFF\\w]+$";
    /**
     * UUID
     */
    public static final String UUID = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
    /**
     * 不带横线的UUID
     */
    public static final String UUID_SIMPLE = "^[0-9a-fA-F]{32}$";
    /**
     * MAC地址正则
     */
    public static final String MAC_ADDRESS = "((?:[a-fA-F0-9]{1,2}[:-]){5}[a-fA-F0-9]{1,2})|0x(\\d{12}).+ETHER";
    /**
     * 16进制字符串
     */
    public static final String HEX = "^[a-fA-F0-9]+$";
    /**
     * 时间正则
     */
    public static final String TIME = "\\d{1,2}:\\d{1,2}(:\\d{1,2})?";

    /**
     * 中国车牌号码（兼容新能源车牌）
     */
    public static final String PLATE_NUMBER =
            //https://gitee.com/dromara/hutool/issues/I1B77H?from=project-issue
            "^(([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z](([0-9]{5}[ABCDEFGHJK])|([ABCDEFGHJK]([A-HJ-NP-Z0-9])[0-9]{4})))|" +
                    //https://gitee.com/dromara/hutool/issues/I1BJHE?from=project-issue
                    "([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领]\\d{3}\\d{1,3}[领])|" +
                    "([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳使领]))$";

    /**
     * 统一社会信用代码
     * <pre>
     * 第一部分：登记管理部门代码1位 (数字或大写英文字母)
     * 第二部分：机构类别代码1位 (数字或大写英文字母)
     * 第三部分：登记管理机关行政区划码6位 (数字)
     * 第四部分：主体标识码（组织机构代码）9位 (数字或大写英文字母)
     * 第五部分：校验码1位 (数字或大写英文字母)
     * </pre>
     */
    public static final String CREDIT_CODE = "^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$";
    /**
     * 车架号（车辆识别代号由世界制造厂识别代号(WMI、车辆说明部分(VDS)车辆指示部分(VIS)三部分组成，共 17 位字码。）<br>
     * 别名：车辆识别代号、车辆识别码、车架号、十七位码<br>
     * 标准号：GB 16735-2019<br>
     * 标准官方地址：https://openstd.samr.gov.cn/bzgk/gb/newGbInfo?hcno=E2EBF667F8C032B1EDFD6DF9C1114E02
     * 对年产量大于或等于1 000 辆的完整车辆和/或非完整车辆制造厂：
     * <pre>
     *   第一部分为世界制造厂识别代号(WMI)，3位
     *   第二部分为车辆说明部分(VDS)，     6位
     *   第三部分为车辆指示部分(VIS)，     8位
     * </pre>
     * <p>
     * 对年产量小于 1 000 辆的完整车辆和/或非完整车辆制造厂：
     * <pre>
     *   第一部分为世界制造广识别代号(WMI),3位;
     *   第二部分为车辆说明部分(VDS)，6位;
     *   第三部分的三、四、五位与第一部分的三位字码起构成世界制造厂识别代号(WMI),其余五位为车辆指示部分(VIS)，8位。
     * </pre>
     *
     * <pre>
     *   eg:LDC613P23A1305189
     *   eg:LSJA24U62JG269225
     *   eg:LBV5S3102ESJ25655
     * </pre>
     */
    public static final String CAR_VIN = "^[A-HJ-NPR-Z0-9]{8}[X0-9]([A-HJ-NPR-Z0-9]{3}\\d{5}|[A-HJ-NPR-Z0-9]{5}\\d{3})$";
    /**
     * 驾驶证  别名：驾驶证档案编号、行驶证编号
     * eg:430101758218
     * 12位数字字符串
     * 仅限：中国驾驶证档案编号
     */
    public static final String CAR_DRIVING_LICENCE = "^[0-9]{12}$";
    /**
     * 中文姓名
     * 维吾尔族姓名里面的点是 · 输入法中文状态下，键盘左上角数字1前面的那个符号；<br>
     * 错误字符：{@code ．.。．.}<br>
     * 正确维吾尔族姓名：
     * <pre>
     * 霍加阿卜杜拉·麦提喀斯木
     * 玛合萨提别克·哈斯木别克
     * 阿布都热依木江·艾斯卡尔
     * 阿卜杜尼亚孜·毛力尼亚孜
     * </pre>
     * <pre>
     * ----------
     * 错误示例：孟  伟                reason: 有空格
     * 错误示例：连逍遥0               reason: 数字
     * 错误示例：依帕古丽-艾则孜        reason: 特殊符号
     * 错误示例：牙力空.买提萨力        reason: 新疆人的点不对
     * 错误示例：王建鹏2002-3-2        reason: 有数字、特殊符号
     * 错误示例：雷金默(雷皓添）        reason: 有括号
     * 错误示例：翟冬:亮               reason: 有特殊符号
     * 错误示例：李                   reason: 少于2位
     * ----------
     * </pre>
     * 总结中文姓名：2-60位，只能是中文和维吾尔族的点·
     * 放宽汉字范围：如生僻姓名 刘欣䶮yǎn
     */
    public static final String CHINESE_NAME = "^[\u2E80-\u9FFF·]{2,60}$";

    /**
     * 16进制字符串
     */
    public static final Regexp PATTERN_HEX = RegexUtils.createRegexp(HEX, Option.CASE_INSENSITIVE);
}
