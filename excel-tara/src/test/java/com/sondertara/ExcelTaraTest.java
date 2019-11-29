package com.sondertara;


import com.sondertara.excel.ExcelTara;
import com.sondertara.excel.entity.ErrorEntity;
import com.sondertara.excel.entity.ExcelHelper;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.function.ImportFunction;
import com.sondertara.model.ImportParam;
import com.sondertara.model.UserDTO;
import com.sondertara.model.UserInfoVo;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for simple App.
 */

public class ExcelTaraTest {
    private static final Logger logger = LoggerFactory.getLogger(ExcelTaraTest.class);

    /**
     * Rigorous Test :-)
     */
    @Test
    public void exportTest() {
        String fileName = "Excel文件名";
        String email = "xhhuangchn@outlook.com";
        final ExcelHelper helper = ExcelHelper.builder().fileName(fileName).receiptUser(email).pageSize(200).build();
        ExcelTara.builder(helper, UserInfoVo.class).exportCsv(null,
                new ExportFunction<String, UserDTO>() {
                    @Override
                    public List<UserDTO> pageQuery(String param, int pageNum, int pageSize) {

                        List<UserDTO> list = new ArrayList<>(200);
                        for (int i = 0; i < 200; i++) {
                            UserDTO userDTO = new UserDTO();

                            userDTO.setA(i);
                            userDTO.setN(pageNum + "测试姓名" + i);
                            userDTO.setD("测试地址" + i);
                            list.add(userDTO);

                            if (pageNum == 5 && i == 150) {
                                break;
                            }
                        }
                        return list;
                    }

                    @Override
                    public UserInfoVo convert(UserDTO queryResult) {
                        UserInfoVo userInfoVo = new UserInfoVo();
                        userInfoVo.setAddress(queryResult.getD());
                        userInfoVo.setAge(queryResult.getA());
                        userInfoVo.setName(queryResult.getN());
                        return userInfoVo;
                    }
                });

        assertTrue(true);
    }

    @Test
    public void importTest() throws Exception {

        Appendable errorWriter = new PrintWriter("error.csv", "GBK");

        CSVPrinter errorPrinter = CSVFormat.EXCEL.print(errorWriter);

        ExcelTara.builder(new FileInputStream(new File("/Users/huangxiaohu/Desktop/保费结算导入结果/error.xlsx")), ImportParam.class)
                .importExcel(true, new ImportFunction<ImportParam>() {

                    /**
                     * @param sheetIndex 当前执行的Sheet的索引, 从1开始
                     * @param rowIndex 当前执行的行数, 从1开始
                     * @param userEntity Excel行数据的实体
                     */
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onProcess(int sheetIndex, int rowIndex, ImportParam userEntity) {
                        //将读取到Excel中每一行的userEntity数据进行自定义处理
                        //如果该行数据发生问题,将不会走本方法,而会走onError方法
                        logger.info(userEntity.toString());
                    }

                    /**
                     * @param errorEntity 错误信息实体
                     */
                    @Override
                    public void onError(ErrorEntity errorEntity) {
                        //将每条数据非空和正则校验后的错误信息errorEntity进行自定义处理

                        logger.info(errorEntity.toString());

                        try {
                            List<String> record = new ArrayList<>(5);
                            record.add(errorEntity.getSheetIndex().toString());
                            record.add(errorEntity.getRowIndex().toString());
                            record.add(errorEntity.getCellIndex().toString());
                            record.add(errorEntity.getColumnName());
                            record.add(errorEntity.getCellValue());
                            record.add(errorEntity.getErrorMessage());
                            errorPrinter.printRecord(record);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        errorPrinter.flush();
        errorPrinter.close();

    }
}
