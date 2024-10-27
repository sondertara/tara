package com.sondertara.common.security.prevention.injection;


import com.sondertara.common.collection.Lists;

import java.util.List;

public class CommandLineInjectionPreventionHandler extends InjectionPreventionHandler{
    private final List<String> DEFAULT_REMOVED_SYMBOLS = Lists.asList(
            "&", "|","||", ";", "$", "%", "-", "(", ")", "`"
    );

    @Override
    public List<String> getBlacklist() {
        List<String> blacklist = super.getBlacklist();
        return blacklist == null ? DEFAULT_REMOVED_SYMBOLS : blacklist;
    }
}
