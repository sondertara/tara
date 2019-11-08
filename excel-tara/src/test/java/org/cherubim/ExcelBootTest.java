package org.cherubim;


import org.cherubim.excel.ExcelBoot;
import org.cherubim.excel.entity.ExcelHelper;
import org.cherubim.excel.function.ExportFunction;
import org.cherubim.model.UserDTO;
import org.cherubim.model.UserInfoVo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for simple App.
 */
public class ExcelBootTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        String fileName = "Excel文件名";
        String email = "xhhuangchn@outlook.com";
        final ExcelHelper helper = ExcelHelper.builder().fileName(fileName).receiptUser(email).pageSize(200).build();
        ExcelBoot.builder(helper, UserInfoVo.class).exportCsv(null,
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
}
