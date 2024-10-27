package com.sondertara.common.text.fragment;

import com.sondertara.common.base.Emptys;
import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.iter.UnmodifiableIterator;
import com.sondertara.common.exception.ExceptionUtils;
import com.sondertara.common.io.IOUtils;
import com.sondertara.common.io.file.Files;
import com.sondertara.common.io.resource.Resource;
import com.sondertara.common.logging.Loggers;
import com.sondertara.common.regex.RegexUtils;
import com.sondertara.common.regex.Regexp;
import com.sondertara.common.regex.RegexpMatcher;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于单行、多行文本拆分
 *
 *  */
public class TextLinesFragment extends UnmodifiableIterator<String> implements Closeable {
    private static final Logger logger = Loggers.getLogger(TextLinesFragment.class);

    @NonNull
    private BufferedReader reader;

    @Nullable
    private MultilineConfig multiline;

    /**
     * 当 multiline 不为 null 时，会有 该属性
     */
    @Nullable
    private Regexp regexp;

    /**
     * end of stream
     */
    private boolean eof = false;

    /**
     * 下一条记录
     */
    private String nextRecord;

    private String flagLine;
    private List<String> nonFlagLines = new ArrayList<>();

    public TextLinesFragment(String filepath) {
        this(Files.openInputStream(new File(filepath)));
    }

    public TextLinesFragment(Resource resource) throws IOException {
        this(resource.getInputStream());
    }


    public TextLinesFragment(InputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream)));
    }

    public void setMultiline(MultilineConfig multiline) {
        if (multiline != null) {
            String pattern = multiline.getPattern();
            if (ObjectUtils.isNotEmpty(pattern)) {
                this.multiline = multiline;
            } else {
                this.multiline = null;
                this.regexp = null;
                logger.warn("invalid multiline.pattern: {}", multiline);
            }
        }

        if (this.multiline != null) {
            this.regexp = RegexUtils.createRegexp(this.multiline.getPattern());
        }
    }

    @Override
    public boolean hasNext() {
        if (!eof) {
            this.nextRecord = this.readRecord();
            return ObjectUtils.isNotNull(this.nextRecord);
        } else {
            IOUtils.close(this);
            return false;
        }
    }

    public String next() {
        String r = this.nextRecord;
        this.nextRecord = null;
        return r;
    }

    private String readLine() {
        String line;
        try {
            line = this.reader.readLine();
        } catch (IOException e) {
            throw ExceptionUtils.wrapAsRuntimeException(e);
        }
        return line;
    }

    private String readRecord() {
        String record = null;
        if (this.multiline == null) {
            record = readLine();

            if (record == null) {
                this.eof = true;
            }

            return record;
        }

        while (record == null) {
            String line = readLine();

            if (line == null) {
                if (ObjectUtils.isNotEmpty(this.nonFlagLines)) {
                    record = concatMultiline();
                    this.nonFlagLines.clear();
                }
                this.eof = true;
                return record;
            }

            RegexpMatcher matcher = this.regexp.matcher(line);
            boolean matches = matcher.matches();
            // 不对 pattern 取反
            if (!this.multiline.isNegate()) {
                // 找到了一个新的不匹配行
                if (!matches) {
                    if (this.multiline.getMatch() == MultilineConfig.Match.BEFORE) {
                        // 加在 下一个 不匹配的行 之前
                        this.flagLine = line;
                        record = concatMultiline();
                    } else {
                        // 加在 上一个 不匹配的行 之后
                        record = concatMultiline();
                        this.flagLine = line;
                    }
                    this.nonFlagLines.clear();
                } else {
                    this.nonFlagLines.add(line);
                }
            } else {
                // 对pattern 取反


                if (!matches) {
                    this.nonFlagLines.add(line);
                } else {
                    // 找到了一个新的 匹配行
                    if (this.multiline.getMatch() == MultilineConfig.Match.BEFORE) {
                        // 加在 下一个匹配的行之前
                        this.flagLine = line;
                        record = concatMultiline();
                    } else {
                        // 加在 上一个匹配的行之后
                        record = concatMultiline();
                        this.flagLine = line;
                    }
                    this.nonFlagLines.clear();
                }
            }
        }
        return record;
    }


    private String concatMultiline() {
        if (Emptys.isAllEmpty(this.flagLine, this.nonFlagLines)) {
            return null;
        }
        String noFlagLinesString = ObjectUtils.isEmpty(this.nonFlagLines) ? "" : StringUtils.join(this.multiline.getConcatSeparator(), this.nonFlagLines);

        StringBuilder builder = new StringBuilder(noFlagLinesString.length() + 255);
        if (this.multiline.getMatch() == MultilineConfig.Match.BEFORE) {

            if (StringUtils.isNotEmpty(noFlagLinesString)) {
                builder.append(noFlagLinesString);
            }

            boolean addSeparator = Emptys.isNoneEmpty(noFlagLinesString, this.flagLine);
            if (addSeparator) {
                builder.append(this.multiline.getConcatSeparator());
            }

            if (StringUtils.isNotEmpty(this.flagLine)) {
                builder.append(this.flagLine);
            }
        } else {

            if (StringUtils.isNotEmpty(this.flagLine)) {
                builder.append(this.flagLine);
            }

            boolean addSeparator = Emptys.isNoneEmpty(noFlagLinesString, this.flagLine);
            if (addSeparator) {
                builder.append(this.multiline.getConcatSeparator());
            }

            if (StringUtils.isNotEmpty(noFlagLinesString)) {
                builder.append(noFlagLinesString);
            }

        }
        return builder.toString();
    }

    @Override
    public void close() throws IOException {
        if (this.reader != null) {
            IOUtils.close(this.reader);
            this.reader = null;
        }
    }
}
