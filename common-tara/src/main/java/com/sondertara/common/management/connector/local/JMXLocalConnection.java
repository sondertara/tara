package com.sondertara.common.management.connector.local;

import com.sondertara.common.management.JMXConnection;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import java.io.IOException;

public class JMXLocalConnection extends JMXConnection {
    public JMXLocalConnection(final MBeanServerConnection conn) {
        super(conn);
    }

    @Override
    public void close() throws IOException {
        if (this.conn instanceof MBeanServer) {
            // ignore it
        }
    }
}
