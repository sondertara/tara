package com.sondertara.common.io.file;

import com.sondertara.common.base.Emptys;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.codec.hex.Hex;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.collection.Maps;
import com.sondertara.common.collection.multivalue.LinkedMultiValueMap;
import com.sondertara.common.collection.multivalue.MultiValueMap;
import com.sondertara.common.function.Predicate2;
import com.sondertara.common.io.IOUtils;
import com.sondertara.common.io.resource.Resource;
import com.sondertara.common.io.resource.Resources;
import com.sondertara.common.logging.Loggers;
import com.sondertara.common.text.properties.Props;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;

public class FileTypes {
    private static final MultiValueMap fileTypesMap = new LinkedMultiValueMap<String, String>();

    static {
        init();
    }

    private static void init() {
        try {
            Resource r = Resources.loadClassPathResource("filetypes.properties", FileTypes.class);
            Properties properties = Props.load(r);
            CollectionUtils.forEach(Maps.newStringMap(properties), (BiConsumer<String, String>) (magicCode, typesString) -> {
                String[] types = StringUtils.split(typesString, ",");
                addFileTypes(magicCode, Lists.asList(types));
            });
        } catch (IOException ex) {
            Logger logger = Loggers.getLogger(FileTypes.class);
            logger.warn("Error occur when load filetypes.properties");
        }
    }


    @SuppressWarnings("unchecked")
    public static String getType(final String fileHexHeader) {
        Map.Entry entry = CollectionUtils.findFirst(fileTypesMap, new Predicate2<String, List<String>>() {
            @Override
            public boolean test(String magicCode, List<String> types) {
                return StringUtils.startsWith(fileHexHeader, magicCode, true);
            }
        });
        if (entry != null) {
            List<String> types = (List<String>) entry.getValue();
            if (Emptys.isNotEmpty(types)) {
                return types.get(0);
            }
        }
        return null;
    }

    public static synchronized void addFileTypes(String magicCode, List<String> types) {
        fileTypesMap.addAll(magicCode, types);
    }

    public static synchronized void addFileType(String magicCode, String type) {
        fileTypesMap.add(magicCode, type);
    }

    public static String readFileMagic(File file) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            return readFileMagic(in);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            IOUtils.close(in);
        }
    }

    public static String readFileMagic(InputStream in) {
        try {
            byte[] bytes = new byte[28];
            int length = IOUtils.read(in, bytes);
            if (length > 0) {
                byte[] bs;
                if (length < 28) {
                    bs = new byte[length];
                    System.arraycopy(bytes, 0, bs, 0, length);
                } else {
                    bs = bytes;
                }

                return Hex.encodeHexString(bs, true);
            }
            return null;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }


    public static String getFileType(File file) {
        String fileHexHeader = readFileMagic(file);
        String type = getType(fileHexHeader);
        if (Emptys.isEmpty(type)) {
            return Files.getSuffix(file);
        }
        return type;
    }

    private FileTypes(){

    }
}
