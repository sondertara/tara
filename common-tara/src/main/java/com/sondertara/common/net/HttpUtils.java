package com.sondertara.common.net;

import com.sondertara.common.concurrent.threadpool.ThreadPoolConfigure;
import com.sondertara.common.concurrent.threadpool.ThreadPoolFactory;
import com.sondertara.common.exception.IORuntimeException;
import com.sondertara.common.io.IOUtils;
import com.sondertara.common.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * 封装HttpURLConnection开箱即用
 * Create by yster@foxmail.com 2018/9/10/010 19:17
 *
 * @author huangxiaohu
 */
public class HttpUtils {
    static {
        if (null == System.getProperty("https.protocols")) {
            System.setProperty("https.protocols", "SSLv2Hello, SSLv3, TLSv1, TLSv1.2");
        }
    }


    private Charset charset = StandardCharsets.UTF_8;
    private int readTimeout = 32000;
    private int connectTimeout = 10000;
    private String method = "GET";
    private boolean doInput = true;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String data = null;

    private final Map<String, Object> params = new LinkedHashMap<>();

    private String url;

    /**
     * 实例化对象
     */
    public static HttpUtils connect(String url) throws IOException {

        return new HttpUtils(url);
    }

    /**
     * 禁止new实例
     */
    private HttpUtils(String url) {
        this.url = url;
    }


    /**
     * 设置读去超时时间/ms
     *
     * @param timeout
     */
    public HttpUtils setReadTimeout(int timeout) {
        this.readTimeout = timeout;
        return this;
    }

    /**
     * 设置链接超时时间/ms
     *
     * @param timeout
     */
    public HttpUtils setConnectTimeout(int timeout) {
        this.connectTimeout = timeout;
        return this;
    }

    /**
     * 设置请求方式
     *
     * @param method
     */
    public HttpUtils method(String method) {
        this.method = method;
        return this;
    }

    /**
     * 添加Headers
     *
     * @param map
     */
    public HttpUtils headers(Map<String, String> map) {
        headers.putAll(map);
        return this;
    }

    /**
     * 是否接受输入流
     * 默认true
     *
     * @param is
     */
    public HttpUtils setDoInput(boolean is) {
        this.doInput = is;
        return this;
    }

    /**
     * 设置请求响应的编码
     */
    public HttpUtils charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public HttpUtils params(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public HttpUtils form(Map<String, Object> form) {
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            sj.add(entry.getKey() + "=" + entry.getValue());
        }
        this.data = sj.toString();
        return this;
    }


    public HttpUtils json(String json) {
        headers.put("Content-Type", "application/json");
        this.data = json;
        return this;
    }

    public HttpUtils json(Object json) {
        headers.put("Content-Type", "application/json");
        this.data = JsonUtils.toJsonString(json);
        return this;
    }

    public void executeAsync(Consumer<Response> consumer) {
        CompletableFuture.runAsync(() -> consumer.accept(execute()), ThreadPoolFactory.getInstance().getOrCreatePool(ThreadPoolConfigure.builder().key("Tara-Http").group("TARA_HTTP").allowCoreThreadTimeOut(true).build()));
    }

    /**
     * 发起请求
     */
    public Response execute() {
        try {
            HttpURLConnection connection;
            if (!params.isEmpty()) {
                StringJoiner sj = new StringJoiner("&");
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    sj.add(entry.getKey() + "=" + entry.getValue());
                }
                url = url + "?" + sj;
            }
            connection = (HttpURLConnection) new URL(url).openConnection();
            //添加请求头
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            //设置读去超时时间为10秒
            connection.setReadTimeout(readTimeout);
            //设置链接超时为10秒
            connection.setConnectTimeout(connectTimeout);
            //设置请求方式,GET,POST
            connection.setRequestMethod(method.toUpperCase());
            //接受输入流
            connection.setDoInput(doInput);


            //写入参数
            if (data != null && !"GET".equalsIgnoreCase(method)) {
                //启动输出流，当需要传递参数时需要开启
                connection.setDoOutput(true);
                //添加请求参数，注意：如果是GET请求，参数要写在URL中
                OutputStream output = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, charset));
                //写入参数 用&分割。
                writer.write(data);
                writer.flush();
                writer.close();
            }
            //发起请求
            connection.connect();
            return new Response(connection);
        } catch (IOException e) {
            throw new IORuntimeException(e.getCause());
        }
    }


    @Slf4j
    public static class Response {

        private final HttpURLConnection connection;

        private final int responseCode;


        public Response(HttpURLConnection connection) {
            this.connection = connection;
            this.responseCode = getRspCode();
        }


        private int getRspCode() {
            try {
                return connection.getResponseCode();
            } catch (IOException e) {
                throw new IORuntimeException(e.getCause());
            }
        }

        public boolean success() {
            return this.responseCode >= 200 && this.responseCode < 300;
        }


        /**
         * 获取响应字符串
         */
        public String getBody(Charset... charsets) {
            //设置编码
            Charset charset = StandardCharsets.UTF_8;
            if (charsets.length > 0) {
                charset = charsets[0];
            }
            InputStream inputStream = null;
            try {
                //读取输入流
                inputStream = connection.getInputStream();
                if (null == inputStream) {
                    throw new IORuntimeException("No content from http connection");
                }
                return IOUtils.copyToString(inputStream, charset);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            } finally {
                if (null != inputStream) {
                    try {
                        inputStream.close();
                    } catch (IOException ignored) {

                    }
                }

            }

        }

        public <T> T getBodyObject(Class<T> tClass, Charset... charsets) throws IOException {
            String body = getBody(charsets);
            if (success()) {
                return JsonUtils.toJavaObject(body, tClass);
            }
            log.warn("Request was failed,response str:{}", body);
            return null;

        }

    }

}