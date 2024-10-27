package com.sondertara.common.random;

import com.sondertara.common.struct.Pair;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.StreamUtils;
import com.sondertara.common.id.NanoId;
import com.sondertara.common.text.StrTokenizer;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Randoms {
    public static IRandom of(Random random) {
        return new RandomProxy(random);
    }

    public static IRandom ofSecure() {
        try {
            return of(SecureRandom.getInstance("SHA1PRNG"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String randomString() {
        return randomString("361E6D51-FAEC-444A-9079-341386DA8E2E");
    }

    public static String randomString(String pattern) {
        return randomString("useandom26T198340PX75pxJACKVERYMINDBUSHWOLFGQZbfghjklqvwyzrict", pattern);
    }


    public static String randomString(String alphabet, String pattern) {
        return randomString(alphabet, pattern, "-");
    }

    public static String randomString(String alphabet, String pattern, final String separator) {
        boolean hasSeparator = StringUtils.isNotEmpty(separator);

        if (!hasSeparator) {
            return randomString(alphabet, pattern.length());
        }

        List<String> tokens = new StrTokenizer(pattern, true, false, -1, separator).tokenize();
        // key: token
        // value: whether is separator
        List<Pair<String, Boolean>> segments = tokens.stream()
                .map(token -> new Pair<>(token, StringUtils.equals(separator, token))).collect(Collectors.toList());

        // 过滤出非分隔符的segments，并计算出总的长度
        int expectedLength =

                StreamUtils.sum(segments.stream()
                        .filter(new Predicate<Pair<String, Boolean>>() {
                            @Override
                            public boolean test(Pair<String, Boolean> segment) {
                                return !segment.getValue();
                            }
                        })
                        .map(new Function<Pair<String, Boolean>, Integer>() {
                            @Override
                            public Integer apply(Pair<String, Boolean> segment) {
                                return segment.getKey().length();
                            }
                        })).intValue();
        String str = randomString(alphabet, expectedLength);

        int offset = 0;
        final StringBuilder builder = new StringBuilder();
        for (Pair<String, Boolean> segment : segments) {
            // 是分隔符
            if (segment.getValue()) {
                builder.append(separator);
            } else {
                int segmentLength = segment.getKey().length();
                int endIndex = offset + segmentLength;
                builder.append(StringUtils.substring(str, offset, endIndex));
                offset = endIndex;
            }
        }
        return builder.toString();
    }

    public static String randomString(String alphabet, int length) {
        return NanoId.randomNanoId(null, alphabet.toCharArray(), length);
    }
}
