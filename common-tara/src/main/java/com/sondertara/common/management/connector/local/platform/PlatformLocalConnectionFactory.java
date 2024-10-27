package com.sondertara.common.management.connector.local.platform;

import com.sondertara.common.management.ConnectorConfiguration;
import com.sondertara.common.management.JMXConnection;
import com.sondertara.common.management.JMXConnectionFactory;
import com.sondertara.common.management.connector.local.JMXLocalConnection;

import java.lang.management.ManagementFactory;

public class PlatformLocalConnectionFactory implements JMXConnectionFactory {
    @Override
    public JMXConnection getConnection(final ConnectorConfiguration config) {
        return new JMXLocalConnection(ManagementFactory.getPlatformMBeanServer());
    }
}
