package com.sondertara.common.security.prevention.owasp.codecs;


import com.sondertara.common.text.xml.XPathHandler;
import com.sondertara.common.text.xml.XPaths;

public class OwaspXPathEncoder implements XPathHandler {
    @Override
    public String getName() {
        return "owasp-xpath-encoder";
    }

    private static final char[] IMMUNE_XPATH = {',', '.', '-', '_', ' '};

    private HTMLEntityCodec htmlCodec = new HTMLEntityCodec();

    /**
     *
     * @param xpathParameter 要在 xpath中拼接的参数
     *
     * @see XPaths
     */
    @Override
    public String transform(String xpathParameter) {
        if (xpathParameter == null) {
            return null;
        }
        return htmlCodec.encode(IMMUNE_XPATH, xpathParameter);
    }
}
