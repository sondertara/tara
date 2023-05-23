package com.sondertara.excel;

import com.sondertara.common.model.PageResult;
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

public class ExcelBeanWriteTest {
    private static final String DEFAULT_TARGET_EXCEL_DIR = "target/generated-excel/";

    static List<User> users = new ArrayList<>();
    public static final int DATA_SIZE = 5000;
    static List<HolidayCfg> holidayCfgs = new ArrayList<>();

    @BeforeAll
    public static void setUp() {
        for (int i = 0; i < DATA_SIZE; i++) {
            User user = new User();
            user.setName(RandomStringUtils.randomAlphabetic(5));
            user.setAge(RandomUtils.nextInt(0, 100));
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
            ExcelBeanWriter.fromData().addData(users).then().to(fos);
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
            ExcelBeanWriter.fromData().addData(users).then().to(fos);
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
        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "export_sheet_data_by_query.xlsx"))) {
            ExcelBeanWriter.fromQuery().mapper(UserPaging.class, index -> {
                List<UserPaging> userPagings = new ArrayList<>();
                for (int i = 0; i < 850; i++) {
                    UserPaging userPaging = new UserPaging();
                    userPaging.setName(RandomStringUtils.randomAlphabetic(5));
                    userPaging.setAge(RandomUtils.nextInt(0, 100));
                    userPaging.setBirth(Calendar.getInstance().getTime());
                    userPaging.setHeight(RandomUtils.nextDouble(0, 200));
                    userPagings.add(userPaging);
                }
                return PageResult.of(userPagings).pagination(index, 1000).total(850L);
            }).then().to(fos);
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
            userPaging.setAge(RandomUtils.nextInt(0, 100));
            userPaging.setBirth(Calendar.getInstance().getTime());
            userPaging.setHeight(RandomUtils.nextDouble(0, 200));
            userPagings.add(userPaging);
        }

        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "export_paging_sheet_data.xlsx"))) {
            ExcelBeanWriter.fromData().addData(userPagings).then().to(fos);
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
            ExcelBeanWriter.fromData().addData(holidayCfgs).addData(users).then().to(fos);
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
            ExcelBeanWriter.fromData().addData(userComplexHeaders).then().to(fos);
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
            ExcelBeanWriter.fromData().addData(userWHS).then().to(fos);
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
            ExcelBeanWriter.fromData().addData(userStyles).then().to(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
