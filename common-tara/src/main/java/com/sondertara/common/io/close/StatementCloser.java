package com.sondertara.common.io.close;

import com.sondertara.common.collection.Lists;

import java.sql.Statement;
import java.util.List;

public class StatementCloser extends AbstractCloser<Statement> {
    @Override
    public List<Class> applyTo() {
        return Lists.newArrayList(Statement.class);
    }

    @Override
    protected void doClose(Statement statement) throws Exception {
        statement.close();
    }
}
