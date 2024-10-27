package com.sondertara.common.management.connector.local.jndi;

import com.sondertara.common.logging.Loggers;
import com.sondertara.common.management.ConnectorConfiguration;
import com.sondertara.common.management.JMXConnection;
import com.sondertara.common.management.JMXConnectionFactory;
import com.sondertara.common.management.connector.local.JMXLocalConnection;
import org.slf4j.Logger;

import javax.management.MBeanServer;
import javax.naming.InitialContext;

public class JndiLocalConnectionFactory implements JMXConnectionFactory {
    private static final String DEFAULT_CONTEXT_FACTORY = "com.sun.InitialContextFactroy";

    @Override
    public JMXConnection getConnection(final ConnectorConfiguration config) {
        final JndiConfiguration jndiConfig = (JndiConfiguration) config;
        final String jndi = jndiConfig.getServerJndi();
        MBeanServer server = null;
        try {
            String jndiFactroyClass = jndiConfig.getJndiFactoryClass();
            if (jndiFactroyClass == null || jndiFactroyClass.isEmpty()) {
                jndiFactroyClass = "com.sun.InitialContextFactroy";
                jndiConfig.setProperty("java.naming.factory.initial", jndiFactroyClass);
            }
            final InitialContext context = new InitialContext(jndiConfig.getProperties());
            server = (MBeanServer) context.lookup(jndi);
        } catch (Throwable t) {
            Logger logger = Loggers.getLogger(getClass());
            logger.error(t.getMessage(), t);
        }
        if (server != null) {
            return new JMXLocalConnection(server);
        }
        return null;
    }
}
