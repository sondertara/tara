package com.sondertara.common.text.xml.cutomizer;

import com.sondertara.common.base.Customizer;
import com.sondertara.common.text.xml.Xmls;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

/**
 *  */
public class SecureSchemaFactoryCustomizer implements Customizer<SchemaFactory> {

    @Override
    public void customize(SchemaFactory factory) {
        Xmls.setFeature(factory, "http://apache.org/xml/features/disallow-doctype-decl", true);
        Xmls.setProperty(factory, XMLConstants.ACCESS_EXTERNAL_DTD, false);
        Xmls.setProperty(factory, XMLConstants.ACCESS_EXTERNAL_SCHEMA, false);
    }
}
