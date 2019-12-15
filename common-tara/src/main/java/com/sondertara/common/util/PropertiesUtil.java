package com.sondertara.common.util;

import com.sondertara.common.exception.TaraException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * PropertiesUtil with singleton
 *
 * @author huangxiaohu
 */
public class PropertiesUtil {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
    private static Map<String, PropertiesUtil> instanceMap = new HashMap<String, PropertiesUtil>();
    private String propertyFileName;
    private Properties properties = null;
    private ClassLoader oClassLoader = null;
    private URI uri = null;


    private PropertiesUtil(String propertyFileName) {
        this.propertyFileName = propertyFileName;
        loadProperties();
    }

    /**
     * 读取properties文件
     *
     * @param file 文件路径
     * @return 返回properties 对象
     */
    public static Properties fromFile(String file) {
        InputStream stream = null;

        try {
            stream = new FileInputStream(new File(file));
            return fromStream(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close(stream);
        }
    }

    /**
     * 读取properties文件
     *
     * @param file 文件路径
     * @return 返回properties 对象
     */
    public static Properties fromClasspath(String file) {
        InputStream stream = null;
        try {
            stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
            return fromStream(stream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(stream);
        }
    }

    /**
     * convert stream  to properties
     *
     * @param stream InputStream
     * @return Properties
     * @throws IOException
     */
    private static Properties fromStream(InputStream stream) throws IOException {
        Properties dest = new Properties();
        Properties src = new Properties();
        src.load(stream);

        // 如果key value为字符串，需要trim一下
        for (Map.Entry<Object, Object> entry : src.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            Object newKey = key;
            Object newValue = value;
            if (newKey instanceof String) {
                newKey = key.toString().trim();
            }

            if (newValue instanceof String) {
                newValue = value.toString().trim();
            }

            dest.put(newKey, newValue);
        }

        return dest;
    }

    /**
     * dispose stream
     *
     * @param stream InputStream
     */
    private static void close(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 获取单例对象
     *
     * @param propertyFileName 文件名称
     * @return PropertiesUtil
     */
    public static PropertiesUtil getInstance(String propertyFileName) {
        if (instanceMap.get(propertyFileName) != null) {
            return (PropertiesUtil) instanceMap.get(propertyFileName);
        }
        //实例化
        PropertiesUtil instance = new PropertiesUtil(propertyFileName);
        instanceMap.put(propertyFileName, instance);

        return instance;
    }

    /**
     * 加载properties文件
     */
    private void loadProperties() {
        try {
            this.properties = new Properties();
            this.oClassLoader = Thread.currentThread().getContextClassLoader();
            this.uri = this.oClassLoader.getResource(propertyFileName + ".properties").toURI();
            InputStream is = oClassLoader.getResourceAsStream(this.propertyFileName + ".properties");
            if (is != null) {
                this.properties.load(is);
                is.close();
            }
            is = null;
        } catch (Exception e) {
            logger.error("can't find properties file[{}]", propertyFileName, e);
        }
    }

    /**
     * 获取文件属性值
     *
     * @param propertyName 属性名
     * @return value
     */
    public String getProperty(String propertyName) {
        String property = null;
        try {
            if (this.properties == null) {
                loadProperties();
            }
            property = this.properties.getProperty(propertyName);

        } catch (Exception e) {
            logger.error("read property[{}] error. ", propertyName, e);
        }
        if (null == property) {
            throw new TaraException("no property[{}] found in[{}] file.", propertyName, propertyFileName);
        }
        return property;
    }

    /**
     * 获取文件属性值
     *
     * @param propertyName 属性名
     * @param defaultValue 默认值
     * @return value
     */
    public String getProperty(String propertyName, String defaultValue) {
        try {
            if (this.properties == null) {
                loadProperties();
            }
            return this.properties.getProperty(propertyName, defaultValue);
        } catch (Exception e) {
            logger.error("read property[{}] error. ", propertyName, e);
        }
        return defaultValue;
    }

    /**
     * get Properties
     *
     * @return Properties
     */
    public Properties getProperties() throws TaraException {
        try {
            if (this.properties == null) {
                loadProperties();
            }
            return this.properties;
        } catch (Exception e) {
            logger.error("load property file name[{}] error. ", propertyFileName, e);
            throw new TaraException(e.getMessage());
        }
    }


    /**
     * 修改文件属性值
     *
     * @param propertyName  属性名
     * @param propertyValue 属性值
     */
    public void setProperty(String propertyName, String propertyValue) {
        try {
            if (this.properties == null) {
                loadProperties();
            }

            OutputStream fos = new FileOutputStream(new File(uri));

            properties.setProperty(propertyName, propertyValue);
            properties.store(fos, "Update '" + propertyName + "' value");

            if (fos != null) {
                fos.flush();
                fos.close();
            }
        } catch (Exception e) {
            logger.error("update property[{}] error{}", propertyName, e.getMessage());
        }
    }

}