package com.sondertara.common.text;

import com.sondertara.common.base.Assert;
import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.collection.StreamUtils;
import com.sondertara.common.text.tokenizer.CommonTokenizer;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *  */
public class StrTokenizer extends CommonTokenizer<String> {
    private List<String> delimiters = StringUtils.WHITESPACE_CHAR.stream().map(String::valueOf).collect(Collectors.toList());;
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
        this(str, (String)null);
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
        setDelimiters(Lists.asList(delimiters));
        this.max = max < 0 ? Integer.MAX_VALUE : max;
        this.ignoreCase = ignoreCase;
        this.tokenFactory = (tokenContent, isDelimiter) -> tokenContent;
    }

    public void setDelimiters(List<String> delimiters) {
        this.delimiters = ObjectUtils.defaultIfEmpty(StreamUtils.of(delimiters).filter(Objects::nonNull).collect(Collectors.toList()), this.delimiters);
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
                    // ignore it
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
            String delimiter = StreamUtils.of(this.delimiters)
                    .filter(new Predicate<String>() {
                        @Override
                        public boolean test(String delimiter) {
                            if (StringUtils.startsWith(delimiter, s, ignoreCase)) {
                                if (getBuffer().limit() - position >= delimiter.length()) {
                                    String substring = getBuffer().subSequence(position, position + delimiter.length()).toString();
                                    return StringUtils.equals(substring, delimiter, ignoreCase);
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        }
                    }).findFirst().orElse("");

            return delimiter;
        }
    }

    public String next(List<String> delimiters) {
        Assert.isTrue(CollectionUtils.isNotEmpty(delimiters));
        this.delimiters = delimiters;
        return getNext();
    }

    public static void main(String[] args) {
       String  string = "system0@*v*@0share-ns-org-10@*v*@0i632d4c-tomcat-00@*v*@0tomcat";
        String[] strings = StringUtils.split(string, "0@*v*@0");
        System.out.println(string);

    }

}
