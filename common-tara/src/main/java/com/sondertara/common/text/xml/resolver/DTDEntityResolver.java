package com.sondertara.common.text.xml.resolver;

import com.sondertara.common.io.IOUtils;
import com.sondertara.common.logging.Loggers;
import org.slf4j.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public class DTDEntityResolver implements EntityResolver {
    private static final Logger logger = Loggers.getLogger(DTDEntityResolver.class);
    private StringReader dtdReader;

    public DTDEntityResolver(final InputStream dtdInputStream) {
        setDtdInputStream(dtdInputStream);
    }

    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
        InputSource source = null;
        try {
            File file = null;
            if (systemId != null && !systemId.isEmpty()) {
                file = new File(systemId);
            } else if (publicId != null && !publicId.isEmpty()) {
                file = new File(publicId);
            }
            if (file != null && file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                setDtdInputStream(fis);
            }
            if (this.dtdReader != null) {
                return new InputSource(this.dtdReader);
            }
            source = new InputSource(new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            // ignore it
        }
        return source;
    }

    void setDtdInputStream(final InputStream dtdInputStream) {
        try {
            String content = IOUtils.readAsString(dtdInputStream);
            StringReader reader = new StringReader(content);
            this.dtdReader = reader;
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        } finally {
            IOUtils.close(dtdInputStream);
        }

    }
}