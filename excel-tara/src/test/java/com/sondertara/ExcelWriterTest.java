package com.sondertara;


import com.sondertara.domain.HolidayCfg;
import com.sondertara.domain.User;
import com.sondertara.domain.export.UserComplexHeader;
import com.sondertara.domain.export.UserPaging;
import com.sondertara.domain.export.UserStyle;
import com.sondertara.domain.export.UserWH;
import com.sondertara.excel.support.ExcelWriter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RunWith(JUnit4.class)
public class ExcelWriterTest {

    private static final String DEFAULT_TARGET_EXCEL_DIR = "target/generated-excel/";

    static List<User> users = new ArrayList<>();
    static List<HolidayCfg> holidayCfgs = new ArrayList<>();

    @BeforeClass
    public static void setUp() {

        for (int i = 0; i < 1000; i++) {
            User user = new User();
            user.setName(RandomStringUtils.randomAlphabetic(5));
            user.setAge(RandomUtils.nextInt(0, 100));
            user.setBirth(Calendar.getInstance().getTime());
            user.setHeight(RandomUtils.nextDouble(0, 200));
            users.add(user);
        }

        for (int i = 0; i < 1000; i++) {
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
     * 复杂表头
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

        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "custom_complex_header.xlsx"))) {
            ExcelWriter.newInstance().addData(userComplexHeaders).write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 自定义行高、列宽
     */
    @Test
    public void testWH() {
        List<UserWH> userWHS = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            UserWH userWH = new UserWH();
            userWH.setName(RandomStringUtils.randomAlphabetic(5));
            userWH.setAge(RandomUtils.nextInt(0, 100));
            userWH.setBirth(Calendar.getInstance().getTime());
            userWH.setHeight(RandomUtils.nextDouble(0, 200));
            userWHS.add(userWH);
        }

        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "custom_width_height.xlsx"))) {
            ExcelWriter.newInstance().addData(userWHS).write(fos);
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

        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "greent_cell_style.xlsx"))) {
            ExcelWriter.newInstance().addData(userStyles).write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sheet分页
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

        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "paging_sheet_data.xlsx"))) {
            ExcelWriter.newInstance().addData(userPagings).write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出数据 -- 多个sheet
     */
    @Test
    public void testWriteMulitSheetData() {
        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "multi_sheet_data.xlsx"))) {
            ExcelWriter.newInstance().addData(holidayCfgs).addData(users).write(fos);
            //ExcelWriter.newInstance().addData(users, holidayCfgs).write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出数据 --  单个sheet
     */
    @Test
    public void testWriteSingleSheetData() {
        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "single_sheet_data.xlsx"))) {
            ExcelWriter.newInstance().addData(users).write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出模版 -- model、data混合
     */
    @Test
    public void testWriteTemplateXlsxWithModelAndData() {
        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "template_by_model_data.xlsx"))) {
            ExcelWriter.newTemplateInstance(HolidayCfg.class).addData(users).write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出模版 -- 多个Model类
     */
    @Test
    public void testWriteTemplateXlsxWdithMulitModel() {
        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "template_by_models.xlsx"))) {
            ExcelWriter.newTemplateInstance(HolidayCfg.class, User.class).write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出模版 -- 单个Model类
     */
    @Test
    public void testWriteTempateXlsxWidthSingleModel() {
        try (FileOutputStream fos = new FileOutputStream(new File(DEFAULT_TARGET_EXCEL_DIR + "template_by_model.xlsx"))) {
            ExcelWriter.newTemplateInstance(HolidayCfg.class).write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
