package com.sondertara;

import com.sondertara.excel.ExcelExportTara;
import com.sondertara.excel.entity.PageQueryParam;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.model.UserDTO;
import com.sondertara.model.UserInfoVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

    public void exportCsvDemo() {

        PageQueryParam query = new PageQueryParam();
        query.setPageStart(1);
        query.setPageEnd(10);
        query.setPageSize(1000);
        String path = ExcelExportTara.of(UserInfoVo.class).query(query, new ExportFunction<PageQueryParam, UserDTO>() {
            @Override
            public List<UserDTO> pageQuery(PageQueryParam param, int pageNo) {

                // query list data from db
                List<UserDTO> list = new ArrayList<>(200);
                for (int i = 0; i < 200; i++) {
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
                return list;
            }

            @Override
            public UserInfoVo convert(UserDTO queryResult) {
                // convert query result
                UserInfoVo userInfoVo = new UserInfoVo();
                userInfoVo.setAddress(queryResult.getD());
                userInfoVo.setAge(queryResult.getA());
                userInfoVo.setName(queryResult.getN());
                return userInfoVo;
            }
        }).exportCsv("Excel-Test");
        logger.info("path:{}", path);
        logger.info("data list size:{}", atomicInteger.get());
        //FileUtils.remove(path);
    }

    public static void main(String[] args) {
        new ExcelExportDemo().exportCsvDemo();
    }
}
