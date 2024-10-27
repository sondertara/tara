package com.sondertara.common.text.xml.resolver;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NullEntityResolver implements EntityResolver {
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
        return new InputSource(new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes(StandardCharsets.UTF_8)));
    }
}