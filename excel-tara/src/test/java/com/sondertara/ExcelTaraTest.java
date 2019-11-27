package com.sondertara;


import com.sondertara.excel.ExcelTara;
import com.sondertara.excel.entity.ErrorEntity;
import com.sondertara.excel.entity.ExcelHelper;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.function.ImportFunction;
import com.sondertara.model.ImportParam;
import com.sondertara.model.PolicyReturnImportParam;
import com.sondertara.model.UserDTO;
import com.sondertara.model.UserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for simple App.
 */
@Slf4j
public class ExcelTaraTest {
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
        ExcelTara.builder(new FileInputStream(new File("/Users/huangxiaohu/Desktop/data-return-import.xlsx")), PolicyReturnImportParam.class)
                .importExcel(true, new ImportFunction<PolicyReturnImportParam>() {

                    /**
                     * @param sheetIndex 当前执行的Sheet的索引, 从1开始
                     * @param rowIndex 当前执行的行数, 从1开始
                     * @param userEntity Excel行数据的实体
                     */
                    @Override
                    public void onProcess(int sheetIndex, int rowIndex, PolicyReturnImportParam userEntity) {
                        //将读取到Excel中每一行的userEntity数据进行自定义处理
                        //如果该行数据发生问题,将不会走本方法,而会走onError方法
                        log.info(userEntity.toString());
                    }

                    /**
                     * @param errorEntity 错误信息实体
                     */
                    @Override
                    public void onError(ErrorEntity errorEntity) {
                        //将每条数据非空和正则校验后的错误信息errorEntity进行自定义处理

                        log.info(errorEntity.toString());
                    }
                });

    }
}
