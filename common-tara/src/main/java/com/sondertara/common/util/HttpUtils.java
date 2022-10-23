package com.sondertara.common.util;

import com.alibaba.fastjson2.JSON;
import com.sondertara.common.exception.TaraException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author huangxiaohu
 */

public class HttpUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static final int CONN_TIME_OUT = 60000;
    private static final int READ_TIME_OUT = 60000;
    private static final int EXECUTION_COUNT = 3;
    private static final int RETRY_INTERVAL = 10000;

    private static final RequestConfig REQUEST_CONFIG_TIME_OUT = RequestConfig.custom()
            .setSocketTimeout(CONN_TIME_OUT)
            .setConnectTimeout(CONN_TIME_OUT)
            .setConnectionRequestTimeout(CONN_TIME_OUT)
            .build();

    public static String doGet(String url, Map<String, Object> param) throws TaraException {
        StringBuffer buffer = new StringBuffer(url);
        if (url.endsWith("/")) {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        buffer.append("?");
        buffer.append(getParams(param));
        StringBuffer result = new StringBuffer();
        BufferedReader in = null;
        try {

            logger.info("data:{}", buffer);
            URL realUrl = new URL(buffer.toString());
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");

            // 设置超时时间
            connection.setConnectTimeout(CONN_TIME_OUT);
            connection.setReadTimeout(READ_TIME_OUT);

            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            throw new TaraException(e);
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                logger.error("释放资源异常", e2);
            }
        }
        return result.toString();
    }

    public static String doClientGet(String url, Map<String, Object> params, boolean isHttps) {
        HttpResponse response = null;
        String apiUrl = url;
        StringBuffer param = new StringBuffer();
        int i = 0;
        for (String key : params.keySet()) {
            if (i == 0) {
                param.append("?");
            } else {
                param.append("&");
            }
            param.append(key).append("=").append(params.get(key));
            i++;
        }
        apiUrl += param;
        String result = null;
        CloseableHttpClient httpclient;
        if (isHttps) {
            httpclient = createSSLClientDefault();
        } else {
            httpclient = HttpClients.createDefault();
        }
        try {
            HttpGet httpGet = new HttpGet(apiUrl);
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, "UTF-8");
            }
        } catch (IOException e) {
            logger.error("HttpClientUtil-doGet,error:", e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    logger.error("HttpClientUtil-doGet,error:", e);
                }
            }
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    logger.error("httpclient close error:", e);
                }
            }
        }
        return result;
    }

    public static String sendPostJson(String url, Map<String, Object> param) throws TaraException {
        System.out.println(url);
        System.out.println(JSON.toJSONString(param));

        return sendPostJson(url, JSON.toJSONString(param));
    }

    public static String sendPostJson(String url, String jsonParam) throws TaraException {
        logger.info("post url ==>[{}]", url);
        logger.info("post param ==>[{}]", jsonParam);
        OutputStream out = null;
        InputStream in = null;
        HttpURLConnection conn = null;
        StringBuffer result = new StringBuffer();
        try {
            URL realUrl = new URL(url);
            // open connect
            conn = (HttpURLConnection) realUrl.openConnection();
            // set property
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // set timeout
            conn.setConnectTimeout(CONN_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();

            // send
            out = conn.getOutputStream();
            out.write(jsonParam.getBytes(StandardCharsets.UTF_8));
            // flush
            out.flush();
            // read response
            if (conn.getResponseCode() < HttpStatus.SC_BAD_REQUEST) {
                in = conn.getInputStream();
            } else {
                in = conn.getErrorStream();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result);
            return result.toString();

        } catch (Exception e) {
            throw new TaraException(e);
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                throw new TaraException(e);
            }

            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static StringBuffer getParams(Map<String, Object> param) throws TaraException {
        StringBuffer buffer = new StringBuffer();
        try {
            if (param != null && !param.isEmpty()) {
                for (Entry<String, Object> entry : param.entrySet()) {
                    buffer.append(entry.getKey()).append("=")
                            .append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"))
                            .append("&");
                }
                buffer.deleteCharAt(buffer.length() - 1);
            }
        } catch (UnsupportedEncodingException e) {
            throw new TaraException(e);
        }
        return buffer;
    }

    public static String sendSSLPost(String url, String params, String contentType) throws TaraException {
        CloseableHttpClient httpClient = createSSLClientDefault();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        String httpStr = null;
        try {
            StringEntity jsonParams = new StringEntity(params, "utf-8");
            httpPost.addHeader("content-type", contentType);
            httpPost.setEntity(jsonParams);
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(CONN_TIME_OUT)
                    .setConnectTimeout(CONN_TIME_OUT)
                    .setConnectionRequestTimeout(CONN_TIME_OUT).build();
            httpPost.setConfig(defaultRequestConfig);
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                httpStr = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (Exception e) {
            throw new TaraException(e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    logger.error("HttpClientUtil-sendSSLPost,error:", e);
                }
            }
        }
        return httpStr;
    }

    public static String sendSSLPost2(String url, Map<String, Object> params) throws TaraException {
        CloseableHttpClient httpClient = createSSLClientDefault();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        String httpStr = null;
        try {
            List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
            for (String key : params.keySet()) {
                paramsList.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(paramsList, "utf-8"));
            httpPost.setConfig(REQUEST_CONFIG_TIME_OUT);
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                httpStr = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (Exception e) {
            throw new TaraException(e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    logger.error("HttpClientUtil-sendSSLPost2,error:", e);
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    logger.error("httpclient close error:", e);
                }
            }
        }
        return httpStr;
    }

    public static String doClientGet(String url, Map<String, Object> params) throws TaraException {
        String result = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            if (params != null) {
                if (url.contains("?")) {
                    url += "&" + getParams(params);
                } else {
                    url += "?" + getParams(params);
                }
            }
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

            // 判断网络连接状态码是否正常(0--200都数正常)
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                System.out.println("GET Response Status: " + httpResponse.getStatusLine().getStatusCode());
                System.out.println("httpClient request result:" + result);
            }
            HttpEntity resEntity = httpResponse.getEntity();
            if (resEntity != null) {
                result = EntityUtils.toString(resEntity, "utf-8");
            }
            httpClient.close();
        } catch (Exception e) {
            logger.error("HttpClientUtil-sendClientGet,error:", e);
            throw new TaraException(e);
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    logger.error("httpclient close error:", e);
                }
            }
        }
        return result;
    }

    public static String doClientPost(String url, Map<String, Object> params) throws TaraException {
        String result = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            if (params != null && !params.isEmpty()) {
                // 设置参数
                List<NameValuePair> list = new ArrayList<>();
                Iterator<Entry<String, Object>> iterator = params.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, Object> elem = iterator.next();
                    if (elem.getValue() != null) {
                        list.add(new BasicNameValuePair(elem.getKey(), String.valueOf(elem.getValue())));
                    }

                }
                if (list.size() > 0) {
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");
                    httpPost.setEntity(entity);
                }
            }

            httpPost.setConfig(REQUEST_CONFIG_TIME_OUT);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "utf-8");
                }
                // 判断网络连接状态码是否正常(0--200都数正常)
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    System.out.println("GET Response Status: " + response.getStatusLine().getStatusCode());
                    System.out.println("httpClient request result:" + result);
                }
            }
        } catch (Exception e) {
            logger.error("HttpClientUtil-sendClientPost,error:", e);
            throw new TaraException(e);
        }
        return result;
    }

    /**
     * 获取httpClient对象，包含重试机制
     *
     * @return httClient
     */
    private static CloseableHttpClient getHttpClient() {

        ServiceUnavailableRetryStrategy serviceUnavailableRetryStrategy = new CustomServiceUnavailableRetryStrategy.Builder()
                .executionCount(EXECUTION_COUNT)
                .retryInterval(RETRY_INTERVAL)
                .build();
        return HttpClientBuilder.create()
                .setServiceUnavailableRetryStrategy(serviceUnavailableRetryStrategy)
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .setDefaultRequestConfig(RequestConfig.DEFAULT)
                .setRetryHandler(getHttpRequestRetryHandler())
                .build();
    }

    public static String postForm(String url, Map<String, Object> param) throws TaraException {

        OutputStream out = null;
        InputStream in = null;
        StringBuffer result = new StringBuffer();
        HttpURLConnection conn = null;
        try {
            StringBuffer buffer = getParams(param);
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            // 设置超时时间
            conn.setConnectTimeout(CONN_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = conn.getOutputStream();
            // 发送请求参数
            out.write(buffer.toString().getBytes(StandardCharsets.UTF_8));
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应

            if (conn.getResponseCode() < HttpStatus.SC_BAD_REQUEST) {
                in = conn.getInputStream();
            } else {
                in = conn.getErrorStream();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result);
            return result.toString();

        } catch (Exception e) {
            throw new TaraException(e);
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException ex) {
                logger.error("释放资源异常", ex);
            }
        }
    }

    /**
     * 请求异常,获取重试控制器
     *
     * @return retry
     */
    private static HttpRequestRetryHandler getHttpRequestRetryHandler() {
        return new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount > EXECUTION_COUNT) {
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {
                    logger.error("没有响应异常");
                    return true;
                } else if (exception instanceof ConnectTimeoutException) {
                    logger.error("连接超时，重试");
                    return true;
                } else if (exception instanceof SSLHandshakeException) {
                    logger.error("本地证书异常");
                    return false;
                } else if (exception instanceof InterruptedIOException) {
                    logger.error("IO中断异常");
                    return false;
                } else if (exception instanceof UnknownHostException) {
                    logger.error("找不到服务器异常");
                    return false;
                } else if (exception instanceof SSLException) {
                    logger.error("SSL异常");
                    return false;
                } else if (exception instanceof HttpHostConnectException) {
                    logger.error("主机连接异常");
                    return false;
                } else if (exception instanceof SocketException) {
                    logger.error("socket异常");
                    return false;
                } else {
                    logger.error("未记录的请求异常：" + exception.getClass());
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求被认为是幂等的，那么就重试。即重复执行不影响程序其他效果的
                return !(request instanceof HttpEntityEnclosingRequest);
            }
        };
    }

    /**
     * 请求异常,重试机制
     *
     * @return HttpRequestRetryHandler
     */
    private static HttpRequestRetryHandler getRetryHandler() {

        return (e, retryTimes, httpContext) -> {
            if (retryTimes > EXECUTION_COUNT) {
                return false;
            }
            if (e instanceof UnknownHostException || e instanceof ConnectTimeoutException
                    || !(e instanceof SSLException) || e instanceof NoHttpResponseException) {
                return true;
            }

            HttpClientContext clientContext = HttpClientContext.adapt(httpContext);
            HttpRequest request = clientContext.getRequest();
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
            // 如果请求被认为是幂等的，那么就重试。即重复执行不影响程序其他效果的
            return idempotent;
        };
    }

    private static CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
            logger.error("HttpClientUtil-createSSLClientDefault,error:", e);
        }
        return HttpClients.createDefault();
    }

    static class CustomServiceUnavailableRetryStrategy implements ServiceUnavailableRetryStrategy {

        private final int executionCount;
        private final long retryInterval;

        CustomServiceUnavailableRetryStrategy(Builder builder) {
            this.executionCount = builder.executionCount;
            this.retryInterval = builder.retryInterval;
        }

        /**
         * retry逻辑
         */
        @Override
        public boolean retryRequest(HttpResponse response, int executionCount,
                HttpContext context) {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK && executionCount <= this.executionCount) {
                System.out.println(String.format("响应码为:%s,需要重新请求", response.getStatusLine().getStatusCode()));

                return true;
            } else {
                return false;
            }
        }

        /**
         * retry间隔时间
         */
        @Override
        public long getRetryInterval() {
            return this.retryInterval;
        }

        /**
         * 获取重试控制器
         */

        public static final class Builder {
            private int executionCount;
            private long retryInterval;

            public Builder() {
                executionCount = 3;
                retryInterval = 5000;
            }

            public Builder executionCount(int executionCount) {
                this.executionCount = executionCount;
                return this;
            }

            public Builder retryInterval(long retryInterval) {
                this.retryInterval = retryInterval;
                return this;
            }

            public CustomServiceUnavailableRetryStrategy build() {
                return new CustomServiceUnavailableRetryStrategy(this);
            }
        }

    }
}
