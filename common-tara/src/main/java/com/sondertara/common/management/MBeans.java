package com.sondertara.common.management;

import com.sondertara.common.collection.Lists;

import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MBeans {
    private MBeans(){

    }
    public static Map<String, Object> getAttributes(JMXConnection connection, String objectName, List<String> attributeNames) {
        MBean mbean = getMBean(connection, objectName, attributeNames);
        return mbean.getAttributeMap();
    }

    public static Object getAttribute(JMXConnection connection, String objectName, String attributeName) {
        MBean mbean = getMBean(connection, objectName, Lists.asList(attributeName));
        return mbean.getAttribute(attributeName);
    }

    public static MBean getMBean(JMXConnection connection, String objectName, List<String> attributeNames) {
        Set<ObjectName> objectNames = ObjectNames.queryObjectNames(connection, objectName);
        ObjectName oname = null;
        if (objectNames.size() > 1) {
            oname = objectNames.toArray(new ObjectName[0])[0];
        }
        boolean getAttributes = attributeNames != null && !attributeNames.isEmpty();
        MBean mbean = MBean.of(oname);
        if (getAttributes) {
            mbean.putAttributes(connection.getAttributes(oname, attributeNames.toArray(new String[0])));
        }
        return mbean;
    }

    public static List<MBean> getMBeans(JMXConnection connection, String objectName, Collection<String> attributeNames) {
        Set<ObjectName> objectNames = ObjectNames.queryObjectNames(connection, objectName);
        List<MBean> mbeans = new ArrayList<MBean>();
        boolean getAttributes = attributeNames != null && !attributeNames.isEmpty();
        for (ObjectName oname : objectNames) {
            MBean mbean = MBean.of(oname);
            mbeans.add(mbean);
            if (getAttributes) {
                mbean.putAttributes(connection.getAttributes(oname, attributeNames.toArray(new String[0])));
            }
        }
        return mbeans;
    }
}
