package com.sondertara.common.propertyset;


import com.sondertara.common.base.Nameable;

/**
 *  */
public interface PropertySet<SRC> extends Nameable {
    SRC getSource();

    /**
     * whether contains the specified property or not
     *
     * @return a boolean
     */
    boolean containsProperty(String name);

    /**
     * @param name the property name
     * @return null if not contains the specified name
     */
    Object getProperty(String name);
}
