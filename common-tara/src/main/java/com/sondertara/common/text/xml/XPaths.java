package com.sondertara.common.text.xml;


import com.sondertara.common.base.Emptys;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.StreamUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 根节点：/ <br>
 * 当前节点：.<br>
 * 父节点：..<br>
 * 父子关系: /<br>
 * 元素属性：@<br>
 * 元素节点：元素名<br>
 * 所有子元素节点：*<br>
 * 所有属性：@*<br>
 * 并列关系：|<br>
 * 约束关系：[]<br>
 * <p>
 * <p>
 * 避免注入的方案：
 * https://cheatsheetseries.owasp.org/cheatsheets/Injection_Prevention_in_Java_Cheat_Sheet.html
 *
 * @author jinuo.fang
 */
public class XPaths {
    private XPaths() {

    }

    // ---- 属性判断

    /**
     * 不包括指定属性
     *
     * @param attrName attribute name
     * @return expression
     */
    public static String notContainsAttr(String attrName) {
        XPathInjectionPreventionHandler handler = XPathInjectionPreventionHandler.getInstance();
        attrName = handler.apply(attrName);
        return "name(@" + attrName + ")=''";
    }

    public static String attrNotEquals(String attrName, String value) {
        XPathInjectionPreventionHandler handler = XPathInjectionPreventionHandler.getInstance();
        attrName = handler.apply(attrName);
        value = handler.apply(value);
        return "@" + attrName + "!=" + value;
    }

    // ---------------------------------逻辑表达式--------------------------------------//

    /**
     * ( a or b ) and c
     *
     * @return expression
     */
    public static String aorB_and_C(String expA, String expB, String expC) {
        XPathInjectionPreventionHandler handler = XPathInjectionPreventionHandler.getInstance();
        expA = handler.apply(expA);
        expB = handler.apply(expB);
        expC = handler.apply(expC);
        return "(" + expA + " or " + expB + ") and " + expC;
    }

    /**
     * a or ( b and c)
     *
     * @return expression
     */
    public static String a_or_BandC(String expA, String expB, String expC) {
        XPathInjectionPreventionHandler handler = XPathInjectionPreventionHandler.getInstance();
        expA = handler.apply(expA);
        expB = handler.apply(expB);
        expC = handler.apply(expC);
        return expA + " or (" + expB + " and " + expC + ")";
    }

    public static String wrapXpath(String xpath, boolean usingCustomNamespace, String namespacePrefix) {
        if (usingCustomNamespace && Emptys.isNotEmpty(xpath)) {
            return XPaths.wrapXpath(xpath, namespacePrefix);
        }
        return xpath;
    }

    public static String wrapXpath(String xpathExpr, final String namespacePrefix) {
        boolean startWithSlash = StringUtils.startsWith(xpathExpr, "/");
        String[] segments = StringUtils.split(xpathExpr, "/");
        final String prefix = namespacePrefix + ":";
        List<String> prefixedSegments = StreamUtils.of(segments).filter(Objects::nonNull).map(new Function<String, String>() {
            @Override
            public String apply(String segment) {
                return StringUtils.startsWith(segment, prefix) ? segment : (prefix + segment);
            }
        }).collect(Collectors.toList());
        return (startWithSlash ? "/" : "") + StringUtils.join("/", prefixedSegments);
    }
}
