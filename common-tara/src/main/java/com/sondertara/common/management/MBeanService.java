package com.sondertara.common.management;

import com.sondertara.common.struct.Entry;

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

public interface MBeanService {
    boolean isServiceMatch();

    List<Entry<String, Object>> getMBeanAttrs(final Hashtable<String, String> p0, final Collection<String> p1);
}
