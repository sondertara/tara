package org.cherubim.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;

/**
 * 支持HttpBasicAuth的HTTP工具类
 *
 * @author fanqijun
 * @date 2018-12-05 14:18
 */
@Slf4j
public class HttpBasicAuthUtil {

    private HttpBasicAuthUtil() {
    }

    /**
     * 默认编码
     */
    private static final String ENCODING = "UTF-8";

    /**
     * 超时时间
     */
    private static final int CONN_TIME_OUT = 60000;

    /**
     * 客户端对象
     */
    private static final CloseableHttpClient CLOSEABLE_HTTP_CLIENT = HttpClients.custom()
        .setDefaultCredentialsProvider(new BasicCredentialsProvider()).build();

    /**
     * 超时配置
     */
    private static final RequestConfig REQUEST_CONFIG_TIME_OUT = RequestConfig.custom()
        .setSocketTimeout(CONN_TIME_OUT)
        .setConnectTimeout(CONN_TIME_OUT)
        .setConnectionRequestTimeout(CONN_TIME_OUT)
        .build();

    /**
     * 发送post请求
     *
     * @param url 请求地址
     * @param jsonParam 请求参数(JSON格式)
     * @return 请求结果
     */
    public static String doPost(String url, String jsonParam, String basicAuthKey) {

        String result = "";
        HttpPost httpPost;
        HttpResponse httpResponse;
        HttpEntity httpEntity;

        try {

            // 请求参数
            StringEntity paramEntity = new StringEntity(jsonParam, ENCODING);

            httpPost = new HttpPost(url);
            httpPost.setEntity(paramEntity);

            httpPost.setConfig(REQUEST_CONFIG_TIME_OUT);

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Cache-Control", "no-cache");
            httpPost.setHeader("Authorization",
                "Basic " + new String(Base64.encodeBase64(basicAuthKey.getBytes(ENCODING))));

            log.info("请求详情:{}", httpPost);

            httpResponse = CLOSEABLE_HTTP_CLIENT.execute(httpPost);

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                httpEntity = httpResponse.getEntity();
                if (httpEntity != null) {
                    result = EntityUtils.toString(httpEntity, ENCODING);
                }
            } else {
                log.error("请求不成功");
                System.out.println(EntityUtils.toString(httpResponse.getEntity(), ENCODING));
            }
        } catch (Exception e) {
            log.error("请求发生异常", e);
        }

        return result;

    }

    public static void main(String[] args) throws UnsupportedEncodingException {

        String param = "{\n" +
            "  \"content\": \"有人关注了你发布的 LANNIA 2233，快去看下吧的！\",\n" +
            "  \"channel\": \"dafengche\",\n" +
            "  \"userId\": \"540017\",\n" +
            "  \"type\": \"car-message\",\n" +
            "  \"cardType\": \"picText\",\n" +
            "  \"cardDef\": {\n" +
            "    \"title\": \"今日头条\",\n" +
            "    \"bodyText\": \"客户李鲁泽 16389382937 想买你的车\",\n" +
            "    \"isBigPic\": true,\n" +
            "    \"bodyPicture\": \"http://img.souche.com/f2e/4d60e311f119e07cfb9816b694d84865.jpg\",\n"
            +
            "    \"isShowFooter\": true,\n" +
            "    \"footer\": {\n" +
            "      \"text\": \"查看详情\",\n" +
            "      \"link\": \"dfc://open/dfcCarDetail?carId=d2ea499e4df24371bc4ea1af1ca2e161\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"protocol\": \"dfc://open/dfcCarDetail?carId=d2ea499e4df24371bc4ea1af1ca2e161\",\n"
            +
            "  \"disableQueue\": true\n" +
            "}";
        String result = HttpBasicAuthUtil
            .doPost("http://msgcenter.dasouche.net/v1/channel/jpush", param,
                "chenxinshi:43rvlxn8q8xcjehbudx2gvmevkrq45wg");
        System.out.println(result);

    }

}
