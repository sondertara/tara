package com.sondertara.common.net.port;

import com.sondertara.common.io.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NativeLocalTcpPortGenerator implements LocalPortGenerator {
    @Override
    public Integer get() {
        Socket socket = new Socket();
        int port = -1;
        try {
            socket.bind(new InetSocketAddress(0));
            port = socket.getLocalPort();
        } catch (IOException ex) {
            // ignore it
        } finally {
            IOUtils.close(socket);
        }

        return port;
    }
}
