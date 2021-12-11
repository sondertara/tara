package com.sondertara;

import com.sondertara.common.util.FileUtils;
import com.sondertara.excel.ExcelTara;
import com.sondertara.excel.entity.ExcelHelper;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.model.UserDTO;
import com.sondertara.model.UserInfoVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * the export demo
 * <p>
 * date 2019/12/15 9:08 下午
 *
 * @author huangxiaohu
 * @version 1.0
 * @since 1.0
 **/
public class ExcelExportDemo {
    private static final Logger logger = LoggerFactory.getLogger(ExcelExportDemo.class);

    public void exportCsvDemo() {
        String fileName = "Excel文件名";
        String email = "xhhuangchn@outlook.com";
        final ExcelHelper helper = ExcelHelper.builder().fileName(fileName).workspace(email).pageSize(200).build();
        String path = ExcelTara.builder(helper, UserInfoVo.class).exportCsv(null, new ExportFunction<String, UserDTO>() {
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
        FileUtils.remove(path);
    }
}
