package com.sondertara.common.text.lexer;


import com.sondertara.common.base.Assert;
import org.jspecify.annotations.NonNull;

public abstract class AbstractLexer implements Lexer {

    protected abstract void startInternal(@NonNull CharSequence buf, int startOffset, int endOffset, int initialState);


    public final void start(@NonNull CharSequence buf, int start, int end) {
        Assert.notNull(buf, "buf");
        startInternal(buf, start, end, 0);
    }

    public final void start(@NonNull CharSequence buf) {
        Assert.notNull(buf, "buf");
        startInternal(buf, 0, buf.length(), 0);
    }

    public Token getToken() {
        return new BaseToken(getTokenType(), getTokenStart(), getTokenEnd(), getTokenText());
    }

    @NonNull
    public String getTokenText() {
        return getBufferSequence().subSequence(getTokenStart(), getTokenEnd()).toString();
    }

    @NonNull
    public LexerPosition getCurrentPosition() {
        int offset = getTokenStart();
        int intState = getState();
        return new LexerPositionImpl(offset, intState);
    }

    /**
     * 从指定位置开始处理
     *
     * @param position 指定的位置
     */
    public void restore(@NonNull LexerPosition position) {
        Assert.notNull(position, "position");
        startInternal(getBufferSequence(), position.getOffset(), getBufferEnd(), position.getState());
    }
}
