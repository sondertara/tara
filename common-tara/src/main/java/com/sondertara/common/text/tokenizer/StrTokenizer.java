package com.sondertara.common.text.tokenizer;


import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *  */
public class StrTokenizer extends CommonTokenizer<String> {

    public static final List<String> WHITESPACE_CHAR = Lists.newArrayList(" ", "\n", "\t", "\r", "\f");
    private List<String> delimiters = WHITESPACE_CHAR;
    /**
     * 找到的分隔符最大个数，小于0 代表不限制
     * 找到max个分割符之后，不再进行分割。
     */
    private final int max;

    /**
     * 已找到的分隔符的个数
     */
    private int foundDelimiterCount = 0;
    private long lastDelimiterStartPosition = -1;

    /**
     * 是否忽略大小写
     */
    private boolean ignoreCase;

    public StrTokenizer(String str) {
        this(str, (String) null);
    }

    public StrTokenizer(String str, String... delimiters) {
        this(str, false, delimiters);
    }

    public StrTokenizer(String str, boolean returnDelimiter, String... delimiters) {
        this(str, returnDelimiter, false, delimiters);
    }

    public StrTokenizer(String str, boolean returnDelimiter, int max, String... delimiters) {
        this(str, returnDelimiter, false, max, delimiters);
    }

    public StrTokenizer(String str, boolean returnDelimiter, boolean ignoreCase, String... delimiters) {
        this(str, returnDelimiter, ignoreCase, -1, delimiters);
    }

    public StrTokenizer(String str, boolean returnDelimiter, boolean ignoreCase, int max, String... delimiters) {
        super(str, returnDelimiter);
        setDelimiters(Lists.newArrayList(delimiters));
        this.max = max < 0 ? Integer.MAX_VALUE : max;
        this.ignoreCase = ignoreCase;
        this.tokenFactory = (tokenContent, isDelimiter) -> tokenContent;
    }

    public void setDelimiters(List<String> delimiters) {
        this.delimiters = Optional.ofNullable(delimiters).map(s -> s.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList())).orElse(this.delimiters);
    }

    @Override
    protected String getDelimiter(int start, int end) {
        String delimiter = super.getDelimiter(start, end);
        foundDelimiterCount++;
        return delimiter;
    }

    @Override
    protected String getIfDelimiterStart(final int position, char c) {
        boolean continueFind = foundDelimiterCount < max;
        if (continueFind) {
            String delimiter = getIfDelimiterStartInternal(position, c);
            if (delimiter != null) {
                if (lastDelimiterStartPosition == position) {
                    // 本次为 获取 delimiter
                } else {
                    // foundDelimiterCount++;
                    lastDelimiterStartPosition = position;
                }
            }
            return delimiter;
        } else {
            return null;
        }
    }

    private String getIfDelimiterStartInternal(final int position, char c) {
        final String s = c + "";
        if (this.delimiters.contains("")) {
            if (this.getBuffer().position() < position) {
                return "";
            } else {
                return null;
            }
        } else {
            String delimiter = this.delimiters.stream()
                    .filter(delimiter1 -> {
                        if (StringUtils.startWith(delimiter1, s, ignoreCase)) {
                            if (getBuffer().limit() - position >= delimiter1.length()) {
                                String substring = getBuffer().subSequence(position, position + delimiter1.length()).toString();
                                return StringUtils.equals(substring, delimiter1, ignoreCase);
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }).findFirst().get();

            return delimiter;
        }
    }

    public String next(List<String> delimiters) {
        if(CollectionUtils.isEmpty(delimiters)){
            throw new IllegalArgumentException("delimiters is empty");
        }
        this.delimiters = delimiters;
        return getNext();
    }

}
