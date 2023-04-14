package com.sondertara.common.util;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.sondertara.common.convert.ConvertUtils;
import com.sondertara.common.exception.TaraException;
import com.sondertara.common.function.Filter;
import com.sondertara.common.lang.Assert;
import com.sondertara.common.regex.PatternPool;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigInteger;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;

/**
 * IP地址工具类
 *
 * @author ZhuKun
 * @since 5.4.1
 */
public class IpUtils {

    /**
     * 默认最小端口，1024
     */
    public static final int PORT_RANGE_MIN = 1024;
    /**
     * 默认最大端口，65535
     */
    public static final int PORT_RANGE_MAX = 0xFFFF;
    public static final String LOCAL_IP = "127.0.0.1";
    /**
     * IP段的分割符
     */
    public static final String IP_SPLIT_MARK = "-";
    /**
     * IP与掩码的分割符
     */
    public static final String IP_MASK_SPLIT_MARK = StringUtils.SLASH;
    /**
     * 最大掩码位
     */
    public static final int IP_MASK_MAX = 32;
    private static final String IP_UNKNOWN = "unknown";
    /**
     * 请求通过反向代理之后，可能包含请求客户端真实IP的HTTP HEADER
     * 如果后续扩展，有其他可能包含IP的HTTP HEADER，加到这里即可
     */
    private final static String[] POSSIBLE_HEADERS = new String[]{
            "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP",
            "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"
    };
    private static final Map<String, List<InetAddress>> LOCAL_ADDRESS_MAP;

    static {
        LOCAL_ADDRESS_MAP = getLocalAddresses();
    }

    /**
     * 获得本机的机器名称，用来从配置文件中排除本机
     *
     * @return the host name
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
     * get all InetAddress
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
            while (interfaces.hasMoreElements()) {
                List<InetAddress> tmpList = new ArrayList<>();
                NetworkInterface interfaceN = interfaces.nextElement();
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
        String localIp = "127.0.0.1"; // 本地IP
        String netIp = null; // 外网IP

        InetAddress ip;
        boolean netIpFound = false; // 是否找到外网IP

        List<NetworkInterface> interfaces = getNetworkInterfaces();
        assert interfaces != null;
        for (NetworkInterface networkInterface : interfaces) {
            Enumeration<InetAddress> address = networkInterface.getInetAddresses();
            while (address.hasMoreElements()) {
                ip = address.nextElement();
                if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {
                    // 外网IP
                    netIp = ip.getHostAddress();
                    netIpFound = true;
                    break;
                } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {
                    // 内网IP
                    localIp = ip.getHostAddress();
                }
            }
            if (netIpFound) {
                break;
            }
        }


        if (StringUtils.isNotBlank(netIp)) {
            // 如果配置了外网IP则优先返回外网IP地址
            return netIp;
        }
        return localIp;

    }


    /**
     * #func 返回本机IP<br>
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

    /**
     * #func 获取IP地址<br>
     * #desc 不再简单getRemoteAddr
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = null;
        // 先检查代理：逐个HTTP HEADER检查过去，看看是否存在客户端真实IP
        for (String header : POSSIBLE_HEADERS) {
            ip = request.getHeader(header);
            if (StringUtils.isNotBlank(ip) && !IP_UNKNOWN.equalsIgnoreCase(ip)) {
                // 请求经过多次反向代理后可能会有多个IP值（以英文逗号分隔），第一个IP才是客户端真实IP
                return ip.contains(",") ? ip.split(",")[0] : ip;
            }
        }
        // 从所有可能的HTTP HEADER中都没有找到客户端真实IP，采用request.getRemoteAddr()来兜底
        ip = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
            // 说明是从本机发出的请求，直接获取并返回本机IP地址
            return getLocalIp();
        }
        return ip;
    }


    /**
     * 格式化IP段
     *
     * @param ip   IP地址
     * @param mask 掩码
     * @return 返回xxx.xxx.xxx.xxx/mask的格式
     */
    public static String formatIpBlock(String ip, String mask) {
        return ip + IP_MASK_SPLIT_MARK + getMaskBitByMask(mask);
    }

    /**
     * 智能转换IP地址集合
     *
     * @param ipRange IP段，支持X.X.X.X-X.X.X.X或X.X.X.X/X
     * @param isAll   true:全量地址，false:可用地址；仅在ipRange为X.X.X.X/X时才生效
     * @return IP集
     */
    public static List<String> list(String ipRange, boolean isAll) {
        if (ipRange.contains(IP_SPLIT_MARK)) {
            // X.X.X.X-X.X.X.X
            final String[] range = StringUtils.splitToArray(ipRange, IP_SPLIT_MARK);
            return list(range[0], range[1]);
        } else if (ipRange.contains(IP_MASK_SPLIT_MARK)) {
            // X.X.X.X/X
            final String[] param = StringUtils.splitToArray(ipRange, IP_MASK_SPLIT_MARK);
            return list(param[0], Integer.parseInt(param[1]), isAll);
        } else {
            return Lists.newArrayList(ipRange);
        }
    }

    /**
     * 根据IP地址、子网掩码获取IP地址区间
     *
     * @param ip      IP地址
     * @param maskBit 掩码位，例如24、32
     * @param isAll   true:全量地址，false:可用地址
     * @return 区间地址
     */
    public static List<String> list(String ip, int maskBit, boolean isAll) {
        if (maskBit == IP_MASK_MAX) {
            final List<String> list = new ArrayList<>();
            if (isAll) {
                list.add(ip);
            }
            return list;
        }

        String startIp = getBeginIpStr(ip, maskBit);
        String endIp = getEndIpStr(ip, maskBit);
        if (isAll) {
            return list(startIp, endIp);
        }

        int lastDotIndex = startIp.lastIndexOf(CharUtils.DOT) + 1;
        startIp = StringUtils.subPre(startIp, lastDotIndex) +
                (Integer.parseInt(Objects.requireNonNull(StringUtils.subSuf(startIp, lastDotIndex))) + 1);
        lastDotIndex = endIp.lastIndexOf(CharUtils.DOT) + 1;
        endIp = StringUtils.subPre(endIp, lastDotIndex) +
                (Integer.parseInt(Objects.requireNonNull(StringUtils.subSuf(endIp, lastDotIndex))) - 1);
        return list(startIp, endIp);
    }

    /**
     * 得到IP地址区间
     *
     * @param ipFrom 开始IP
     * @param ipTo   结束IP
     * @return 区间地址
     */
    public static List<String> list(String ipFrom, String ipTo) {
        final int[] ipf = ConvertUtils.convert(int[].class, StringUtils.splitToArray(ipFrom, CharUtils.DOT));
        final int[] ipt = ConvertUtils.convert(int[].class, StringUtils.splitToArray(ipTo, CharUtils.DOT));

        final List<String> ips = new ArrayList<>();
        for (int a = ipf[0]; a <= ipt[0]; a++) {
            for (int b = (a == ipf[0] ? ipf[1] : 0); b <= (a == ipt[0] ? ipt[1]
                    : 255); b++) {
                for (int c = (b == ipf[1] ? ipf[2] : 0); c <= (b == ipt[1] ? ipt[2]
                        : 255); c++) {
                    for (int d = (c == ipf[2] ? ipf[3] : 0); d <= (c == ipt[2] ? ipt[3]
                            : 255); d++) {
                        ips.add(a + "." + b + "." + c + "." + d);
                    }
                }
            }
        }
        return ips;
    }

    /**
     * 根据long值获取ip v4地址：xx.xx.xx.xx
     *
     * @param longIp IP的long表示形式
     * @return IP V4 地址
     */
    public static String longToIpv4(long longIp) {
        final StringBuilder sb = StringUtils.builder();
        // 直接右移24位
        sb.append(longIp >> 24 & 0xFF);
        sb.append(CharUtils.DOT);
        // 将高8位置0，然后右移16位
        sb.append(longIp >> 16 & 0xFF);
        sb.append(CharUtils.DOT);
        sb.append(longIp >> 8 & 0xFF);
        sb.append(CharUtils.DOT);
        sb.append(longIp & 0xFF);
        return sb.toString();
    }

    /**
     * 根据ip地址(xxx.xxx.xxx.xxx)计算出long型的数据
     * 方法别名：inet_aton
     *
     * @param strIp IP V4 地址
     * @return long值
     */
    public static long ipv4ToLong(String strIp) {
        final Matcher matcher = PatternPool.IPV4.matcher(strIp);
        if (matcher.matches()) {
            return matchAddress(matcher);
        }
        throw new IllegalArgumentException("Invalid IPv4 address!");
    }

    /**
     * 根据ip地址(xxx.xxx.xxx.xxx)计算出long型的数据, 如果格式不正确返回 defaultValue
     *
     * @param strIp        IP V4 地址
     * @param defaultValue 默认值
     * @return long值
     */
    public static long ipv4ToLong(String strIp, long defaultValue) {
        return isIpv4(strIp) ? ipv4ToLong(strIp) : defaultValue;
    }

    /**
     * 验证是否为IPV4地址
     *
     * @param value 值
     * @return 是否为IPV4地址
     */
    public static boolean isIpv4(CharSequence value) {
        return RegexUtils.isMatch(PatternPool.IPV4, value);
    }

    /**
     * 验证是否为IPV4地址
     *
     * @param value 值
     * @return 是否为IPV4地址
     */
    public static boolean isIpv6(CharSequence value) {
        return RegexUtils.isMatch(PatternPool.IPV6, value);
    }

    /**
     * 根据 ip/掩码位 计算IP段的起始IP（字符串型）
     * 方法别名：inet_ntoa
     *
     * @param ip      给定的IP，如218.240.38.69
     * @param maskBit 给定的掩码位，如30
     * @return 起始IP的字符串表示
     */
    public static String getBeginIpStr(String ip, int maskBit) {
        return longToIpv4(getBeginIpLong(ip, maskBit));
    }

    /**
     * 根据 ip/掩码位 计算IP段的起始IP（Long型）
     *
     * @param ip      给定的IP，如218.240.38.69
     * @param maskBit 给定的掩码位，如30
     * @return 起始IP的长整型表示
     */
    public static Long getBeginIpLong(String ip, int maskBit) {
        return ipv4ToLong(ip) & ipv4ToLong(getMaskByMaskBit(maskBit));
    }

    /**
     * 根据 ip/掩码位 计算IP段的终止IP（字符串型）
     *
     * @param ip      给定的IP，如218.240.38.69
     * @param maskBit 给定的掩码位，如30
     * @return 终止IP的字符串表示
     */
    public static String getEndIpStr(String ip, int maskBit) {
        return longToIpv4(getEndIpLong(ip, maskBit));
    }

    /**
     * 根据子网掩码转换为掩码位
     *
     * @param mask 掩码的点分十进制表示，例如 255.255.255.0
     * @return 掩码位，例如 24
     * @throws IllegalArgumentException 子网掩码非法
     */
    public static int getMaskBitByMask(String mask) {
        Integer maskBit = MaskBit.getMaskBit(mask);
        if (maskBit == null) {
            throw new IllegalArgumentException("Invalid netmask " + mask);
        }
        return maskBit;
    }

    /**
     * 计算子网大小
     *
     * @param maskBit 掩码位
     * @param isAll   true:全量地址，false:可用地址
     * @return 地址总数
     */
    public static int countByMaskBit(int maskBit, boolean isAll) {
        //如果是可用地址的情况，掩码位小于等于0或大于等于32，则可用地址为0
        if ((!isAll) && (maskBit <= 0 || maskBit >= 32)) {
            return 0;
        }

        final int count = (int) Math.pow(2, 32 - maskBit);
        return isAll ? count : count - 2;
    }

    /**
     * 根据掩码位获取掩码
     *
     * @param maskBit 掩码位
     * @return 掩码
     */
    public static String getMaskByMaskBit(int maskBit) {
        return MaskBit.get(maskBit);
    }

    /**
     * 根据开始IP与结束IP计算掩码
     *
     * @param fromIp 开始IP
     * @param toIp   结束IP
     * @return 掩码x.x.x.x
     */
    public static String getMaskByIpRange(String fromIp, String toIp) {
        long toIpLong = ipv4ToLong(toIp);
        long fromIpLong = ipv4ToLong(fromIp);
        Assert.isTrue(fromIpLong < toIpLong, "to IP must be greater than from IP!");

        String[] fromIpSplit = StringUtils.splitToArray(fromIp, CharUtils.DOT);
        String[] toIpSplit = StringUtils.splitToArray(toIp, CharUtils.DOT);
        StringBuilder mask = new StringBuilder();
        for (int i = 0; i < toIpSplit.length; i++) {
            mask.append(255 - Integer.parseInt(toIpSplit[i]) + Integer.parseInt(fromIpSplit[i])).append(CharUtils.DOT);
        }
        return mask.substring(0, mask.length() - 1);
    }

    /**
     * 计算IP区间有多少个IP
     *
     * @param fromIp 开始IP
     * @param toIp   结束IP
     * @return IP数量
     */
    public static int countByIpRange(String fromIp, String toIp) {
        long toIpLong = ipv4ToLong(toIp);
        long fromIpLong = ipv4ToLong(fromIp);
        if (fromIpLong > toIpLong) {
            throw new IllegalArgumentException("to IP must be greater than from IP!");
        }
        int count = 1;
        int[] fromIpSplit = StringUtils.split(fromIp, CharUtils.DOT).stream().mapToInt(Integer::parseInt).toArray();
        int[] toIpSplit = StringUtils.split(toIp, CharUtils.DOT).stream().mapToInt(Integer::parseInt).toArray();
        for (int i = fromIpSplit.length - 1; i >= 0; i--) {
            count += (toIpSplit[i] - fromIpSplit[i]) * Math.pow(256, fromIpSplit.length - i - 1);
        }
        return count;
    }

    /**
     * 判断掩码是否合法
     *
     * @param mask 掩码的点分十进制表示，例如 255.255.255.0
     * @return true：掩码合法；false：掩码不合法
     */
    public static boolean isMaskValid(String mask) {
        return MaskBit.getMaskBit(mask) != null;
    }

    /**
     * 判断掩码位是否合法
     *
     * @param maskBit 掩码位，例如 24
     * @return true：掩码位合法；false：掩码位不合法
     */
    public static boolean isMaskBitValid(int maskBit) {
        return MaskBit.get(maskBit) != null;
    }

    /**
     * 判定是否为内网IPv4<br>
     * 私有IP：
     * <pre>
     * A类 10.0.0.0-10.255.255.255
     * B类 172.16.0.0-172.31.255.255
     * C类 192.168.0.0-192.168.255.255
     * </pre>
     * 当然，还有127这个网段是环回地址
     *
     * @param ipAddress IP地址
     * @return 是否为内网IP
     * @since 5.7.18
     */
    public static boolean isInnerIp(String ipAddress) {
        boolean isInnerIp;
        long ipNum = ipv4ToLong(ipAddress);

        long aBegin = ipv4ToLong("10.0.0.0");
        long aEnd = ipv4ToLong("10.255.255.255");

        long bBegin = ipv4ToLong("172.16.0.0");
        long bEnd = ipv4ToLong("172.31.255.255");

        long cBegin = ipv4ToLong("192.168.0.0");
        long cEnd = ipv4ToLong("192.168.255.255");

        isInnerIp = isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd) || LOCAL_IP.equals(ipAddress);
        return isInnerIp;
    }

    //-------------------------------------------------------------------------------- Private method start

    /**
     * 根据 ip/掩码位 计算IP段的终止IP（Long型）
     * 注：此接口返回负数，请使用转成字符串后再转Long型
     *
     * @param ip      给定的IP，如218.240.38.69
     * @param maskBit 给定的掩码位，如30
     * @return 终止IP的长整型表示
     */
    public static Long getEndIpLong(String ip, int maskBit) {
        return getBeginIpLong(ip, maskBit)
                + ~ipv4ToLong(getMaskByMaskBit(maskBit));
    }

    /**
     * 将匹配到的Ipv4地址的4个分组分别处理
     *
     * @param matcher 匹配到的Ipv4正则
     * @return ipv4对应long
     */
    private static long matchAddress(Matcher matcher) {
        long addr = 0;
        for (int i = 1; i <= 4; ++i) {
            addr |= Long.parseLong(matcher.group(i)) << 8 * (4 - i);
        }
        return addr;
    }

    /**
     * 将IPv6地址字符串转为大整数
     *
     * @param ipv6Str 字符串
     * @return 大整数, 如发生异常返回 null
     * @since 5.5.7
     * @deprecated 拼写错误，请使用{@link #ipv6ToBigInteger(String)}
     */
    @Deprecated
    public static BigInteger ipv6ToBitInteger(String ipv6Str) {
        return ipv6ToBigInteger(ipv6Str);
    }

    /**
     * 将IPv6地址字符串转为大整数
     *
     * @param ipv6Str 字符串
     * @return 大整数, 如发生异常返回 null
     * @since 5.5.7
     */
    public static BigInteger ipv6ToBigInteger(String ipv6Str) {
        try {
            InetAddress address = InetAddress.getByName(ipv6Str);
            if (address instanceof Inet6Address) {
                return new BigInteger(1, address.getAddress());
            }
        } catch (UnknownHostException ignore) {
        }
        return null;
    }

    /**
     * 将大整数转换成ipv6字符串
     *
     * @param bigInteger 大整数
     * @return IPv6字符串, 如发生异常返回 null
     * @since 5.5.7
     */
    public static String bigIntegerToIPv6(BigInteger bigInteger) {
        try {
            return InetAddress.getByAddress(bigInteger.toByteArray()).toString().substring(1);
        } catch (UnknownHostException ignore) {
            return null;
        }
    }

    /**
     * 检测本地端口可用性<br>
     * 来自org.springframework.util.SocketUtils
     *
     * @param port 被检测的端口
     * @return 是否可用
     */
    public static boolean isUsableLocalPort(int port) {
        if (false == isValidPort(port)) {
            // 给定的IP未在指定端口范围中
            return false;
        }

        // issue#765@Github, 某些绑定非127.0.0.1的端口无法被检测到
        try (ServerSocket ss = new ServerSocket(port)) {
            ss.setReuseAddress(true);
        } catch (IOException ignored) {
            return false;
        }

        try (DatagramSocket ds = new DatagramSocket(port)) {
            ds.setReuseAddress(true);
        } catch (IOException ignored) {
            return false;
        }

        return true;
    }

    /**
     * 是否为有效的端口<br>
     * 此方法并不检查端口是否被占用
     *
     * @param port 端口号
     * @return 是否有效
     */
    public static boolean isValidPort(int port) {
        // 有效端口是0～65535
        return port >= 0 && port <= PORT_RANGE_MAX;
    }

    /**
     * 查找1024~65535范围内的可用端口<br>
     * 此方法只检测给定范围内的随机一个端口，检测65535-1024次<br>
     * 来自org.springframework.util.SocketUtils
     *
     * @return 可用的端口
     * @since 4.5.4
     */
    public static int getUsableLocalPort() {
        return getUsableLocalPort(PORT_RANGE_MIN);
    }

    /**
     * 查找指定范围内的可用端口，最大值为65535<br>
     * 此方法只检测给定范围内的随机一个端口，检测65535-minPort次<br>
     * 来自org.springframework.util.SocketUtils
     *
     * @param minPort 端口最小值（包含）
     * @return 可用的端口
     * @since 4.5.4
     */
    public static int getUsableLocalPort(int minPort) {
        return getUsableLocalPort(minPort, PORT_RANGE_MAX);
    }

    /**
     * 查找指定范围内的可用端口<br>
     * 此方法只检测给定范围内的随机一个端口，检测maxPort-minPort次<br>
     * 来自org.springframework.util.SocketUtils
     *
     * @param minPort 端口最小值（包含）
     * @param maxPort 端口最大值（包含）
     * @return 可用的端口
     * @since 4.5.4
     */
    public static int getUsableLocalPort(int minPort, int maxPort) {
        final int maxPortExclude = maxPort + 1;
        int randomPort;
        for (int i = minPort; i < maxPortExclude; i++) {
            randomPort = RandomUtils.randomInt(minPort, maxPortExclude);
            if (isUsableLocalPort(randomPort)) {
                return randomPort;
            }
        }

        throw new TaraException("Could not find an available port in the range [{}, {}] after {} attempts", minPort, maxPort, maxPort - minPort);
    }

    /**
     * 获取多个本地可用端口<br>
     * 来自org.springframework.util.SocketUtils
     *
     * @param numRequested 尝试次数
     * @param minPort      端口最小值（包含）
     * @param maxPort      端口最大值（包含）
     * @return 可用的端口
     * @since 4.5.4
     */
    public static TreeSet<Integer> getUsableLocalPorts(int numRequested, int minPort, int maxPort) {
        final TreeSet<Integer> availablePorts = new TreeSet<>();
        int attemptCount = 0;
        while ((++attemptCount <= numRequested + 100) && availablePorts.size() < numRequested) {
            availablePorts.add(getUsableLocalPort(minPort, maxPort));
        }

        if (availablePorts.size() != numRequested) {
            throw new TaraException("Could not find {} available  ports in the range [{}, {}]", numRequested, minPort, maxPort);
        }

        return availablePorts;
    }

    /**
     * 获取指定名称的网卡信息
     *
     * @param name 网络接口名，例如Linux下默认是eth0
     * @return 网卡，未找到返回{@code null}
     * @since 5.0.7
     */
    public static NetworkInterface getNetworkInterface(String name) {
        Enumeration<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            return null;
        }

        NetworkInterface netInterface;
        while (networkInterfaces.hasMoreElements()) {
            netInterface = networkInterfaces.nextElement();
            if (null != netInterface && name.equals(netInterface.getName())) {
                return netInterface;
            }
        }

        return null;
    }

    /**
     * 获取本机所有网卡
     *
     * @return 所有网卡，异常返回{@code null}
     * @since 3.0.1
     */
    public static List<NetworkInterface> getNetworkInterfaces() {
        Enumeration<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            return null;
        }
        List<NetworkInterface> list = new ArrayList<>();
        while (networkInterfaces.hasMoreElements()) {
            list.add(networkInterfaces.nextElement());

        }
        return list;
    }

    /**
     * 获得本机的IPv4地址列表<br>
     * 返回的IP列表有序，按照系统设备顺序
     *
     * @return IP地址列表 {@link LinkedHashSet}
     */
    public static LinkedHashSet<String> localIpv4s() {
        final LinkedHashSet<InetAddress> localAddressList = localAddressList(t -> t instanceof Inet4Address);

        return toIpList(localAddressList);
    }

    /**
     * 获得本机的IPv6地址列表<br>
     * 返回的IP列表有序，按照系统设备顺序
     *
     * @return IP地址列表 {@link LinkedHashSet}
     * @since 4.5.17
     */
    public static LinkedHashSet<String> localIpv6s() {
        final LinkedHashSet<InetAddress> localAddressList = localAddressList(t -> t instanceof Inet6Address);

        return toIpList(localAddressList);
    }

    /**
     * 地址列表转换为IP地址列表
     *
     * @param addressList 地址{@link Inet4Address} 列表
     * @return IP地址字符串列表
     * @since 4.5.17
     */
    public static LinkedHashSet<String> toIpList(Set<InetAddress> addressList) {
        final LinkedHashSet<String> ipSet = new LinkedHashSet<>();
        for (InetAddress address : addressList) {
            ipSet.add(address.getHostAddress());
        }

        return ipSet;
    }

    /**
     * 获得本机的IP地址列表（包括Ipv4和Ipv6）<br>
     * 返回的IP列表有序，按照系统设备顺序
     *
     * @return IP地址列表 {@link LinkedHashSet}
     */
    public static LinkedHashSet<String> localIps() {
        final LinkedHashSet<InetAddress> localAddressList = localAddressList(null);
        return toIpList(localAddressList);
    }

    /**
     * 获取所有满足过滤条件的本地IP地址对象
     *
     * @param addressFilter 过滤器，null表示不过滤，获取所有地址
     * @return 过滤后的地址对象列表
     * @since 4.5.17
     */
    public static LinkedHashSet<InetAddress> localAddressList(Filter<InetAddress> addressFilter) {
        return localAddressList(null, addressFilter);
    }

    /**
     * 获取所有满足过滤条件的本地IP地址对象
     *
     * @param addressFilter          过滤器，null表示不过滤，获取所有地址
     * @param networkInterfaceFilter 过滤器，null表示不过滤，获取所有网卡
     * @return 过滤后的地址对象列表
     */
    public static LinkedHashSet<InetAddress> localAddressList(Filter<NetworkInterface> networkInterfaceFilter, Filter<InetAddress> addressFilter) {
        Enumeration<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new TaraException(e);
        }

        final LinkedHashSet<InetAddress> ipSet = new LinkedHashSet<>();

        while (networkInterfaces.hasMoreElements()) {
            final NetworkInterface networkInterface = networkInterfaces.nextElement();
            if (networkInterfaceFilter != null && !networkInterfaceFilter.accept(networkInterface)) {
                continue;
            }
            final Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                final InetAddress inetAddress = inetAddresses.nextElement();
                if (inetAddress != null && (null == addressFilter || addressFilter.accept(inetAddress))) {
                    ipSet.add(inetAddress);
                }
            }
        }

        return ipSet;
    }

    /**
     * 获取本机网卡IP地址，这个地址为所有网卡中非回路地址的第一个<br>
     * 如果获取失败调用 {@link InetAddress#getLocalHost()}方法获取。<br>
     * 此方法不会抛出异常，获取失败将返回{@code null}<br>
     * <p>
     * 参考：http://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
     *
     * @return 本机网卡IP地址，获取失败返回{@code null}
     * @since 3.0.7
     */
    public static String getLocalhostStr() {
        InetAddress localhost = getLocalhost();
        if (null != localhost) {
            return localhost.getHostAddress();
        }
        return null;
    }

    /**
     * 获取本机网卡IP地址，规则如下：
     *
     * <pre>
     * 1. 查找所有网卡地址，必须非回路（loopback）地址、非局域网地址（siteLocal）、IPv4地址
     * 2. 如果无满足要求的地址，调用 {@link InetAddress#getLocalHost()} 获取地址
     * </pre>
     * <p>
     * 此方法不会抛出异常，获取失败将返回{@code null}<br>
     * <p>
     * 见：https://github.com/dromara/hutool/issues/428
     *
     * @return 本机网卡IP地址，获取失败返回{@code null}
     * @since 3.0.1
     */
    public static InetAddress getLocalhost() {
        final LinkedHashSet<InetAddress> localAddressList = localAddressList(address -> {
            // 非loopback地址，指127.*.*.*的地址
            return !address.isLoopbackAddress()
                    // 需为IPV4地址
                    && address instanceof Inet4Address;
        });

        if (CollectionUtils.isNotEmpty(localAddressList)) {
            InetAddress address2 = null;
            for (InetAddress inetAddress : localAddressList) {
                if (!inetAddress.isSiteLocalAddress()) {
                    // 非地区本地地址，指10.0.0.0 ~ 10.255.255.255、172.16.0.0 ~ 172.31.255.255、192.168.0.0 ~ 192.168.255.255
                    return inetAddress;
                } else if (null == address2) {
                    address2 = inetAddress;
                }
            }

            if (null != address2) {
                return address2;
            }
        }

        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            // ignore
        }

        return null;
    }

    /**
     * 获得本机MAC地址
     *
     * @return 本机MAC地址
     */
    public static String getLocalMacAddress() {
        return getMacAddress(getLocalhost());
    }

    /**
     * 获得指定地址信息中的MAC地址，使用分隔符“-”
     *
     * @param inetAddress {@link InetAddress}
     * @return MAC地址，用-分隔
     */
    public static String getMacAddress(InetAddress inetAddress) {
        return getMacAddress(inetAddress, "-");
    }

    /**
     * 获得指定地址信息中的MAC地址
     *
     * @param inetAddress {@link InetAddress}
     * @param separator   分隔符，推荐使用“-”或者“:”
     * @return MAC地址，用-分隔
     */
    public static String getMacAddress(InetAddress inetAddress, String separator) {
        if (null == inetAddress) {
            return null;
        }

        final byte[] mac = getHardwareAddress(inetAddress);
        if (null != mac) {
            final StringBuilder sb = new StringBuilder();
            String s;
            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append(separator);
                }
                // 字节转换为整数
                s = Integer.toHexString(mac[i] & 0xFF);
                sb.append(s.length() == 1 ? 0 + s : s);
            }
            return sb.toString();
        }

        return null;
    }

    /**
     * 获得指定地址信息中的硬件地址
     *
     * @param inetAddress {@link InetAddress}
     * @return 硬件地址
     * @since 5.7.3
     */
    public static byte[] getHardwareAddress(InetAddress inetAddress) {
        if (null == inetAddress) {
            return null;
        }

        try {
            final NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
            if (null != networkInterface) {
                return networkInterface.getHardwareAddress();
            }
        } catch (SocketException e) {
            throw new TaraException(e);
        }
        return null;
    }

    /**
     * 获得本机物理地址
     *
     * @return 本机物理地址
     * @since 5.7.3
     */
    public static byte[] getLocalHardwareAddress() {
        return getHardwareAddress(getLocalhost());
    }


    /**
     * 创建 {@link InetSocketAddress}
     *
     * @param host 域名或IP地址，空表示任意地址
     * @param port 端口，0表示系统分配临时端口
     * @return {@link InetSocketAddress}
     * @since 3.3.0
     */
    public static InetSocketAddress createAddress(String host, int port) {
        if (StringUtils.isBlank(host)) {
            return new InetSocketAddress(port);
        }
        return new InetSocketAddress(host, port);
    }


    /**
     * 指定IP的long是否在指定范围内
     *
     * @param userIp 用户IP
     * @param begin  开始IP
     * @param end    结束IP
     * @return 是否在范围内
     */
    private static boolean isInner(long userIp, long begin, long end) {
        return (userIp >= begin) && (userIp <= end);
    }
    //-------------------------------------------------------------------------------- Private method end


    public static class MaskBit {

        /**
         * 掩码位与掩码的点分十进制的双向对应关系
         */
        private static final BiMap<Integer, String> MASK_BIT_MAP;

        static {
            MASK_BIT_MAP = HashBiMap.create();
            MASK_BIT_MAP.put(1, "128.0.0.0");
            MASK_BIT_MAP.put(2, "192.0.0.0");
            MASK_BIT_MAP.put(3, "224.0.0.0");
            MASK_BIT_MAP.put(4, "240.0.0.0");
            MASK_BIT_MAP.put(5, "248.0.0.0");
            MASK_BIT_MAP.put(6, "252.0.0.0");
            MASK_BIT_MAP.put(7, "254.0.0.0");
            MASK_BIT_MAP.put(8, "255.0.0.0");
            MASK_BIT_MAP.put(9, "255.128.0.0");
            MASK_BIT_MAP.put(10, "255.192.0.0");
            MASK_BIT_MAP.put(11, "255.224.0.0");
            MASK_BIT_MAP.put(12, "255.240.0.0");
            MASK_BIT_MAP.put(13, "255.248.0.0");
            MASK_BIT_MAP.put(14, "255.252.0.0");
            MASK_BIT_MAP.put(15, "255.254.0.0");
            MASK_BIT_MAP.put(16, "255.255.0.0");
            MASK_BIT_MAP.put(17, "255.255.128.0");
            MASK_BIT_MAP.put(18, "255.255.192.0");
            MASK_BIT_MAP.put(19, "255.255.224.0");
            MASK_BIT_MAP.put(20, "255.255.240.0");
            MASK_BIT_MAP.put(21, "255.255.248.0");
            MASK_BIT_MAP.put(22, "255.255.252.0");
            MASK_BIT_MAP.put(23, "255.255.254.0");
            MASK_BIT_MAP.put(24, "255.255.255.0");
            MASK_BIT_MAP.put(25, "255.255.255.128");
            MASK_BIT_MAP.put(26, "255.255.255.192");
            MASK_BIT_MAP.put(27, "255.255.255.224");
            MASK_BIT_MAP.put(28, "255.255.255.240");
            MASK_BIT_MAP.put(29, "255.255.255.248");
            MASK_BIT_MAP.put(30, "255.255.255.252");
            MASK_BIT_MAP.put(31, "255.255.255.254");
            MASK_BIT_MAP.put(32, "255.255.255.255");
        }

        /**
         * 根据掩码位获取掩码
         *
         * @param maskBit 掩码位
         * @return 掩码
         */
        public static String get(int maskBit) {
            return MASK_BIT_MAP.get(maskBit);
        }

        /**
         * 根据掩码获取掩码位
         *
         * @param mask 掩码的点分十进制表示，如 255.255.255.0
         * @return 掩码位，如 24；如果掩码不合法，则返回null
         * @since 5.6.5
         */
        public static Integer getMaskBit(String mask) {
            return MASK_BIT_MAP.inverse().get(mask);
        }

    }
}
