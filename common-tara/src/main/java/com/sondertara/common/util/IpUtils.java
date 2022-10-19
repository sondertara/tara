package com.sondertara.common.util;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangxiaohu
 */
public class IpUtils {

    private static final String IP_UNKNOWN = "unknown";

    private static Map<String, List<InetAddress>> LOCAL_ADDRESS_MAP;

    static {
        LOCAL_ADDRESS_MAP = getLocalAddresses();
    }

    /**
     * #func 获取IP地址<br>
     * #desc 不再简单getRemoteAddr
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || IP_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 取X-Forwarded-For中第一个非unknown的有效IP字符串。
        if (ip.contains(",")) {
            String[] ipList = ip.split(",");
            String tmp;
            for (String s : ipList) {
                tmp = s;
                if (tmp != null && !IP_UNKNOWN.equalsIgnoreCase(tmp.trim())) {
                    return tmp.trim();
                }
            }
        }
        return ip;
    }

    /**
     * 获得本机的机器名称，用来从配置文件中排除本机
     *
     * @author zhangfeng
     */
    public static String getLocalHostName() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * #func 返回主机名的全限定域名<br>
     *
     * @author v_dongguoshuang
     */
    public static String getFullyLocalHostName() {
        String hostName = null;
        try {
            InetAddress inet = InetAddress.getLocalHost();
            hostName = inet.getCanonicalHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return hostName;
    }

    /**
     * #func 返回本机IP<br>
     *
     * @author v_dongguoshuang
     */
    public static String getLocalHostAddress() {
        try {
            InetAddress inet = InetAddress.getLocalHost();
            return inet.getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * ??
     *
     * @return map
     */
    public static Map<String, List<InetAddress>> getLocalAddresses() {

        if (LOCAL_ADDRESS_MAP != null) {
            return LOCAL_ADDRESS_MAP;
        }
        Map<String, List<InetAddress>> map = new LinkedHashMap<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces != null && interfaces.hasMoreElements()) {
                List<InetAddress> tmpList = new ArrayList<>();
                NetworkInterface interfaceN = interfaces.nextElement();
                // byte[] hardwareAddress = interfaceN.getHardwareAddress();
                // if (null != hardwareAddress) {
                // String[] hexAddress = new String[hardwareAddress.length];
                // for (int i = 0; i < hardwareAddress.length; i++) {
                // hexAddress[i] = String.format("%02X", hardwareAddress[i]);
                // }
                // System.out.println(String.join("-", hexAddress));
                // }

                Enumeration<InetAddress> ienum = interfaceN.getInetAddresses();
                while (ienum.hasMoreElements()) {
                    InetAddress ia = ienum.nextElement();
                    tmpList.add(ia);
                }

                map.put(interfaceN.getName(), tmpList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public static String getLocalIp() {
        return getLocalIPV4(false);
    }

    /**
     * 获取本机IPv4地址
     *
     * @param isInter 是否内网
     * @return 地址
     */
    public static String getLocalIPV4(boolean isInter) {
        Map<String, List<InetAddress>> map = getLocalAddresses();

        List<InetAddress> list = new ArrayList<>();

        map.values().forEach(list::addAll);
        String localIp = "";
        for (InetAddress ia : list) {
            String ip = ia.getHostAddress();
            // 忽略ipv6
            if (ia instanceof Inet6Address || ip.startsWith("127")) {
                continue;
            }
            // ipv4?
            if (StringUtils.isBlank(localIp)) {
                localIp = ip;
            }
            if (isInter && ip.startsWith("19.")) {
                return ip;
            }
            if (!isInter && !ip.startsWith("19.")) {
                return ip;
            }
        }
        return localIp;
    }

    /**
     * #func 判断本机是否和传入的域名一致<br>
     */
    public static boolean isDomainEqualsLocal(String domainName) {
        if (StringUtils.isBlank(domainName)) {
            return false;
        }
        try {
            InetAddress host = InetAddress.getByName(domainName);
            String domainAddress = host.getHostAddress();
            if (domainAddress.equals(getLocalHostAddress())) {
                return true;
            } else {
                return false;
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return false;
    }

}