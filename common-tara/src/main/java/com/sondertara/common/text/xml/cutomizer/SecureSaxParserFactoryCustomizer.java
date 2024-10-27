package com.sondertara.common.text.xml.cutomizer;

import com.sondertara.common.base.Customizer;
import com.sondertara.common.text.xml.Xmls;

import javax.xml.parsers.SAXParserFactory;

/**
 *  */
public class SecureSaxParserFactoryCustomizer implements Customizer<SAXParserFactory> {

    @Override
    public void customize(SAXParserFactory factory) {
        Xmls.setFeature(factory, "http://apache.org/xml/features/disallow-doctype-decl", true);

        Xmls.setFeature(factory, "http://xml.org/sax/features/external-general-entities", false);
        Xmls.setFeature(factory, "http://xml.org/sax/features/external-parameter-entities", false);
    }
}
