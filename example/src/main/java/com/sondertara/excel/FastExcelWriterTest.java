package com.sondertara.excel;

import com.sondertara.excel.domain.ExcelHeaderVo;
import com.sondertara.excel.fast.writer.BorderStyle;
import com.sondertara.excel.fast.writer.Color;
import com.sondertara.excel.fast.writer.FastWorkbook;
import com.sondertara.excel.fast.writer.Worksheet;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FastExcelWriterTest {

    @Test
    public void simpleTest() {
        try (OutputStream os = Files.newOutputStream(Paths.get("Fast-test.xlsx"))) {
            FastWorkbook wb = new FastWorkbook(os, "MyApplication", "1.0");
            Worksheet ws = wb.newWorksheet("硬件预算申请表");
            ExcelHeaderVo excelHeaderVo = new ExcelHeaderVo();
            excelHeaderVo.setFillColor(Color.GRAY1);
            {
                ExcelHeaderVo.Element element = new ExcelHeaderVo.Element();
                element.position(0, 0)
                        .name("设备基本信息")
                        .size(4, 1);
                element.addChild(new ExcelHeaderVo.Element().name("名称"));
                element.addChild(new ExcelHeaderVo.Element().name("类型"));
                element.addChild(new ExcelHeaderVo.Element().name("用途"));
                element.addChild(new ExcelHeaderVo.Element().name("型号"));
                element.addChild(new ExcelHeaderVo.Element().name("配置"));
                System.out.println(excelHeaderVo.getStep());
                excelHeaderVo.addElement(element);
            }

            {
                ExcelHeaderVo.Element element = new ExcelHeaderVo.Element().name("服务要求").size(1, 2);
                excelHeaderVo.addElement(element);
            }

            {
                ExcelHeaderVo.Element element = new ExcelHeaderVo.Element()
                        .name("设备基本信息")
                        .size(4, 1);
                element.addChild(new ExcelHeaderVo.Element().name("名称"));
                element.addChild(new ExcelHeaderVo.Element().name("类型"));
                element.addChild(new ExcelHeaderVo.Element().name("用途"));
                ExcelHeaderVo.Element named = new ExcelHeaderVo.Element().name("配置");
                named.addChild(new ExcelHeaderVo.Element().name("测试1"));
                named.addChild(new ExcelHeaderVo.Element().name("测试2"));
                element.addChild(named);
                element.addChild(new ExcelHeaderVo.Element().name("型号"));
                System.out.println(excelHeaderVo.getStep());
                excelHeaderVo.addElement(element);
            }

            for (ExcelHeaderVo.Element element : excelHeaderVo.getElements()) {
                write(element, ws);
                ws.range(element.top(), element.right() - 1, element.bottom() - 1, element.left()).merge();
            }
            ws.range(excelHeaderVo.getTop(), excelHeaderVo.getRight() - 1, excelHeaderVo.getBottom() - 1, excelHeaderVo.getLeft()).style().fillColor(excelHeaderVo.getFillColor()).horizontalAlignment("center").verticalAlignment("center").bold().borderStyle(BorderStyle.MEDIUM).set();

            System.out.println("最后一行:"+ws.getLastRowNum());

//            ws.header("page 1 of ?", Position.RIGHT);
//            ws.value(0, 0, "设备基本信息");
//            ws.value(0, 5, "服务要求");
//            ws.value(0, 6, "预算信息");
//            ws.value(1, 0, "名称");
//            ws.value(1, 1, "类型");
//            ws.value(1, 2, "用途");
//            ws.value(1, 3, "型号");
//            ws.value(1, 4, "配置");
//            ws.value(1, 6, "设备数量");
//            ws.value(1, 7, "数量测量方法");
//            ws.value(1, 8, "设备单价");
//            ws.value(1, 9, "单价依据");
//            ws.range(0, 0, 0, 4).style().merge().set();
//            ws.range(0, 6, 0, 10).style().merge().set();
//            ws.range(0, 5, 1, 5).style().merge().set();
            wb.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void write(ExcelHeaderVo.Element element, Worksheet ws) {
        ws.value(element.getrIndex(), element.getcIndex(), element.getName());
        List<ExcelHeaderVo.Element> children = element.getChildren();
        for (ExcelHeaderVo.Element child : children) {
            ws.value(child.getrIndex(), child.getcIndex(), child.getName());
            ws.range(child.top(), child.right() - 1, child.bottom() - 1, child.left()).merge();
            if (!child.getChildren().isEmpty()) {
                write(child, ws);
            }
//                    ws.range(child.top(), child.right(), child.bottom(), child.left()).merge();
        }
    }
}
