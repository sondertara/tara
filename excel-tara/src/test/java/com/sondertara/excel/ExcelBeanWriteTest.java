package com.sondertara.excel;

import com.sondertara.common.model.PageResult;
import com.sondertara.common.timing.Stopwatch;
import com.sondertara.excel.boot.ExcelBeanWriter;
import com.sondertara.excel.domain.HolidayCfg;
import com.sondertara.excel.domain.User;
import com.sondertara.excel.domain.export.UserComplexHeader;
import com.sondertara.excel.domain.export.UserPaging;
import com.sondertara.excel.domain.export.UserStyle;
import com.sondertara.excel.domain.export.UserWH;
import com.sondertara.excel.meta.annotation.ExcelExport;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExcelBeanWriteTest {
    private static final String DEFAULT_TARGET_EXCEL_DIR = "target/generated-excel/";

    static List<User> users = new ArrayList<>();
    public static final int DATA_SIZE = 200;
    static List<HolidayCfg> holidayCfgs = new ArrayList<>();

    @BeforeAll
    public static void setUp() {
        for (int i = 0; i < DATA_SIZE; i++) {
            User user = new User();
            if (i > 11 && i <= 16) {
                user.setName("Job");
            } else if (i > 22 && i < 27) {
                user.setName("Tom");
            } else if (i > DATA_SIZE - 6) {
                user.setName("Jack");
            } else {
                user.setName(RandomStringUtils.randomAlphabetic(5));
            }

            user.setAge(i);
            user.setBirth(Calendar.getInstance().getTime());
            user.setHeight(RandomUtils.nextDouble(0, 200));
            users.add(user);
        }

        for (int i = 0; i < DATA_SIZE; i++) {
            HolidayCfg holidayCfg = new HolidayCfg();
            holidayCfg.setHolidayName(RandomStringUtils.randomAlphabetic(5));
            holidayCfg.setHolidayDate(Calendar.getInstance().getTime());
            holidayCfg.setIsWork(RandomStringUtils.random(1, '是', '否'));
            holidayCfgs.add(holidayCfg);
        }

        File targetPath = new File(DEFAULT_TARGET_EXCEL_DIR);
        if (!targetPath.exists()) {
            targetPath.mkdirs();
        }
    }

    /**
     * 根据字段定义顺序导出
     * export Excel the columns order is field definition order
     *
     * @see ExcelExport#bindType()
     * @see com.sondertara.excel.enums.ExcelColBindType#COL_INDEX
     */
    @Test
    public void testOrderByFieldOrder() {
        try (FileOutputStream fos = new FileOutputStream(DEFAULT_TARGET_EXCEL_DIR + "export_data_order_by_field.xlsx")) {
            ExcelBeanWriter.create().mapping(User.class).addData(users).then().to(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据字段定义顺序导出
     * export Excel the columns order is field definition order
     *
     * @see ExcelExport#bindType()
     * @see com.sondertara.excel.enums.ExcelColBindType#COL_INDEX
     */
    @Test
    public void testOrderByColIndex() {
        try (FileOutputStream fos = new FileOutputStream(DEFAULT_TARGET_EXCEL_DIR + "export_data_order_by_col_index.xlsx")) {
            ExcelBeanWriter.create().mapping(User.class).addData(users).then().to(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接口查询导出
     * test sheet data by query
     */
    @Test
    public void testDataByQuery() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "export_sheet_data_by_query.xlsx"))) {
            ExcelBeanWriter.create().mapping(UserPaging.class).addData(index -> {
                List<UserPaging> userPagings = new ArrayList<>();
                for (int i = 0; i < 3000; i++) {
                    UserPaging userPaging = new UserPaging();
                    userPaging.setAge(i);
                    userPaging.setName(RandomStringUtils.randomAlphabetic(5));
                    userPaging.setBirth(Calendar.getInstance().getTime());
                    userPaging.setHeight(RandomUtils.nextDouble(0, 200));
                    userPagings.add(userPaging);
                }
                System.out.println(index);
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return PageResult.of(userPagings).pagination(index, 3000).total(30000L).build();
            }).then().to(fos);
            long elapsed = stopwatch.stop().elapsedMillis();
            System.out.println("耗时:" + elapsed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sheet分页
     * test sheet pagination
     */
    @Test
    public void testWritePagingSheet() {
        List<UserPaging> userPagings = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            UserPaging userPaging = new UserPaging();
            userPaging.setName(RandomStringUtils.randomAlphabetic(5));
            userPaging.setAge(i);
            userPaging.setBirth(Calendar.getInstance().getTime());
            userPaging.setHeight(RandomUtils.nextDouble(0, 200));
            userPagings.add(userPaging);
        }

        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "export_paging_sheet_data.xlsx"))) {
            ExcelBeanWriter.create().mapping(UserPaging.class).addData(userPagings).then().to(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 导出数据 -- 多个sheet
     */
    @Test
    public void testWriteMultipleSheetData() {
        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "export_multiple_sheet_data.xlsx"))) {
            ExcelBeanWriter.create()
                    .mapping(HolidayCfg.class).addData(holidayCfgs)
                    .then()
                    .mapping(User.class).addData(users)
                    .then().to(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 复杂表头
     * test complexHeader
     */
    @Test
    public void testComplexHeader() {
        List<UserComplexHeader> userComplexHeaders = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            UserComplexHeader userComplexHeader = new UserComplexHeader();
            userComplexHeader.setName(RandomStringUtils.randomAlphabetic(5));
            userComplexHeader.setAge(RandomUtils.nextInt(0, 100));
            userComplexHeader.setBirth(Calendar.getInstance().getTime());
            userComplexHeader.setHeight(RandomUtils.nextDouble(0, 200));
            userComplexHeaders.add(userComplexHeader);
        }
        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "export_custom_complex_header.xlsx"))) {
            ExcelBeanWriter.create().mapping(UserComplexHeader.class).addData(userComplexHeaders).then().to(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 自定义行高、列宽
     * test for custom height and width
     */
    @Test
    public void testCustomWidthAndHeight() {
        List<UserWH> userWHS = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            UserWH userWH = new UserWH();
            userWH.setName(RandomStringUtils.randomAlphabetic(5));
            userWH.setAge(RandomUtils.nextInt(0, 100));
            userWH.setBirth(Calendar.getInstance().getTime());
            userWH.setHeight(RandomUtils.nextDouble(0, 200));
            userWHS.add(userWH);
        }

        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "export_custom_width_height.xlsx"))) {
            ExcelBeanWriter.create().mapping(UserWH.class).addData(userWHS).then().to(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 单元格样式测试（标题、条纹）
     */
    @Test
    public void testCellStyle() {
        List<UserStyle> userStyles = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            UserStyle userStyle = new UserStyle();
            userStyle.setName(RandomStringUtils.randomAlphabetic(5));
            userStyle.setAge(RandomUtils.nextInt(0, 100));
            userStyle.setBirth(Calendar.getInstance().getTime());
            userStyle.setHeight(RandomUtils.nextDouble(0, 200));
            userStyles.add(userStyle);
        }

        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "export_greent_cell_style.xlsx"))) {
            ExcelBeanWriter.create().mapping(UserStyle.class).addData(userStyles).then().to(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
