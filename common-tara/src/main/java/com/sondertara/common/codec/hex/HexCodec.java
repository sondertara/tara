package com.sondertara.common.codec.hex;


import com.sondertara.common.codec.BinaryCodec;
import com.sondertara.common.codec.CodecException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Converts hexadecimal StringUtils. The charset used for certain operation can be set, the default is set in
 * <p>
 * This class is thread-safe.
 */
public class HexCodec implements BinaryCodec {

    /**
     * encode时，把任意的bytes 转换成 16进制字符串后，再用该 字符集进行编码
     * decode时，把任意的bytes 用该字符集解码成 16进制字符数组，再转为二进制 也就是 byte[]
     */
    private final Charset charset;

    /**
     * Creates a new codec with the default charset name {@link StandardCharsets#UTF_8}
     */
    public HexCodec() {
        // use default encoding
        this.charset = StandardCharsets.UTF_8;
    }

    /**
     * Creates a new codec with the given Charset.
     *
     * @param charset the charset.
     *      */
    public HexCodec(final Charset charset) {
        this.charset = charset;
    }

    /**
     * Creates a new codec with the given charset name.
     *
     * @param charsetName the charset name.
     * @throws java.nio.charset.UnsupportedCharsetException If the named charset is unavailable
     *      */
    public HexCodec(final String charsetName) {
        this(Charset.forName(charsetName));
    }

    /**
     * Converts an array of character bytes representing hexadecimal values into an array of bytes of those same values.
     * The returned array will be half the length of the passed array, as it takes two characters to represent any given
     * byte. An exception is thrown if the passed char array has an odd number of elements.
     *
     * @param array An array of character bytes containing hexadecimal digits
     * @return A byte array containing binary data decoded from the supplied byte array (representing characters).
     * @throws CodecException Thrown if an odd number of characters is supplied to this function
     * @see Hex#decodeHex(char[])
     */
    @Override
    public byte[] decode(final byte[] array) throws CodecException {
        return Hex.decodeHex(new String(array, getCharset()).toCharArray());
    }

    /**
     * Converts an array of bytes into an array of bytes for the characters representing the hexadecimal values of each
     * byte in order. The returned array will be double the length of the passed array, as it takes two characters to
     * represent any given byte.
     * <p>
     * The conversion from hexadecimal characters to the returned bytes is performed with the charset named by
     * {@link #getCharset()}.
     * </p>
     *
     * @param array a byte[] to convert to Hex characters
     * @return A byte[] containing the bytes of the hexadecimal characters
     * @see Hex#encodeHex(byte[])
     *      */
    @Override
    public byte[] encode(final byte[] array) {
        return Hex.encodeHexString(array).getBytes(this.getCharset());
    }

    /**
     * Gets the charset.
     *
     * @return the charset.
     *      */
    public Charset getCharset() {
        return this.charset;
    }

    /**
     * Gets the charset name.
     *
     * @return the charset name.
     *      */
    public String getCharsetName() {
        return this.charset.name();
    }

    /**
     * Returns a string representation of the object, which includes the charset name.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return super.toString() + "[charsetName=" + this.charset + "]";
    }
}
