package com.sondertara.common.lang.resource;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 字符串资源，字符串做为资源
 *
 * @author huangxiaohu
 * @see CharSequenceResource
 * @since 4.1.0
 */
public class StringResource extends CharSequenceResource {
    private static final long serialVersionUID = 1L;

    /**
     * 构造，使用UTF8编码
     *
     * @param data 资源数据
     */
    public StringResource(String data) {
        super(data, null);
    }

    /**
     * 构造，使用UTF8编码
     *
     * @param data 资源数据
     * @param name 资源名称
     */
    public StringResource(String data, String name) {
        super(data, name, StandardCharsets.UTF_8);
    }

    /**
     * 构造
     *
     * @param data    资源数据
     * @param name    资源名称
     * @param charset 编码
     */
    public StringResource(String data, String name, Charset charset) {
        super(data, name, charset);
    }
}
