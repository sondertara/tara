package com.sondertara.common.text.xml.errorhandler;

import com.sondertara.common.logging.Loggers;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class LogErrorHandler implements org.xml.sax.ErrorHandler {

    @Override
    public void warning(final SAXParseException exception) throws SAXException {
        Loggers.getLogger(getClass()).warn("parse fail", exception);
    }

    @Override
    public void error(final SAXParseException exception) throws SAXException {
        Loggers.getLogger(getClass()).error("parse fail", exception);
    }

    @Override
    public void fatalError(final SAXParseException exception) throws SAXException {
        Loggers.getLogger(getClass()).error("parse fail", exception);
    }
}