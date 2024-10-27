package com.sondertara.common.net.port;

import com.sondertara.common.io.IOUtils;

import java.io.IOException;
import java.net.DatagramSocket;

public class NativeLocalUdpPortGenerator implements LocalPortGenerator {
    @Override
    public Integer get() {
        DatagramSocket socket = null;
        int port = -1;
        try {
            socket = new DatagramSocket();
            port = socket.getLocalPort();
        } catch (IOException ex) {
            // ignore it
        } finally {
            IOUtils.close(socket);
        }
        return port;
    }
}
