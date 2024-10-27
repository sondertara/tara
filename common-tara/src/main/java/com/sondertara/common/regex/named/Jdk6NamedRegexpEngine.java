package com.sondertara.common.regex.named;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.regex.Option;
import com.sondertara.common.regex.Regexp;
import com.sondertara.common.regex.RegexpEngine;

/**
 *  */
public class Jdk6NamedRegexpEngine implements RegexpEngine {
    @Override
    public String getName() {
        return "jdk";
    }

    @Override
    public Regexp apply(String regexp, Option option) {
        int flags = Option.toFlags(option);
        if (option.isGlobal()) {
            if (!StringUtils.startsWith(regexp, "^")) {
                regexp = "^" + regexp;
            }
            if (!StringUtils.endsWith(regexp, "&")) {
                regexp = regexp + "$";
            }
        }
        return NamedRegexp.compile(regexp, flags);
    }
}
