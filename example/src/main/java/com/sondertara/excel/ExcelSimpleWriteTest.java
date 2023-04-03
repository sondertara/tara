package com.sondertara.excel;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.sondertara.common.io.FileUtils;
import com.sondertara.common.model.PageResult;
import com.sondertara.common.util.BeanUtils;
import com.sondertara.common.util.RandomUtils;
import com.sondertara.excel.base.TaraExcelConfig;
import com.sondertara.excel.boot.ExcelSimpleLegacyWriter;
import com.sondertara.excel.boot.ExcelSimpleWriter;
import com.sondertara.excel.domain.User;
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
    static String path ="test1.xlsx";

    @BeforeAll
    public static void setup() throws IOException {
        FileUtils.remove(path);
        FileUtils.touch(path);

    }

    /**
     * 如果查询很耗时,或者数据很大，可以使用分页查询接口,使用生产消费模型来加快Excel导出
     * If it takes time to query data or large amount of data,can use query function which is designed by producer-consumer pattern
     *
     * @throws IOException io error
     * @see com.sondertara.excel.function.ExportFunction
     */
    @Test
    public void testQuery() throws IOException {
        OutputStream outputStream = Files.newOutputStream(Paths.get(path));
        Stopwatch stopwatch = Stopwatch.createStarted();
        ExcelSimpleLegacyWriter.create().header(Lists.newArrayList("姓名", "年龄", "生日", "身高")).addData(index -> {
            List<User> users = new ArrayList<>();
            for (int i = 0; i < 3000; i++) {
                User user = new User();
                user.setName(RandomUtils.randomString(6));
                if (i == 0) {
                    user.setName("这是一个测试看看效果最大装备行号经济纠纷急急急");
                }
                if (i == 1) {
                    user.setName("这是一个测试");
                }
                user.setAge(i);
                user.setBirth(new Date());
                user.setHeight(RandomUtils.randomDouble(180));
                users.add(user);
            }
            try {
                Thread.sleep(2 * 1000);
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
            PageResult<Object[]> result = PageResult.of(list).pagination(index, 3000).total(11000L);
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

        ExcelSimpleWriter<?> simpleWriter = ExcelSimpleWriter.create().header(Lists.newArrayList("姓名", "年龄", "生日", "身高"));
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
                Thread.sleep(1 * 1000);
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
