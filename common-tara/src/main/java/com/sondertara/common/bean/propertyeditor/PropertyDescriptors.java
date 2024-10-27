package com.sondertara.common.bean.propertyeditor;

import com.sondertara.common.base.ObjectUtils;

import java.beans.PropertyDescriptor;

public class PropertyDescriptors {
    private PropertyDescriptors(){

    }
    /**
     * Compare the given {@code PropertyDescriptors} and return {@code true} if
     * they are equivalent, i.e. their read method, write method, property type,
     * property editor and flags are equivalent.
     * @see PropertyDescriptor#equals(Object)
     */
    public static boolean equals(PropertyDescriptor pd, PropertyDescriptor otherPd) {
        return (ObjectUtils.equals(pd.getReadMethod(), otherPd.getReadMethod()) &&
                ObjectUtils.equals(pd.getWriteMethod(), otherPd.getWriteMethod()) &&
                ObjectUtils.equals(pd.getPropertyType(), otherPd.getPropertyType()) &&
                ObjectUtils.equals(pd.getPropertyEditorClass(), otherPd.getPropertyEditorClass()) &&
                pd.isBound() == otherPd.isBound() && pd.isConstrained() == otherPd.isConstrained());
    }

}
