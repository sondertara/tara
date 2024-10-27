package com.sondertara.common.text.tokenizer;

import java.util.function.BiFunction;

/**
 * 5.1.0
 *
 * 找到一个token 后，构建出 Token对象
 *
 * @param <Token>
 */
public interface TokenFactory<Token> extends BiFunction<String, Boolean, Token> {
}
