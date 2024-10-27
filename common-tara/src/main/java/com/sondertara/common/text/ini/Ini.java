package com.sondertara.common.text.ini;


import com.sondertara.common.base.Emptys;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.configuration.ConfigurationException;
import com.sondertara.common.io.IOUtils;
import com.sondertara.common.io.resource.Resources;
import com.sondertara.common.logging.Loggers;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * <pre>
 * [section name]
 * a1=1
 * a2=2
 * ;this is a comment
 * #this is a comment
 * </pre>
 */
public class Ini implements Map<String, Ini.Section> {
    private static final String DEFAULT_SECTION_NAME = "";
    private static final String COMMENT_POUND = "#";
    private static final String COMMENT_SEMICOLON = ";";
    private static final String SECTION_PREFIX = "[";
    private static final String SECTION_SUFFIX = "]";
    private static final char ESCAPE_TOKEN = '\\';
    private final Map<String, Section> sections;

    public Ini() {
        this.sections = new LinkedHashMap<String, Section>();
    }

    public Ini(Ini defaults) {
        this();
        if (defaults == null) {
            throw new NullPointerException("Defaults cannot be null.");
        } else {
            CollectionUtils.forEach(defaults.sections, new Consumer<Section>() {
                @Override
                public void accept(Section section) {
                    Section copy = new Section(section);
                    Ini.this.sections.put(section.getName(), copy);
                }
            });
        }
    }

    private static String cleanName(String sectionName) {
        return StringUtils.useValueIfBlank(sectionName, "").trim();
    }

    public static Ini fromResourcePath(String resourcePath) throws ConfigurationException {
        if (!StringUtils.isNotEmpty(resourcePath)) {
            throw new IllegalArgumentException("Resource Path argument cannot be null or empty.");
        } else {
            Ini ini = new Ini();
            ini.loadFile(resourcePath);
            return ini;
        }
    }

    protected static boolean isSectionHeader(String line) {
        String s = StringUtils.useValueIfBlank(line, "");
        return s.startsWith(SECTION_PREFIX) && s.endsWith(SECTION_SUFFIX);
    }

    protected static String getSectionName(String line) {
        String s = StringUtils.useValueIfBlank(line, "");
        return isSectionHeader(s) ? cleanName(s.substring(1, s.length() - 1)) : null;
    }

    public boolean isEmpty() {
        Collection<Section> sections = this.sections.values();
        if (!sections.isEmpty()) {
            return CollectionUtils.allMatch(sections, new Predicate<Section>() {
                @Override
                public boolean test(Section section) {
                    return section.isEmpty();
                }
            });
        }

        return true;
    }

    public Set<String> getSectionNames() {
        return Collections.unmodifiableSet(this.sections.keySet());
    }

    public Collection<Section> getSections() {
        return Collections.unmodifiableCollection(this.sections.values());
    }

    public Section getSection(String sectionName) {
        String name = cleanName(sectionName);
        return this.sections.get(name);
    }
    public Section createSectionIfAbsent(String sectionName) {
        return createSectionIfAbsent(sectionName, null);
    }
    public Section createSectionIfAbsent(String sectionName, Section s) {
        String name = cleanName(sectionName);
        Section section = this.getSection(name);
        if (section == null) {
            section = new Section(name);
            this.sections.put(name, section);
        }
        if(s!=null) {
            section.putAll(s);
        }
        return section;
    }

    public Section removeSection(String sectionName) {
        String name = cleanName(sectionName);
        return this.sections.remove(name);
    }

    public void setSectionProperty(String sectionName, String propertyName, String propertyValue) {
        String name = cleanName(sectionName);
        Section section = this.getSection(name);
        if (section == null) {
            section = this.createSectionIfAbsent(name);
        }

        section.put(propertyName, propertyValue);
    }

    public String getSectionProperty(String sectionName, String propertyName) {
        Section section = this.getSection(sectionName);
        return section != null ? section.get(propertyName) : null;
    }

    public String getSectionProperty(String sectionName, String propertyName, String defaultValue) {
        String value = this.getSectionProperty(sectionName, propertyName);
        return value != null ? value : defaultValue;
    }

    public void loadFile(String resourcePath) throws ConfigurationException {
        InputStream is = null;
        try {
            is = Resources.loadFileResource(resourcePath).getInputStream();
            this.load(is);
        } catch (IOException ioe) {
            throw new ConfigurationException(ioe);
        } finally {
            IOUtils.close(is);
        }

    }

    public void load(String iniConfig) throws ConfigurationException {
        this.load(new StringReader(iniConfig));
    }

    public void load(InputStream is) throws ConfigurationException {
        if (is == null) {
            throw new NullPointerException("InputStream argument cannot be null.");
        } else {
            InputStreamReader isr;
            try {
                isr = new InputStreamReader(is, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                throw new ConfigurationException(ex);
            }

            this.load(isr);
        }
    }


    public void load(Reader reader) {
        String sectionName = "";
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder sectionContent = new StringBuilder();

        String rawLine;
        Logger logger = Loggers.getLogger(getClass());
        try {
            while ((rawLine = bufferedReader.readLine()) != null) {
                String line = StringUtils.trim(rawLine);
                if (!line.startsWith(COMMENT_POUND) && !line.startsWith(COMMENT_SEMICOLON)) {
                    String newSectionName = getSectionName(line);
                    if (newSectionName != null) {
                        this.addSectionIfAbsent(sectionName, sectionContent);
                        sectionContent = new StringBuilder();
                        sectionName = newSectionName;
                        if (logger.isDebugEnabled()) {
                            logger.debug("Parsing [{}]",newSectionName);
                        }
                    } else {
                        sectionContent.append(rawLine).append("\n");
                    }
                }
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }

        this.addSectionIfAbsent(sectionName, sectionContent);
    }

    public void merge(Map<String, Section> m) {
        CollectionUtils.forEach(m, new BiConsumer<String, Section>() {
            @Override
            public void accept(String key, Section section) {
                Ini.this.createSectionIfAbsent(key,section);
            }
        });

    }

    private void addSectionIfAbsent(String name, StringBuilder content) {
        if (content.length() > 0) {
            String contentString = content.toString();
            String cleaned = StringUtils.trim(contentString);
            if (StringUtils.isNotEmpty(cleaned)) {
                Section section = new Section(name, contentString);
                if (!section.isEmpty()) {
                    this.sections.put(name, section);
                }
            }
        }

    }


    public boolean equals(Object obj) {
        if (obj instanceof Ini) {
            Ini ini = (Ini) obj;
            return this.sections.equals(ini.sections);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.sections.hashCode();
    }

    public String toString() {
        if (Emptys.isNotEmpty(this.sections)) {
            final StringBuilder sb = new StringBuilder(256);
            CollectionUtils.forEach(this.sections, new BiConsumer<String, Section>() {
                @Override
                public void accept(String sectionName, Section section) {
                    sb.append(SECTION_PREFIX).append(sectionName).append(SECTION_SUFFIX).append("\n");
                    CollectionUtils.forEach(section.props, new BiConsumer<String, String>() {
                        @Override
                        public void accept(String key, String value) {
                            sb.append(key).append("=").append(value).append("\n");
                        }
                    });
                    sb.append("\n");
                }
            });

            return sb.toString();
        } else {
            return "<empty INI>";
        }
    }

    public int size() {
        return this.sections.size();
    }

    public boolean containsKey(Object key) {
        return this.sections.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return this.sections.containsValue(value);
    }

    public Section get(Object key) {
        return this.sections.get(key);
    }

    public Section put(String key, Section value) {
        return this.sections.put(key, value);
    }

    public Section remove(Object key) {
        return this.sections.remove(key);
    }

    public void putAll(Map<? extends String, ? extends Section> m) {
        this.sections.putAll(m);
    }

    public void clear() {
        this.sections.clear();
    }

    public Set<String> keySet() {
        return Collections.unmodifiableSet(this.sections.keySet());
    }

    public Collection<Section> values() {
        return Collections.unmodifiableCollection(this.sections.values());
    }

    public Set<Entry<String, Section>> entrySet() {
        return Collections.unmodifiableSet(this.sections.entrySet());
    }

    public static class Section implements Map<String, String> {
        private final String name;
        private final Map<String, String> props;

        private Section(String name) {
            if (name == null) {
                throw new NullPointerException("name");
            } else {
                this.name = name;
                this.props = new LinkedHashMap<String, String>();
            }
        }

        private Section(String name, String sectionContent) {
            if (name == null) {
                throw new NullPointerException("name");
            } else {
                this.name = name;
                Map<String, String> props;
                if (StringUtils.isNotBlank(sectionContent)) {
                    props = toMapProps(sectionContent);
                } else {
                    props = new LinkedHashMap<String, String>();
                }

                this.props = props;

            }
        }

        private Section(Section defaults) {
            this(defaults.getName());
            this.putAll(defaults.props);
        }

        protected static boolean isContinued(String line) {
            if (StringUtils.isBlank(line)) {
                return false;
            } else {
                int length = line.length();
                int backslashCount = 0;

                for (int i = length - 1; i > 0 && line.charAt(i) == ESCAPE_TOKEN; --i) {
                    ++backslashCount;
                }

                return backslashCount % 2 != 0;
            }
        }

        private static boolean isKeyValueSeparatorChar(char c) {
            return Character.isWhitespace(c) || c == ':' || c == '=';
        }

        private static boolean isCharEscaped(CharSequence s, int index) {
            return index > 0 && s.charAt(index) == ESCAPE_TOKEN;
        }

        protected static String[] splitKeyValue(String keyValueLine) {
            String line = StringUtils.useValueIfBlank(keyValueLine, null);
            if (line == null) {
                return Emptys.EMPTY_STRINGS;
            } else {
                StringBuilder keyBuffer = new StringBuilder();
                StringBuilder valueBuffer = new StringBuilder();
                boolean buildingKey = true;

                for (int i = 0; i < line.length(); ++i) {
                    char c = line.charAt(i);
                    if (buildingKey) {
                        if (isKeyValueSeparatorChar(c) && !isCharEscaped(line, i)) {
                            buildingKey = false;
                        } else if (!isCharEscaped(line, i)) {
                            keyBuffer.append(c);
                        }
                    } else if (valueBuffer.length() != 0 || !isKeyValueSeparatorChar(c) || isCharEscaped(line, i)) {
                        valueBuffer.append(c);
                    }
                }

                String key = StringUtils.trim(keyBuffer.toString());
                String value = StringUtils.trim(valueBuffer.toString());
                if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
                    Logger logger = Loggers.getLogger(Ini.class);
                    logger.trace("Discovered key/value pair: {} = {}", key, value);
                    return new String[]{key, value};
                } else {
                    String msg = "Line argument must contain a key and a value.  Only one string token was found.";
                    throw new IllegalArgumentException(msg);
                }
            }
        }

        private static Map<String, String> toMapProps(String content) {
            Map<String, String> props = new LinkedHashMap<String, String>();
            StringBuilder lineBuffer = new StringBuilder();
            Scanner scanner = null;
            try {
                scanner = new Scanner(content);

                while (scanner.hasNextLine()) {
                    String line = StringUtils.trim(scanner.nextLine());
                    if (isContinued(line)) {
                        line = line.substring(0, line.length() - 1);
                        lineBuffer.append(line);
                    } else {
                        lineBuffer.append(line);
                        line = lineBuffer.toString();
                        lineBuffer = new StringBuilder();
                        String[] kvPair = splitKeyValue(line);
                        if (kvPair != null && kvPair.length == 2) {
                            props.put(kvPair[0], kvPair[1]);
                        }
                    }
                }
            }finally {
                IOUtils.close(scanner);
            }

            return props;
        }

        public String getName() {
            return this.name;
        }

        public void clear() {
            this.props.clear();
        }

        public boolean containsKey(Object key) {
            return this.props.containsKey(key);
        }

        public boolean containsValue(Object value) {
            return this.props.containsValue(value);
        }

        public Set<Entry<String, String>> entrySet() {
            return this.props.entrySet();
        }

        public String get(Object key) {
            return this.props.get(key);
        }

        public boolean isEmpty() {
            return this.props.isEmpty();
        }

        public Set<String> keySet() {
            return this.props.keySet();
        }

        public String put(String key, String value) {
            return this.props.put(key, value);
        }

        public void putAll(Map<? extends String, ? extends String> m) {
            this.props.putAll(m);
        }

        public String remove(Object key) {
            return this.props.remove(key);
        }

        public int size() {
            return this.props.size();
        }

        public Collection<String> values() {
            return this.props.values();
        }

        public String toString() {
            String name = this.getName();
            return "".equals(name) ? "<default>" : name;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Section)) {
                return false;
            } else {
                Section other = (Section) obj;
                return this.getName().equals(other.getName()) && this.props.equals(other.props);
            }
        }

        public int hashCode() {
            return this.name.hashCode() * 31 + this.props.hashCode();
        }
    }
}
