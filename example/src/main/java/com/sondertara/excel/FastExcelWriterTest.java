package com.sondertara.excel;

import com.sondertara.excel.fast.writer.Position;
import com.sondertara.excel.fast.writer.FastWorkbook;
import com.sondertara.excel.fast.writer.Worksheet;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FastExcelWriterTest {

    @Test
    public void simpleTest() {
        try (OutputStream os = Files.newOutputStream(Paths.get("Fast-test.xlsx"))) {
            FastWorkbook wb = new FastWorkbook(os, "MyApplication", "1.0");
            Worksheet ws = wb.newWorksheet("Sheet 1");
            ws.header("page 1 of ?", Position.RIGHT);
            List<Object> data = new ArrayList<>();
            data.add("This is a string in A1");
            data.add(ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()));
            data.add("这是测试");
            data.add("这是测试1");
            data.add("这是测试2");
            data.add(123456);
            data.add(123456.23);
            data.add("最后一行");
            data.add(false);
            data.add("123456");
            data.add("123456.23");
            data.add("设置");
            for (Object o : data) {
                System.out.println(o.getClass().getName());
            }

            for (int i = 0; i < 1000; i++) {
                for (int i1 = 0; i1 < data.size(); i1++) {
                    ws.value(i, i1, data.get(i1));
                }
            }
            wb.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
