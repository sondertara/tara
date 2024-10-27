package com.sondertara.common.text.xml.cutomizer;

import com.sondertara.common.base.Customizer;
import com.sondertara.common.text.xml.Xmls;

import javax.xml.transform.TransformerFactory;

/**
 *  */
public class SecureTransformerFactoryCustomizer implements Customizer<TransformerFactory> {

    @Override
    public void customize(TransformerFactory factory) {
        Xmls.setAttribute(factory, "http://javax.xml.XMLConstants/property/accessExternalDTD", false);
        Xmls.setAttribute(factory, "http://javax.xml.XMLConstants/property/accessExternalStylesheet", false);
        Xmls.setAttribute(factory, "http://javax.xml.XMLConstants/feature/secure-processing", true);
    }
}
