
package org.cherubim.common.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

/**
 * @author huangxiaohu
 */
public class RegexUtil {

    private final static LoadingCache<String, Pattern> LOAD_CACHE =
            CacheBuilder.newBuilder()
                    .maximumSize(30)
                    .build(new CacheLoader<String, Pattern>() {
                        @Override
                        public Pattern load(String pattern) {
                            return Pattern.compile(pattern);
                        }
                    });

    public static Boolean isMatch(String pattern, String value) throws ExecutionException {
        return LOAD_CACHE.get(pattern).matcher(value).matches();
    }
}
