package com.sondertara.common.struct;

import com.sondertara.common.base.Emptys;
import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.base.Valid;
import com.sondertara.common.collection.multivalue.LinkedMultiValueMap;
import com.sondertara.common.collection.multivalue.MultiValueMap;
import com.sondertara.common.hash.HashCodeBuilder;
import com.sondertara.common.text.StringTemplates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entry<K, V> extends Pair<K, V> {
    public Entry(K key) {
        super(key,null);
    }

    public Entry(K key, V value) {
        super(key,value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Entry)) {
            return false;
        }
        @SuppressWarnings("rawtypes")
        Entry that = (Entry) obj;
        if (!ObjectUtils.equals(getKey(), that.getKey())) {
            return false;
        }
        return ObjectUtils.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().with(getKey()).with(getValue()).build();
    }

    @Override
    public String toString() {
        return StringTemplates.formatWithBean("{key: ${key}, value: ${value}}", this);
    }

    public static Entry<String, String> newEntry(String keyValue, String spec) throws IllegalArgumentException {
        Valid.isTrue(Emptys.isNotEmpty(spec),"argument 'spec' is null .");
        Valid.isTrue(Emptys.isNotEmpty(keyValue),"argument 'keyValue' is null .");
        int index = keyValue.indexOf(spec);
        if (index == -1) {
            return new Entry<String, String>(keyValue.trim(), "");
        }
        return new Entry<String, String>(keyValue.substring(0, index).trim(), keyValue.substring(index + spec.length()).trim());
    }

    public static Map<String, String> getMap(String str, String keyValueSpec,  String entrySpec) {
        Map<String, String> map = new HashMap<String, String>();
        if (Emptys.isEmpty(str)) {
            return map;
        }
        String[] entryArray = str.split(entrySpec);
        Entry<String, String> entry;
        for (String keyValue : entryArray) {
            try {
                entry = Entry.newEntry(keyValue, keyValueSpec);
            } catch (IllegalArgumentException ex) {
                entry = null;
            }
            if (entry != null) {
                map.put(entry.getKey(), entry.getValue());
            }
        }

        return map;
    }

    public static MultiValueMap<String,String> getMultiValueMap(String str, String keyValueSpec, String entrySpec) {
        MultiValueMap<String,String> map = new LinkedMultiValueMap<String, String>();
        if (Emptys.isEmpty(str)) {
            return map;
        }
        String[] entryArray = str.split(entrySpec);
        Entry<String, String> entry;
        for (String keyValue : entryArray) {
            try {
                entry = Entry.newEntry(keyValue, keyValueSpec);
            } catch (IllegalArgumentException ex) {
                entry = null;
            }
            if (entry != null) {
                map.add(entry.getKey(), entry.getValue());
            }
        }

        return map;
    }

    public static List<Map<String, String>> getMapList(String src, String keyValueSpec, String entrySpec, String listSpecFlag) {
        List<String> strList = new ArrayList<String>();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (Emptys.isEmpty(listSpecFlag)) {
            strList.add(src);
        } else {
            int index = src.indexOf(listSpecFlag);
            if (index == -1) {
                return list;
            }

            int nextIndex;
            while ((nextIndex = src.indexOf(listSpecFlag, index + listSpecFlag.length())) != -1) {
                strList.add(src.substring(index, nextIndex));
                index = nextIndex;
            }
            strList.add(src.substring(index));
        }

        for (String str : strList) {
            Map<String, String> map = getMap(str, keyValueSpec, entrySpec);
            list.add(map);
        }

        return list;
    }
}