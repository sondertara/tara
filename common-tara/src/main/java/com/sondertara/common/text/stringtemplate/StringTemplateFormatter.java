package com.sondertara.common.text.stringtemplate;

import com.sondertara.common.text.Formatter;

public interface StringTemplateFormatter extends Formatter<String, String> {
    @Override
    String format(String input, Object... args);
}
