package com.sondertara.common.text.lexer;


import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

interface Lexer {

    /**
     * 前进
     */
    void next();

    void start(@NonNull CharSequence buf, int start, int end);

    void start(@NonNull CharSequence buf);

    /**
     * @return 获取当前 token 的文本
     */
    @NonNull
    String getTokenText();

    int getState();

    @Nullable
    int getTokenType();

    /**
     * @return 获取当前token的开始 offset,包含
     */
    int getTokenStart();

    /**
     *  @return 获取当前token的结束位置 ,不包含
     */
    int getTokenEnd();


    @NonNull
    LexerPosition getCurrentPosition();

    /**
     * 从指定的位置开始
     */
    void restore(@NonNull LexerPosition position);

    @NonNull
    CharSequence getBufferSequence();

    int getBufferEnd();


}