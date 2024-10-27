package com.sondertara.common.text.unit;

import com.sondertara.common.base.Assert;
import com.sondertara.common.text.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据大小，可以将类似于'12MB'表示转换为bytes长度的数字
 * <p>
 * 此类来自于：Spring-framework
 *
 * <pre>
 *     byte        1B     1
 *     kilobyte    1KB    1,024
 *     megabyte    1MB    1,048,576
 *     gigabyte    1GB    1,073,741,824
 *     terabyte    1TB    1,099,511,627,776
 * </pre>
 *
 * @author Sam Brannen,Stephane Nicoll
 */
public final class DataSize implements Comparable<DataSize> {

    private static final int DEFAULT_SCALE = 6;

    /**
     * The pattern for parsing.
     */
    private static final Pattern PATTERN = Pattern.compile("^([+-]?\\d+(\\.\\d+)?)([a-zA-Z]{0,2})$");

    /**
     * Bytes per Kilobyte(KB).
     */
    public static final long BYTES_PER_KB = 1024;

    /**
     * Bytes per Megabyte(MB).
     */
    public static final long BYTES_PER_MB = BYTES_PER_KB * 1024;

    /**
     * Bytes per Gigabyte(GB).
     */
    public static final long BYTES_PER_GB = BYTES_PER_MB * 1024;

    /**
     * Bytes per Terabyte(TB).
     */
    public static final long BYTES_PER_TB = BYTES_PER_GB * 1024;


    /**
     * The number of bytes in a kilobyte.
     */
    public static final BigInteger ONE_KB_BI = BigInteger.valueOf(BYTES_PER_KB);

    /**
     * The number of bytes in a megabyte.
     */
    public static final long ONE_MB = BYTES_PER_MB;

    /**
     * The number of bytes in a megabyte.
     */
    public static final BigInteger ONE_MB_BI = ONE_KB_BI.multiply(ONE_KB_BI);

    /**
     * The number of bytes in a gigabyte.
     */
    public static final long ONE_GB = BYTES_PER_GB;

    /**
     * The number of bytes in a gigabyte.
     */
    public static final BigInteger ONE_GB_BI = ONE_KB_BI.multiply(ONE_MB_BI);

    /**
     * The number of bytes in a terabyte.
     */
    public static final long ONE_TB = BYTES_PER_TB;

    /**
     * The number of bytes in a terabyte.
     */
    public static final BigInteger ONE_TB_BI = ONE_KB_BI.multiply(ONE_GB_BI);

    /**
     * The number of bytes in a petabyte.
     */
    public static final long ONE_PB = BYTES_PER_KB * ONE_TB;

    /**
     * The number of bytes in a petabyte.
     */
    public static final BigInteger ONE_PB_BI = ONE_KB_BI.multiply(ONE_TB_BI);

    /**
     * The number of bytes in an exabyte.
     */
    public static final long ONE_EB = BYTES_PER_KB * ONE_PB;

    /**
     * The number of bytes in an exabyte.
     */
    public static final BigInteger ONE_EB_BI = ONE_KB_BI.multiply(ONE_PB_BI);

    /**
     * The number of bytes in a zettabyte.
     */
    public static final BigInteger ONE_ZB = BigInteger.valueOf(BYTES_PER_KB).multiply(BigInteger.valueOf(ONE_EB));

    /**
     * The number of bytes in a yottabyte.
     */
    public static final BigInteger ONE_YB = ONE_KB_BI.multiply(ONE_ZB);

    public static String byteCountToDisplaySize(final BigInteger size) {
        String displaySize;

        if (size.divide(ONE_EB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_EB_BI)) + " EB";
        } else if (size.divide(ONE_PB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_PB_BI)) + " PB";
        } else if (size.divide(ONE_TB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_TB_BI)) + " TB";
        } else if (size.divide(ONE_GB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_GB_BI)) + " GB";
        } else if (size.divide(ONE_MB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_MB_BI)) + " MB";
        } else if (size.divide(ONE_KB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_KB_BI)) + " KB";
        } else {
            displaySize = String.valueOf(size) + " bytes";
        }
        return displaySize;
    }

    public static String byteCountToDisplaySize(final long size) {
        return byteCountToDisplaySize(BigInteger.valueOf(size));
    }

    /**
     * bytes长度
     */
    private final BigInteger bytes;

    /**
     * 构造
     *
     * @param bytes 长度
     */
    private DataSize(BigInteger bytes) {
        this.bytes = bytes;
    }

    /**
     * 获得对应bytes的DataSize
     *
     * @param bytes bytes大小，可正可负
     * @return this
     */
    public static DataSize ofBytes(long bytes) {
        return new DataSize(BigInteger.valueOf(bytes));
    }

    /**
     * 获得对应kilobytes的DataSize
     *
     * @param kilobytes kilobytes大小，可正可负
     * @return a DataSize
     */
    public static DataSize ofKilobytes(long kilobytes) {
        return new DataSize(BigInteger.valueOf(kilobytes).multiply(BigInteger.valueOf(BYTES_PER_KB)));
    }

    /**
     * 获得对应megabytes的DataSize
     *
     * @param megabytes megabytes大小，可正可负
     * @return a DataSize
     */
    public static DataSize ofMegabytes(long megabytes) {
        return new DataSize(BigInteger.valueOf(megabytes).multiply(BigInteger.valueOf(BYTES_PER_MB)));
    }

    /**
     * 获得对应gigabytes的DataSize
     *
     * @param gigabytes gigabytes大小，可正可负
     * @return a DataSize
     */
    public static DataSize ofGigabytes(long gigabytes) {
        return new DataSize(BigInteger.valueOf(gigabytes).multiply(BigInteger.valueOf(BYTES_PER_GB)));
    }

    /**
     * 获得对应terabytes的DataSize
     *
     * @param terabytes terabytes大小，可正可负
     * @return a DataSize
     */
    public static DataSize ofTerabytes(long terabytes) {
        return new DataSize(BigInteger.valueOf(terabytes).multiply(BigInteger.valueOf(BYTES_PER_TB)));
    }

    /**
     * 获得指定{@link DataUnits}对应的DataSize
     *
     * @param amount 大小
     * @param unit   数据大小单位，null表示默认的BYTES
     * @return DataSize
     */
    public static DataSize of(long amount, DataUnits unit) {
        if (null == unit) {
            unit = DataUnits.BYTES;
        }
        return new DataSize(BigInteger.valueOf(amount).multiply(unit.size().toBytes()));
    }

    /**
     * 获得指定{@link DataUnits}对应的DataSize
     *
     * @param amount 大小
     * @param unit   数据大小单位，null表示默认的BYTES
     * @return DataSize
     */
    public static DataSize of(BigDecimal amount, DataUnits unit) {
        if (null == unit) {
            unit = DataUnits.BYTES;
        }
        return new DataSize(amount.multiply(new BigDecimal(unit.size().toBytes())).toBigInteger());
    }

    /**
     * 获取指定数据大小文本对应的DataSize对象，如果无单位指定，默认获取{@link DataUnits#BYTES}
     * <p>
     * 例如：
     *
     * <pre>
     * "12KB" -- parses as "12 kilobytes"
     * "5MB"  -- parses as "5 megabytes"
     * "20"   -- parses as "20 bytes"
     * </pre>
     *
     * @param text the text to parse
     * @return the parsed DataSize
     * @see #parse(CharSequence, DataUnits)
     */
    public static DataSize parse(CharSequence text) {
        return parse(text, null);
    }

    /**
     * Obtain a DataSize from a text string such as {@code 12MB} using
     * the specified default {@link DataUnits} if no unit is specified.
     * <p>
     * The string starts with a number followed optionally by a unit matching one of
     * the
     * supported {@linkplain DataUnits suffixes}.
     * <p>
     * Examples:
     *
     * <pre>
     * "12KB" -- parses as "12 kilobytes"
     * "5MB"  -- parses as "5 megabytes"
     * "20"   -- parses as "20 kilobytes" (where the {@code
     * defaultUnit
     * } is {@link DataUnits#KILOBYTES})
     * </pre>
     *
     * @param text        the text to parse
     * @param defaultUnit 默认的数据单位
     * @return the parsed DataSize
     */
    public static DataSize parse(CharSequence text, DataUnits defaultUnit) {
        Assert.notNull(text, "Text must not be null");
        try {
            final Matcher matcher = PATTERN.matcher(text);
            Assert.state(matcher.matches(), "Does not match data size pattern");

            final DataUnits unit = determineDataUnit(matcher.group(3), defaultUnit);
            return DataSize.of(new BigDecimal(matcher.group(1)), unit);
        } catch (Exception ex) {
            throw new IllegalArgumentException("'" + text + "' is not a valid data size", ex);
        }
    }

    /**
     * 决定数据单位，后缀不识别时使用默认单位
     *
     * @param suffix      后缀
     * @param defaultUnit 默认单位
     * @return {@link DataUnits}
     */
    private static DataUnits determineDataUnit(String suffix, DataUnits defaultUnit) {
        DataUnits defaultUnitToUse = (defaultUnit != null ? defaultUnit : DataUnits.BYTES);
        return (StringUtils.isNotEmpty(suffix) ? DataUnits.fromSuffix(suffix) : defaultUnitToUse);
    }

    /**
     * 是否为负数，不包括0
     *
     * @return 负数返回true，否则false
     */
    public boolean isNegative() {
        return this.bytes.compareTo(BigInteger.ZERO) < 0;
    }

    /**
     * 返回bytes大小
     *
     * @return bytes大小
     */
    public BigInteger toBytes() {
        return this.bytes;
    }

    /**
     * 返回KB大小
     *
     * @return KB大小
     */
    public long toKilobytes() {
        return this.bytes.divide(BigInteger.valueOf(BYTES_PER_KB)).longValue();
    }

    /**
     * 返回MB大小
     *
     * @return MB大小
     */
    public BigDecimal toMegabytes() {
        return new BigDecimal(this.bytes).divide(BigDecimal.valueOf(BYTES_PER_MB), DEFAULT_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 返回GB大小
     *
     * @return GB大小
     */
    public BigDecimal toGigabytes() {
        return new BigDecimal(this.bytes).divide(BigDecimal.valueOf(BYTES_PER_GB), DEFAULT_SCALE, RoundingMode.HALF_UP);

    }

    /**
     * 返回TB大小
     *
     * @return TB大小
     */
    public BigDecimal toTerabytes() {
        return new BigDecimal(this.bytes).divide(BigDecimal.valueOf(BYTES_PER_TB), DEFAULT_SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public int compareTo(DataSize other) {
        return this.bytes.compareTo(other.bytes);
    }

    @Override
    public String toString() {
        return String.format("%dB", this.bytes);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        DataSize otherSize = (DataSize) other;
        return (this.bytes == otherSize.bytes);
    }

    @Override
    public int hashCode() {
        return this.bytes.hashCode();
    }

    public static String format(long size) {
        if (size <= 0) {
            return "0";
        }
        int digitGroups = Math.min(DataUnits.UNIT_NAMES.length - 1, (int) (Math.log10(size) / Math.log10(1024)));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " "
                + DataUnits.UNIT_NAMES[digitGroups];
    }

}
