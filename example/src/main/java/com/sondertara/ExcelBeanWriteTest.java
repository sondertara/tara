package com.sondertara;

import com.sondertara.common.util.RandomUtils;
import com.sondertara.domain.User;
import com.sondertara.excel.boot.ExcelBeanWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelBeanWriteTest {

    @Test
    public void export() {

        ExcelBeanWriter.fromQuery().mapper(User.class, pageNo -> null).generate();
    }
}
