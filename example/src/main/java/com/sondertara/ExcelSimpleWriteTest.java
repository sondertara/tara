package com.sondertara;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.sondertara.common.io.FileUtils;
import com.sondertara.common.util.BeanUtils;
import com.sondertara.common.util.RandomUtils;
import com.sondertara.domain.User;
import com.sondertara.excel.boot.ExcelSimpleWriter;
import com.sondertara.excel.entity.PageResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ExcelSimpleWriteTest {
    private static final String DEFAULT_TARGET_EXCEL_DIR = "/generated-excel/";
    static String path = "E:\\workspace\\java\\tara\\example\\src\\main\\resources\\user_query.xlsx";

    @BeforeAll
    public static void setup() throws IOException {

        FileUtils.remove(path);
        FileUtils.touch(path);

    }

    @Test
    public void testQuery() throws IOException {
        OutputStream outputStream = Files.newOutputStream(Paths.get(path));
        Stopwatch stopwatch = Stopwatch.createStarted();
        ExcelSimpleWriter.newWorkbook().header(Lists.newArrayList("姓名", "年龄", "生日", "身高")).addData(index -> {
            List<User> users = new ArrayList<>();
            for (int i = 0; i < 3000; i++) {
                User user = new User();
                user.setName(RandomUtils.randomString(6));
                if (i==0){
                    user.setName("这是一个测试看看效果最大装备行号经济纠纷急急急");
                }
                if (i==1){
                    user.setName("这是一个测试");
                }
                user.setAge(i);
                user.setBirth(new Date());
                user.setHeight(RandomUtils.randomDouble(180));
                users.add(user);
            }
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<Object[]> list = users.stream().map(s -> {
                Object[] objects = new Object[4];
                objects[0] = s.getName();
                objects[1] = s.getAge();
                objects[2] = s.getBirth();
                objects[3] = s.getHeight();
                Map<String, Object> bean = BeanUtils.beanToMap(s);
                return objects;
            }).collect(Collectors.toList());
            PageResult<Object[]> result = new PageResult<>(list, 15000, index, 3000);
            return result;
        }).to(outputStream);
        System.out.println("Cost:" + stopwatch.stop().elapsed(TimeUnit.SECONDS));
        outputStream.flush();
        outputStream.close();
    }

    @Test
    public void testFromData() throws IOException {
        OutputStream outputStream = Files.newOutputStream(Paths.get(path));
        Stopwatch stopwatch = Stopwatch.createStarted();

        ExcelSimpleWriter simpleWriter = ExcelSimpleWriter.newWorkbook().header(Lists.newArrayList("姓名", "年龄", "生日", "身高"));
        for (int j = 0; j < 5; j++) {
            List<User> users = new ArrayList<>();
            for (int i = 0; i < 3000; i++) {
                User user = new User();
                user.setName(RandomUtils.randomString(6));
                user.setAge(i);
                user.setBirth(new Date());
                user.setHeight(RandomUtils.randomDouble(180));
                users.add(user);
            }
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<Object[]> list = users.stream().map(s -> {
                Object[] objects = new Object[4];
                objects[0] = s.getName();
                objects[1] = s.getAge();
                objects[2] = s.getBirth();
                objects[3] = s.getHeight();
                return objects;
            }).collect(Collectors.toList());
            simpleWriter.addData(list);
        }
        simpleWriter.to(outputStream);
        System.out.println("Cost:" + stopwatch.stop().elapsed(TimeUnit.SECONDS));
        outputStream.flush();
        outputStream.close();
    }

}
