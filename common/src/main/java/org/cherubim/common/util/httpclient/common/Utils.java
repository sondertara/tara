package org.cherubim.common.util.httpclient.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;

/**
 * 工具类
 * <p>
 * 用于设定参数特定类型 启用bebug模式，打印消息
 *
 * @author arron
 * @version 1.0
 */
@Slf4j
public class Utils {

    //传入参数特定类型
    public static final String ENTITY_STRING = "$ENTITY_STRING$";
    public static final String ENTITY_JSON = "$ENTITY_JSON$";
    public static final String ENTITY_FILE = "$ENTITY_FILEE$";
    public static final String ENTITY_BYTES = "$ENTITY_BYTES$";
    public static final String ENTITY_INPUTSTREAM = "$ENTITY_INPUTSTREAM$";
    public static final String ENTITY_SERIALIZABLE = "$ENTITY_SERIALIZABLE$";
    public static final String ENTITY_MULTIPART = "$ENTITY_MULTIPART$";
    private static final List<String> SPECIAL_ENTITIY = Arrays
            .asList(ENTITY_STRING, ENTITY_JSON, ENTITY_BYTES, ENTITY_FILE, ENTITY_INPUTSTREAM,
                    ENTITY_SERIALIZABLE, ENTITY_MULTIPART);

    /**
     * 是否开启debug，
     */
    private static boolean debug = false;
    private static Logger logger = LoggerFactory.getLogger(Utils.class);

    /**
     * 检测url是否含有参数，如果有，则把参数加到参数列表中
     *
     * @param url      资源地址
     * @param nvps     参数列表
     * @param encoding 编码
     * @return 返回去掉参数的url
     * @throws UnsupportedEncodingException 不支持的编码异常
     */
    public static String checkHasParas(String url, List<NameValuePair> nvps, String encoding)
            throws UnsupportedEncodingException {
        // 检测url中是否存在参数
        if (url.contains("?") && url.indexOf("?") < url.indexOf("=")) {
            Map<String, Object> map = buildParas(url.substring(url.indexOf("?") + 1));
            map2HttpEntity(nvps, map, encoding);
            url = url.substring(0, url.indexOf("?"));
        }
        return url;
    }

    public static String getUrlParams(String url, Map<String, Object> param) {

        StringBuffer sb = new StringBuffer();
        sb.append(url);
        try {
            if (param != null) {
                if (url.contains("?")) {
                    sb.append("&").append(getParams(param));
                } else {
                    sb.append("?").append(getParams(param));
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static StringBuffer getParams(Map<String, Object> param)
            throws UnsupportedEncodingException {

        StringBuffer buffer = new StringBuffer();
        if (param != null && !param.isEmpty()) {
            for (Entry<String, Object> entry : param.entrySet()) {
                buffer.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"))
                        .append("&");
            }
            buffer.deleteCharAt(buffer.length() - 1);
        }
        return buffer;
    }

    /**
     * 参数转换，将map中的参数，转到参数列表中
     *
     * @param nvps     参数列表
     * @param map      参数列表（map）
     * @param encoding 编码
     * @return 返回HttpEntity
     * @throws UnsupportedEncodingException 不支持的编码异常
     */
    public static HttpEntity map2HttpEntity(List<NameValuePair> nvps, Map<String, Object> map,
                                            String encoding) throws UnsupportedEncodingException {
        HttpEntity entity = null;
        if (map != null && map.size() > 0) {
            boolean isSpecial = false;
            // 拼接参数
            for (Entry<String, Object> entry : map.entrySet()) {
                //判断是否在之中
                if (SPECIAL_ENTITIY.contains(entry.getKey())) {
                    isSpecial = true;
                    //string
                    if (ENTITY_STRING.equals(entry.getKey())) {
                        entity = new StringEntity(String.valueOf(entry.getValue()), encoding);
                        break;
                    } else //json
                        if (ENTITY_JSON.equals(entry.getKey())) {
                            entity = new StringEntity(String.valueOf(entry.getValue()), encoding);
                            String contentType = "application/json";
                            if (encoding != null) {
                                contentType += ";charset=" + encoding;
                            }
                            ((StringEntity) entity).setContentType(contentType);
                            break;
                        } else //file
                            if (ENTITY_BYTES.equals(entry.getKey())) {
                                entity = new ByteArrayEntity((byte[]) entry.getValue());
                                break;
                            } else if (ENTITY_FILE.equals(entry.getKey())) {
                                if (File.class.isAssignableFrom(entry.getValue().getClass())) {
                                    entity = new FileEntity((File) entry.getValue(),
                                            ContentType.APPLICATION_OCTET_STREAM);
                                } else if (entry.getValue().getClass() == String.class) {
                                    entity = new FileEntity(new File((String) entry.getValue()),
                                            ContentType.create("text/plain", "UTF-8"));
                                }
                                break;
                            } else //inputstream
                                if (ENTITY_INPUTSTREAM.equals(entry.getKey())) {
//						entity = new InputStreamEntity();
                                    break;
                                } else //serializeable
                                    if (ENTITY_SERIALIZABLE.equals(entry.getKey())) {
//						entity = new SerializableEntity()
                                        break;
                                    } else //MultipartEntityBuilder
                                        if (ENTITY_MULTIPART.equals(entry.getKey())) {
                                            File[] files = null;
                                            if (File.class
                                                    .isAssignableFrom(entry.getValue().getClass().getComponentType())) {
                                                files = (File[]) entry.getValue();
                                            } else if (entry.getValue().getClass().getComponentType() == String.class) {
                                                String[] names = (String[]) entry.getValue();
                                                files = new File[names.length];
                                                for (int i = 0; i < names.length; i++) {
                                                    files[i] = new File(names[i]);
                                                }
                                            }
                                            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                                            builder.setCharset(Charset.forName(encoding));
                                            // 设置浏览器兼容模式
                                            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                                            int count = 0;
                                            for (File file : files) {
                                                //把文件转换成流对象FileBody
                                                // FileBody fileBody = new FileBody(file);
                                                //builder.addPart(String.valueOf(map.get(ENTITY_MULTIPART+".name")) + count++, fileBody);

                                                builder.addBinaryBody(
                                                        String.valueOf(map.get(ENTITY_MULTIPART + ".name")) + count++,
                                                        file);
                                            }
                                            boolean forceRemoveContentTypeCharset = (Boolean) map
                                                    .get(ENTITY_MULTIPART + ".rmCharset");
                                            Map<String, Object> m = new HashMap<String, Object>();
                                            m.putAll(map);
                                            m.remove(ENTITY_MULTIPART);
                                            m.remove(ENTITY_MULTIPART + ".name");
                                            m.remove(ENTITY_MULTIPART + ".rmCharset");
                                            Iterator<Entry<String, Object>> iterator = m.entrySet().iterator();
                                            // 发送的数据
                                            while (iterator.hasNext()) {
                                                Entry<String, Object> e = iterator.next();
                                                builder.addTextBody(e.getKey(), String.valueOf(e.getValue()),
                                                        ContentType.create("text/plain", encoding));
                                            }
                                            // 生成 HTTP POST 实体
                                            entity = builder.build();

                                            //强制去除contentType中的编码设置，否则，在某些情况下会导致上传失败
                                            if (forceRemoveContentTypeCharset) {
                                                removeContentTypeCharset(encoding, entity);
                                            }
                                            break;
                                        } else {
                                            nvps.add(new BasicNameValuePair(entry.getKey(),
                                                    String.valueOf(entry.getValue())));
                                        }
                } else {
                    nvps.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
                }
            }
            if (!isSpecial) {
                entity = new UrlEncodedFormEntity(nvps, encoding);
            }
        }
        return entity;
    }

    /**
     * 移除content-type中的charset
     *
     * @param encoding 编码
     * @param entity   请求参数及数据信息
     */
    private static void removeContentTypeCharset(String encoding, HttpEntity entity) {
        try {
            Class<?> clazz = entity.getClass();
            Field field = clazz.getDeclaredField("contentType");
            field.setAccessible(true);
            if (Modifier.isFinal(field.getModifiers())) {
                //去除final修饰符的影响，将字段设为可修改的
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            }
            BasicHeader o = (BasicHeader) field.get(entity);
            field.set(entity, new BasicHeader(HTTP.CONTENT_TYPE,
                    o.getValue().replace("; charset=" + encoding, "")));
        } catch (NoSuchFieldException e) {
            log.error("exception", e);
        } catch (SecurityException e) {
            log.error("exception", e);
        } catch (IllegalArgumentException e) {
            log.error("exception", e);
        } catch (IllegalAccessException e) {
            log.error("exception", e);
        }
    }


    /**
     * 生成参数 参数格式：k1=v1&amp;k2=v2
     *
     * @param paras 参数列表
     * @return 返回参数列表（map）
     */
    public static Map<String, Object> buildParas(String paras) {
        String[] p = paras.split("&");
        String[][] ps = new String[p.length][2];
        int pos = 0;
        for (int i = 0; i < p.length; i++) {
            pos = p[i].indexOf("=");
            ps[i][0] = p[i].substring(0, pos);
            ps[i][1] = p[i].substring(pos + 1);
            pos = 0;
        }
        return buildParas(ps);
    }

    /**
     * 生成参数 参数类型：{{"k1","v1"},{"k2","v2"}}
     *
     * @param paras 参数列表
     * @return 返回参数列表（map）
     */
    public static Map<String, Object> buildParas(String[][] paras) {
        // 创建参数队列
        Map<String, Object> map = new HashMap<String, Object>();
        for (String[] para : paras) {
            map.put(para[0], para[1]);
        }
        return map;
    }

}
