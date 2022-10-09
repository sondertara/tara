package com.sondertara.common.bean;

import com.sondertara.common.bean.model.ArpBaitEntity;
import com.sondertara.common.bean.model.ArpBaitVo;
import com.sondertara.common.bean.model.BaitTemplateEntity;
import com.sondertara.common.bean.model.BaitTemplateVo;
import com.sondertara.common.bean.model.FileBaitEntity;
import com.sondertara.common.bean.model.FileBaitVo;
import com.sondertara.common.bean.model.ProcessBaitEntity;
import com.sondertara.common.bean.model.ProcessBaitVo;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BeanUtilsTest {

    @Test
    public void test() {
        // 90ms 93ms 106ms 101ms 100ms
        try {
            //native
            BaitTemplateVo baitTemplateVo = getBaitTemplateVo();
            BaitTemplateEntity entity = convertToBaitTemplateEntity(baitTemplateVo);
            BaitTemplateEntity entity1 = new BaitTemplateEntity();
            //Deep copy
            //DeepCopyUtils.copyProperties(baitTemplateVo, entity1);
            //Assertions.assertEquals(entity.toString(), entity1.toString());

            //local
            BaitTemplateEntity entity3 = new BaitTemplateEntity();
            com.sondertara.common.util.BeanUtils.copyProperties(baitTemplateVo, entity3);
            Assertions.assertEquals(entity.toString(), entity3.toString());
            // apache
            BaitTemplateEntity entity2 = new BaitTemplateEntity();
            BeanUtils.copyProperties(entity2, baitTemplateVo);
            Assertions.assertEquals(entity.toString(), entity2.toString());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

    //Benchmark                                        Mode  Cnt   Score   Error  Units
    //Test.apacheBeanUtilsTest  avgt    5   6.928 ± 6.069  ms/op
    //Test.deepCopyTest         avgt    5  17.572 ± 1.526  ms/op
    //Test.hutoolBeanUtilTest   avgt    5  15.146 ± 0.528  ms/op
    //Test.hutoolConvertTest    avgt    5  16.643 ± 2.288  ms/op
    //Test.localCopyTest        avgt    5   0.387 ± 0.022  ms/op
    //Test.nativeTest           avgt    5   0.227 ± 0.032  ms/op


    private BaitTemplateVo getBaitTemplateVo() {
        BaitTemplateVo baitTemplateVo = new BaitTemplateVo();
        baitTemplateVo.setName("123");
        baitTemplateVo.setOsType("windows");
        baitTemplateVo.setId(123456L);
        baitTemplateVo.setArpBaitList(new ArrayList<ArpBaitVo>() {{
            ArpBaitVo arpBaitVo1 = new ArpBaitVo();
            arpBaitVo1.setId(123);
            arpBaitVo1.setIp("1.2.3.4");
            arpBaitVo1.setMac("dd-dd-dd-dd-dd-dd");
            add(arpBaitVo1);
            ArpBaitVo arpBaitVo2 = new ArpBaitVo();
            arpBaitVo2.setId(1234);
            arpBaitVo2.setIp("1.2.3.4");
            arpBaitVo2.setMac("dd-dd-dd-dd-dd-dd");
            add(arpBaitVo2);
        }});
        baitTemplateVo.setFileBaitList(new ArrayList<FileBaitVo>() {{
            FileBaitVo fileBaitVo1 = new FileBaitVo();
            fileBaitVo1.setId(123);
            fileBaitVo1.setPath("/test");
            add(fileBaitVo1);
            FileBaitVo fileBaitVo2 = new FileBaitVo();
            fileBaitVo2.setId(1234);
            fileBaitVo2.setPath("/test");
            add(fileBaitVo2);
        }});
        baitTemplateVo.setProcessBaitList(new ArrayList<ProcessBaitVo>() {{
            ProcessBaitVo processBaitVo1 = new ProcessBaitVo();
            processBaitVo1.setId(123);
            processBaitVo1.setCommand("df -h");
            add(processBaitVo1);
            ProcessBaitVo processBaitVo2 = new ProcessBaitVo();
            processBaitVo2.setId(123);
            processBaitVo2.setCommand("df -h");
            add(processBaitVo2);
        }});
        return baitTemplateVo;
    }


    public static BaitTemplateEntity convertToBaitTemplateEntity(BaitTemplateVo item) {
        if (item == null) {
            return null;
        }
        BaitTemplateEntity result = new BaitTemplateEntity();
        result.setId(item.getId().intValue());
        result.setName(item.getName());
        result.setOsType(item.getOsType());
        List<FileBaitVo> fileBaitList = item.getFileBaitList();
        if (fileBaitList == null) {
            result.setFileBaitList(null);
        } else {
            result.setFileBaitList(fileBaitList.stream().map(BeanUtilsTest::convertToFileBaitEntity).collect(Collectors.toList()));
        }
        List<ArpBaitVo> arpBaitList = item.getArpBaitList();
        if (arpBaitList == null) {
            result.setArpBaitList(null);
        } else {
            result.setArpBaitList(arpBaitList.stream().map(BeanUtilsTest::convertToArpBaitEntity).collect(Collectors.toList()));
        }
        List<ProcessBaitVo> processBaitList = item.getProcessBaitList();
        if (processBaitList == null) {
            result.setProcessBaitList(null);
        } else {
            result.setProcessBaitList(processBaitList.stream().map(BeanUtilsTest::convertToProcessBaitEntity).collect(Collectors.toList()));
        }
        return result;
    }

    public static ProcessBaitEntity convertToProcessBaitEntity(ProcessBaitVo item) {
        if (item == null) {
            return null;
        }
        ProcessBaitEntity result = new ProcessBaitEntity();
        result.setId(item.getId());
        result.setCommand(item.getCommand());
        return result;
    }

    public static ArpBaitEntity convertToArpBaitEntity(ArpBaitVo item) {
        if (item == null) {
            return null;
        }
        ArpBaitEntity result = new ArpBaitEntity();
        result.setId(item.getId());
        result.setIp(item.getIp());
        result.setMac(item.getMac());
        return result;
    }

    public static FileBaitEntity convertToFileBaitEntity(FileBaitVo item) {
        if (item == null) {
            return null;
        }
        FileBaitEntity result = new FileBaitEntity();
        result.setId(item.getId());
        result.setPath(item.getPath());
        return result;
    }
}
