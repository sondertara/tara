package com.sondertara.common.io.file;

import com.sondertara.common.base.Emptys;
import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.base.Valid;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.hash.HashCodeBuilder;
import com.sondertara.common.math.Radixs;
import com.sondertara.common.regex.RegexUtils;
import com.sondertara.common.regex.Regexp;

import java.util.function.Consumer;


/**
 * @see FilePermission
 */
@Deprecated
public class UnixFileAccessMode {
    private static final Regexp PATTERN = RegexUtils.createRegexp("^[01]+$");
    private static final char TRUE = '1';
    private String binaryMode;

    public UnixFileAccessMode(String binaryMode) {
        setBinaryMode(binaryMode);
    }

    public String getBinaryMode() {
        return binaryMode;
    }

    public void setBinaryMode(String binaryMode) {
        Valid.notNull(binaryMode);
        Valid.isTrue(binaryMode.length() >= 9);
        Valid.isTrue(PATTERN.matcher(binaryMode).matches());
        this.binaryMode = binaryMode;
    }

    public boolean isReadable() {
        return binaryMode.charAt(0) == TRUE;
    }

    public boolean isWritable() {
        return binaryMode.charAt(1) == TRUE;
    }

    public boolean isExecutable() {
        return binaryMode.charAt(2) == TRUE;
    }

    public boolean isReadonly() {
        return !isWritable() || !isExecutable();
    }

    public boolean isGroupReadable() {
        return binaryMode.charAt(3) == TRUE;
    }

    public boolean isGroupWritable() {
        return binaryMode.charAt(4) == TRUE;
    }

    public boolean isGroupExecutable() {
        return binaryMode.charAt(5) == TRUE;
    }

    public boolean isGroupReadonly() {
        return !isGroupWritable() || !isGroupExecutable();
    }

    public boolean isOtherReadable() {
        return binaryMode.charAt(6) == TRUE;
    }

    public boolean isOtherWritable() {
        return binaryMode.charAt(7) == TRUE;
    }

    public boolean isOtherExecutable() {
        return binaryMode.charAt(8) == TRUE;
    }

    public boolean isOtherReadonly() {
        return !isOtherWritable() || !isOtherExecutable();
    }

    public boolean isOnlyOwnerExecutable() {
        return isExecutable() && (!isGroupExecutable() && !isOtherExecutable());
    }

    /**
     * 参数必须是 3位数，且是 8进制
     *
     * @param octal octal string
     */
    public static String toBinaryMode(String octal) {
        Valid.isTrue(Emptys.isNotEmpty(octal));
        Valid.isTrue(octal.length() == 3, "the expected length is 3");
        Valid.isTrue(Radixs.isOctal(octal), "the expect argument is octal string");
        final StringBuilder modeString = new StringBuilder(9);
        CollectionUtils.forEach(octal.toCharArray(), new Consumer<Character>() {
            @Override
            public void accept(Character c) {
                modeString.append(Radixs.toBinary(c - 48));
            }
        });
        return modeString.toString();
    }

    /**
     * 参数必须是 3位数，且是 8进制
     *
     * @param octal octal string
     */
    public static UnixFileAccessMode create(String octal) {
        return new UnixFileAccessMode(toBinaryMode(octal));
    }

    /**
     * @param decimal 是进制数字
     */
    public static UnixFileAccessMode create(int decimal) {
        return create(Radixs.toOtc(decimal));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnixFileAccessMode that = (UnixFileAccessMode) o;

        return ObjectUtils.equals(binaryMode, that.binaryMode);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().with(binaryMode).build();
    }

    @Override
    public String toString() {
        return "binaryMode:" + binaryMode + ", octalMode: " + Radixs.binaryToOctal(binaryMode);
    }
}
