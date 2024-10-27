package com.sondertara.common.security.prevention.injection;


import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.struct.Holder;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class InjectionPreventionHandler implements Function<String, String> {
    private List<String> blacklist = null;

    public void setBlacklist(List<String> blacklist) {
        this.blacklist = blacklist;
    }

    public List<String> getBlacklist() {
        return this.blacklist;
    }

    @Override
    public String apply(String value) {
        final Holder<String> stringHolder = new Holder<String>(value);
        CollectionUtils.forEach(getBlacklist(), new Consumer<String>() {
            @Override
            public void accept(String str) {
                String v = stringHolder.get();
                v = StringUtils.remove(v, str);
                stringHolder.set(v);
            }
        }, new Predicate<String>() {
            @Override
            public boolean test(String str) {
                return stringHolder.isEmpty();
            }
        });
        return stringHolder.get();
    }
}
