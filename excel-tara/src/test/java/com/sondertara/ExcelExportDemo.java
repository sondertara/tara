package com.sondertara;

import com.sondertara.domain.UserDTO;
import com.sondertara.domain.UserInfoVo;
import com.sondertara.excel.boot.ExcelBeanWriter;
import com.sondertara.excel.entity.PageQueryParam;
import com.sondertara.excel.entity.PageResult;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    private final AtomicInteger atomicInteger = new AtomicInteger(0);


    @Test
    public void export() throws Exception {

        File file = new File("test.xlsx");
        if (!file.exists()) {
            file.createNewFile();
        }
        OutputStream out = new FileOutputStream(file, false);
        PageQueryParam query = PageQueryParam.builder().build();
        ExcelBeanWriter.fromQuery().mapper(UserInfoVo.class).query((pageNo, pageSize) -> {
            // query list data from db
            List<UserDTO> list = new ArrayList<>(200);
            for (int i = 0; i < pageSize; i++) {
                UserDTO userDTO = new UserDTO();

                userDTO.setA(i);
                userDTO.setN(pageNo + "测试姓名" + i);
                userDTO.setD("测试地址" + i);
                list.add(userDTO);
                if (pageNo == 5 && i == 150) {
                    break;
                }
            }
            atomicInteger.getAndAdd(list.size());

            // convert to target data list
            return new PageResult<>(pageNo, pageSize, 1000L, list.stream().map(u -> {
                UserInfoVo userInfoVo = new UserInfoVo();
                userInfoVo.setAddress(u.getD());
                userInfoVo.setAge(u.getA());
                userInfoVo.setName(u.getN());
                return userInfoVo;
            }).collect(Collectors.toList()));
        }).pagination(1, 10, 200).to(out);

    }
}
