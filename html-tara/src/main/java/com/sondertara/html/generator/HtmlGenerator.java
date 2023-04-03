package com.sondertara.html.generator;

import com.sondertara.common.exception.TaraException;
import com.sondertara.common.io.FileUtils;
import com.sondertara.common.io.IoUtils;
import com.sondertara.common.lang.Pair;
import com.sondertara.common.regex.PatternPool;
import com.sondertara.common.time.DatePattern;
import com.sondertara.common.util.LocalDateTimeUtils;
import com.sondertara.common.util.RegexUtils;
import com.sondertara.common.util.StringUtils;
import j2html.Config;
import j2html.tags.specialized.HeadTag;
import j2html.utils.EscapeUtil;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import static j2html.TagCreator.head;
import static j2html.TagCreator.link;
import static j2html.TagCreator.meta;
import static j2html.TagCreator.meter;
import static j2html.TagCreator.title;

/**
 * @author huangxiaohu
 */
@Slf4j
public class HtmlGenerator {

    static final String CJK_FONT = "NotoSansCJKsc-Regular.ttf";
  public   static final String WORK_DIR = System.getProperty("user.home") + File.separator + ".tara_html";

    static void checkEnv() {
        if (!Files.exists(Paths.get(WORK_DIR, CJK_FONT))) {
            throw new TaraException(404, "Could not find work directory,maybe forget copy the momentous files to {}", WORK_DIR);
        }
    }

    /**
     * create html str
     *
     * @param function the html build function
     * @return html str
     */
    public static String create(Function<HeadTag, String> function) {
        checkEnv();
        //Config.textEscaper = text -> text;
        Config.closeEmptyTags = true;
        try {
            HeadTag head = head(meta().attr("charset", "UTF-8"), link().withHref("../common.css").withRel("stylesheet"));
            return function.apply(head);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * export html file
     *
     * @param filePath the filepath with suffix
     * @param function the html build function
     * @throws IOException e
     */
    public static void export(String filePath, Function<HeadTag, String> function) throws IOException {
        String date = LocalDateTimeUtils.now(DatePattern.NORM_DATE_PATTERN);
        String baseDir = Paths.get(WORK_DIR, date).toString();
        String html = create(function);
        Document document = Jsoup.parse(html);
        document.setBaseUri(baseDir);
        Element head = document.head();
        for (Element child : head.children()) {
            System.out.println(child);
            String nodeName = child.nodeName();
            if ("link".equals(nodeName)) {
                Attributes attributes = child.attributes();
                String rel = attributes.get("rel");
                if ("stylesheet".equals(rel)) {
                    String href = attributes.get("href");
                    String css = null;
                    try {
                        Path path = Paths.get(href);
                        Path resolve;
                        if (!path.isAbsolute()) {

                            Pair<Integer, Integer> pair = StringUtils.countAndIndex(path.toString(), ".." + File.separator);
                            String name = path.toString().substring(Math.max(0, pair.getValue()));
                            String parent = FileUtils.getParent(baseDir, pair.getKey());
                            resolve = Paths.get(parent).resolve(name);
                        } else {
                            resolve = path;
                        }
                        css = IoUtils.copyToString(Files.newInputStream(resolve), StandardCharsets.UTF_8);

                    } catch (InvalidPathException e) {
                        if (RegexUtils.isMatch(PatternPool.URL_HTTP, href)) {
                            css = Unirest.get(href).asString().getBody();
                        }
                    }
                    if (css == null) {
                        throw new TaraException("Can not parse the stylesheet href for [{}]", href);
                    }
                    Element element = new Element("style");
                    element.appendText(css);
                    head.appendChild(element);
                    child.remove();
                }
            }
        }

        String outerHtml = document.outerHtml();
        outerHtml = StringEscapeUtils.unescapeHtml4(outerHtml);
        IoUtils.copy(outerHtml.getBytes(StandardCharsets.UTF_8), Files.newOutputStream(Paths.get(filePath)));

    }
}