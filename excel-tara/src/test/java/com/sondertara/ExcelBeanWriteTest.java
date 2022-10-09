package com.sondertara;

import com.sondertara.domain.User;
import com.sondertara.excel.boot.ExcelBeanWriter;
import org.junit.jupiter.api.Test;

public class ExcelBeanWriteTest {

    @Test
    public void export() {

        ExcelBeanWriter.fromQuery().mapper(User.class).query(((pageNo, pageSize) -> null)).pagination(1,1,2).generate();
    }
}
