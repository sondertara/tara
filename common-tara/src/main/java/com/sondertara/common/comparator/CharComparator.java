package com.sondertara.common.comparator;

import com.sondertara.common.text.CharUtils;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author jinuo.fang
 */
public class CharComparator implements Comparator<Character>, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public int compare(Character c1, Character c2) {
        return CharUtils.compare(c1, c2);
    }
}
