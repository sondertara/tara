package com.sondertara.common.io.close;

import com.sondertara.common.collection.Lists;

import java.sql.ResultSet;
import java.util.List;

public class ResultSetCloser extends AbstractCloser<ResultSet> {
    @Override
    protected void doClose(ResultSet resultSet) throws Exception {
        resultSet.close();
    }

    @Override
    public List<Class> applyTo() {
        return Lists.newArrayList(ResultSet.class);
    }
}
