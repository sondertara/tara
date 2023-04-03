package com.sondertara.html.generator;

import com.openhtmltopdf.css.parser.CSSPrimitiveValue;
import com.openhtmltopdf.css.parser.property.PageSize;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.sondertara.common.io.IoUtils;
import com.sondertara.common.time.DatePattern;
import com.sondertara.common.util.LocalDateTimeUtils;
import j2html.utils.EscapeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.sondertara.html.generator.HtmlGenerator.WORK_DIR;
import static com.sondertara.html.generator.HtmlGenerator.checkEnv;

/**
 * @author huangxiaohu
 */

@Slf4j
public class PdfGenerator {

    /**
     * create pdf by html str
     *
     * @param html     the html  str
     * @param destFile the  dest file path
     */
    public static void create(String html, String destFile) {

        try {
            checkEnv();
            PdfRendererBuilder pdfRendererBuilder = new PdfRendererBuilder();
            //pdfRendererBuilder.useFastMode();
            pdfRendererBuilder.useDefaultPageSize(297, 420, BaseRendererBuilder.PageSizeUnits.MM);
            String date = LocalDateTimeUtils.now(DatePattern.NORM_DATE_PATTERN);
            String baseDir = Paths.get(WORK_DIR, date).toUri().toString();
            pdfRendererBuilder.withHtmlContent(html, baseDir);
            pdfRendererBuilder.toStream(Files.newOutputStream(Paths.get(destFile)));
            pdfRendererBuilder.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * create pdf by html str
     *
     * @param html     the html  str
     * @param pageSize the custom pageSize
     * @param destFile the  dest file path
     */
    public static void create(String html, String destFile, PageSize pageSize) {
        try {
            checkEnv();
            String date = LocalDateTimeUtils.now(DatePattern.NORM_DATE_PATTERN);
            PdfRendererBuilder pdfRendererBuilder = new PdfRendererBuilder();
            pdfRendererBuilder.useFastMode();
            pdfRendererBuilder.useDefaultPageSize(pageSize.getPageWidth().getFloatValue(CSSPrimitiveValue.CSS_MM), pageSize.getPageHeight().getFloatValue(CSSPrimitiveValue.CSS_MM), BaseRendererBuilder.PageSizeUnits.MM);
            String baseDir = Paths.get(WORK_DIR, date).toUri().toString();
            pdfRendererBuilder.withHtmlContent(html, baseDir);
            pdfRendererBuilder.toStream(Files.newOutputStream(Paths.get(destFile)));
            pdfRendererBuilder.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {

        String escape = EscapeUtil.escape("[\"AQCP02\",\"AQCP11\"]");

        System.out.println(escape);
        Document document = Jsoup.parse(new File("O:\\workspace\\java\\tara\\.tara_html\\2022-11-16\\新一代电子不停车收费系统-宁夏系统基础设施可研方案审核情况汇报.html"), StandardCharsets.UTF_8.toString(), Paths.get(WORK_DIR, LocalDateTimeUtils.now(DatePattern.PURE_DATE_PATTERN)).toString());

        //for (Element element : document.body().getElementsByTag("td")) {
        //    String text = element.text();
        //    System.out.println(text);
        //
        //    //element.empty().appendText(StringEscapeUtils.escapeHtml3(text));
        //}

        System.out.println(document.body().html());

        String copy = IoUtils.copyToString(Files.newInputStream(Paths.get("O:\\workspace\\java\\tara\\.tara_html\\2022-11-16\\云管理平台基础设施可研方案审核情况汇报.html")), StandardCharsets.UTF_8);
        create(copy ,"11111.pdf",PageSize.B3);
    }
}
