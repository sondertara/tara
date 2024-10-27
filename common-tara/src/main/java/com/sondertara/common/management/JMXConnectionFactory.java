package com.sondertara.common.management;

public interface JMXConnectionFactory {
    JMXConnection getConnection(final ConnectorConfiguration configuration);
}
