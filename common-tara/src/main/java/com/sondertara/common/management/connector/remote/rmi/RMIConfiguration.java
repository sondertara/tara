package com.sondertara.common.management.connector.remote.rmi;

import com.sondertara.common.management.connector.remote.RemoteConfiguration;

public class RMIConfiguration extends RemoteConfiguration {
    private int rmiRegistryPort;

    public boolean isStartRmiRegistry() {
        return this.rmiRegistryPort > 0;
    }

    public int getRmiRegistryPort() {
        return this.rmiRegistryPort;
    }

    public void setRmiRegistryPort(final int rmiRegistryPort) {
        this.rmiRegistryPort = rmiRegistryPort;
    }
}
