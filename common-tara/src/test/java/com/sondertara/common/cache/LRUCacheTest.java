package com.sondertara.common.cache;

import com.sondertara.common.cache.impl.LRUCache;
import org.junit.jupiter.api.Test;

/**
 * TODO
 *
 * @author huangxiaohu.1ih
 * @date 2024/8/22 19:36
 */
public class LRUCacheTest {

    @Test
    public void test() throws InterruptedException {
        Cache<String, String> cache = CacheBuilder.newBuilder()
                .cacheClass(LRUCache.class)
                .maxCapacity(4)
                .evictExpiredInterval(-1)
                .refreshAllInterval(-1)

                .cacheListener(new CacheListener<Object, Object>() {
                    @Override
                    public void onRemove(Object key, Object value) {
                        System.out.println(key + ":" + value + ":");
                    }
                })
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) {
                        return key;
                    }

                });

        cache.put("1", "1");
        cache.put("2", "2");
        cache.put("3", "3");
        cache.put("4", "4");
        Thread.sleep(1000);
        Object o = cache.get("1");
        System.out.println("读取：" + o + ",顺序:" + cache.toMap().keySet().toString());
        Object o1 = cache.get("4");
        System.out.println("读取：" + o1 + ",顺序:" + cache.toMap().keySet());
        Object o2 = cache.get("2");
        System.out.println("读取：" + o2 + ",顺序:" + cache.toMap().keySet());
        String writeKey = "5";
        cache.put(writeKey, "5");
        System.out.println("写入：" + writeKey + ",顺序:" + cache.toMap().keySet());
        Object o3 = cache.get("1");
        System.out.println("读取：" + o3 + ",顺序:" + cache.toMap().keySet());
        cache.size();
        System.out.println(cache.size());
        System.out.println("顺序:" + cache.toMap().keySet());

    }
}
