package com.sondertara.common.text.tokenizer;

import com.sondertara.common.base.Assert;

import java.nio.CharBuffer;
import java.util.Objects;

public abstract class CommonTokenizer<Token> extends AbstractTokenizer<Token> {
    protected boolean returnDelimiter;
    /**
     * buffer#mark()用于标记一个 content region 的开始
     */
    private CharBuffer buffer;
    protected TokenFactory<Token> tokenFactory;

    protected CommonTokenizer(String text, boolean returnDelimiter) {
        Assert.notEmpty(text);
        this.buffer = CharBuffer.wrap(text);
        this.returnDelimiter = returnDelimiter;
    }

    protected final CharBuffer getBuffer() {
        return this.buffer;
    }

    @Override
    protected final Token getNext() {
        Objects.requireNonNull(tokenFactory, "the token factory is null");
        boolean hasRemaining = this.buffer.hasRemaining();
        if (hasRemaining) {
            int position = this.buffer.position();
            int[] delimiterPositions = findNextDelimiter();
            if (delimiterPositions == null) {
                // 直到结束还没找到分隔符
                int regionEnd = this.buffer.limit();
                String region = this.buffer.subSequence(position, regionEnd).toString();
                Token token = tokenFactory.apply(region, false);
                Objects.requireNonNull(token, "the token is null");
                return token;
            } else {
                // 找到了分隔符
               int regionEnd = delimiterPositions[0];
                if (regionEnd == position) {
                    // 刚一进来这个 getNext()方法，就遇到了分隔符
                    if (returnDelimiter) {
                        // 返回分隔符
                        String delimiter = getDelimiter(position, delimiterPositions[1]);
                        this.buffer.position(delimiterPositions[1]);
                        this.buffer.mark();
                        Token token = tokenFactory.apply(delimiter, true);
                        Objects.requireNonNull(token, "the delimiter token is null");
                        return token;
                    } else {
                        // 不返回分隔符的情况下，要再一次进行查找
                        this.buffer.position(delimiterPositions[1]);
                        this.buffer.mark();
                        return getNext();
                    }
                } else if (regionEnd > position) {
                    String region = this.buffer.subSequence(position, regionEnd).toString();
                    this.buffer.position(regionEnd);
                    this.buffer.mark();
                    Token token = tokenFactory.apply(region, false);
                    Objects.requireNonNull(token, "the token is null");
                    return token;
                } else {
                    throw new TokenizationException("error");
                }
            }
        } else {
            return null;
        }

    }

    /**
     * @return 返回下一个delimiter的开始结束位置（包含开始，不包含结束） [0,1)
     */
    private int[] findNextDelimiter() {
        while (this.buffer.hasRemaining()) {
            int position = this.buffer.position();
            char c = this.buffer.get();
            String delimiter = getIfDelimiterStart(position, c);
            if (delimiter != null) {
                return new int[]{position, position + delimiter.length()};
            }
        }
        return null;
    }

    protected String getDelimiter(int start, int end){
        String delimiter = this.buffer.subSequence(start,end).toString();
        this.buffer.position(end);
        this.buffer.mark();
        return delimiter;
    }

    /**
     * 如果接下来是 delimiter，就返回，如果不是范围null
     *
     */
    protected abstract String getIfDelimiterStart(int position, char c);

}
