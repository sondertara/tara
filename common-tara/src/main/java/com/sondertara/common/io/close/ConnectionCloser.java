package com.sondertara.common.io.close;

import com.sondertara.common.collection.Lists;

import java.sql.Connection;
import java.util.List;

public class ConnectionCloser extends AbstractCloser<Connection> {
    @Override
    protected void doClose(Connection connection) throws Exception {
        connection.close();
    }

    @Override
    public List<Class> applyTo() {
        return Lists.newArrayList(Connection.class);
    }
}
