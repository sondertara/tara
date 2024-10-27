package com.sondertara.common.text.xml.cutomizer;

import com.sondertara.common.base.Customizer;
import com.sondertara.common.text.xml.Xmls;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;

/**
 *  */
public class SecureXmlInputFactoryCustomizer implements Customizer<XMLInputFactory> {

    public void customize(XMLInputFactory factory) {
        Xmls.setProperty(factory, XMLInputFactory.SUPPORT_DTD, false);
        Xmls.setProperty(factory, XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        Xmls.setProperty(factory, XMLConstants.ACCESS_EXTERNAL_DTD, false);
        Xmls.setProperty(factory, XMLConstants.ACCESS_EXTERNAL_SCHEMA, false);
    }
}
