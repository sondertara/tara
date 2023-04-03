package com.sondertara;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sondertara.common.io.IoUtils;
import com.sondertara.common.lang.Pair;
import com.sondertara.common.lang.id.NanoId;
import com.sondertara.html.components.Card;
import com.sondertara.html.components.Descriptions;
import com.sondertara.html.components.ImageView;
import com.sondertara.html.components.table.RawTableView;
import com.sondertara.html.components.table.TableCol;
import com.sondertara.html.components.table.TableView;
import com.sondertara.html.generator.HtmlGenerator;
import com.sondertara.html.generator.PdfGenerator;
import j2html.tags.Text;
import j2html.tags.specialized.BodyTag;
import j2html.tags.specialized.HtmlTag;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static j2html.TagCreator.body;
import static j2html.TagCreator.document;
import static j2html.TagCreator.html;

/**
 * @author huangxiaohu
 */
public class HtmlExample {


    public static void main(String[] args) throws IOException {

        PdfGenerator.create(IoUtils.copyToString(Files.newInputStream(Paths.get("O:\\workspace\\java\\itfb\\.tara_html\\新一代电子不停车收费系统-宁夏系统基础设施可研方案审核情况汇报.html")),StandardCharsets.UTF_8),"11111.pdf");
    }

    //    String docs = HtmlGenerator.create((headTag) -> {
    //        //===============================基本信息====================================================
    //        List<Pair<String, String>> tbodyList = new ArrayList<>();
    //        tbodyList.add(Pair.of("项目名称", "基于国产高端容错计算机的行业应用系统关键技术研究与应用示范(201300150)"));
    //        tbodyList.add(Pair.of("项目编号", "201300150"));
    //        tbodyList.add(Pair.of("可研原因", "现有逻辑子系统下新建物理子系统"));
    //        tbodyList.add(Pair.of("紧急程度", "201300150"));
    //
    //        tbodyList.add(Pair.of("项目编号1", "201300150"));
    //        tbodyList.add(Pair.of("项目编号2", "201300150"));
    //        tbodyList.add(Pair.of("项目编号3", "201300150"));
    //        tbodyList.add(Pair.of("项目编号4", "201300150"));
    //        tbodyList.add(Pair.of("项目编号5", "201300150"));
    //        tbodyList.add(Pair.of("项目编号6", "201300150"));
    //        tbodyList.add(Pair.of("项目编号7", "201300150"));
    //        tbodyList.add(Pair.of("项目编号8", "201300150"));
    //        Descriptions descriptions = Descriptions.builder().colSize(3).items(tbodyList).build();
    //
    //        Card basicInfoCard = Card.builder().title("基本信息").content(descriptions).build();
    //        //===============================架构试图====================================================
    //        ImageView imageView = ImageView.builder().path("http://128.192.23.170:8090/images/2/7/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20220415094431.png").build();
    //        Card structCard = Card.builder().title("架构视图").content(imageView).build();
    //
    //        //===============================审核意见====================================================
    //        List<TableCol> columns = new ArrayList<>();
    //        columns.add(TableCol.builder().name("checkOfficeClassify").autoMerge(true).build());
    //        columns.add(TableCol.builder().name("checkCode").build());
    //        columns.add(TableCol.builder().name("isSatisfy").validator(s -> {
    //            if ("否".equals(s)) {
    //                return false;
    //            }
    //            return true;
    //        }).build());
    //        columns.add(TableCol.builder().name("checkVerdict").build());
    //        columns.add(TableCol.builder().name("checkExplain").build());
    //
    //        ObjectMapper mapper = new ObjectMapper();
    //        FileInputStream fileInputStream = null;
    //        List<Map<String, Object>> maps = new ArrayList<>();
    //        try {
    //            fileInputStream = new FileInputStream("F:\\workspace\\java\\cloudterm-master\\1.json");
    //            maps = mapper.readValue(fileInputStream, new TypeReference<List<Map<String, Object>>>() {
    //            });
    //        } catch (Exception e) {
    //            throw new RuntimeException(e);
    //        }
    //
    //
    //        List<String> footData = Arrays.asList("审核处室", "网络处经办人可研审核(洋桥)", "审核人", "宋梦婷", "审核处长", "欧阳锋");
    //
    //        List<List<String>> data = new ArrayList<>();
    //        data.add(footData);
    //        RawTableView footer = RawTableView.builder().data(data).withClass("background-light-blue").build();
    //        TableView tableView = TableView.builder().columns(columns).hasTitle(true).data(maps).footer(footer).build();
    //
    //        Card reviewCard = Card.builder().title("审核意见").content(tableView).build();
    //
    //        //==================================组装================================================
    //        BodyTag bodyTag = body(basicInfoCard, structCard, reviewCard);
    //        HtmlTag html = html(headTag, bodyTag).withLang("en");
    //        String document = document(html);
    //        try {
    //            OutputStream outputStream = Files.newOutputStream(Paths.get(System.getProperty("user.dir"), "export.html"));
    //            IoUtils.copy(document.getBytes(StandardCharsets.UTF_8), outputStream);
    //        } catch (IOException e) {
    //            throw new RuntimeException(e);
    //        }
    //
    //        return document;
    //    });
    //    PdfGenerator.create(docs, Paths.get("export_" + NanoId.randomNanoId() + ".pdf").toString());
    //}



}