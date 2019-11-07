package org.cherubim.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.beanutils.BeanUtils;

import java.util.Map;

/**
 * map转化
 *
 * @author jnx
 * @create 2017/6/8
 */
public class MapUtil {
    /**
     * 将json格式的字符串解析成Map对象 <li>
     * json格式：{"name":"admin","retries":"3fff","testname"
     * :"ddd","testretries":"fffffffff"}
     */
    public static Map<String, Object> toMap(String json) {
        Map<String, Object> userMap =
                JSON.parseObject(json, new TypeReference<Map<String, Object>>() {
                });
        return userMap;
    }

    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {
        if (map == null) {
            return null;
        }
        Object obj = beanClass.newInstance();
        BeanUtils.populate(obj, map);
        return obj;
    }

    public static void main(String[] args) {
        String ss = "{\"carType\":1,\"idCardNo\":\"63232219900218091X\",\"name\":\"马木哈买七\",\"mobile\":\"13213575858\",\"guidePrice\":17980000}";
        Map<String, Object> map = toMap(ss);

        for (Map.Entry entry : map.entrySet()) {
            System.out.println("key=" + entry.getKey() + ",value=" + entry.getValue());
        }
    }
}
