package com.sondertara.common.text.xml;

import com.sondertara.common.collection.StreamUtils;
import com.sondertara.common.registry.GenericRegistry;
import com.sondertara.common.security.prevention.injection.InjectionPreventionHandler;
import com.sondertara.common.spi.CommonServiceProvider;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 *  */
public class XPathInjectionPreventionHandler extends InjectionPreventionHandler {
    private static final GenericRegistry<XPathHandler> registry;

    static {
        final GenericRegistry<XPathHandler> _registry = new GenericRegistry<XPathHandler>();
        Iterator<XPathHandler> iterator = new CommonServiceProvider<XPathHandler>().get(XPathHandler.class);

        StreamUtils.of(iterator)
                .forEach(new Consumer<XPathHandler>() {
                    @Override
                    public void accept(XPathHandler xPathHandler) {
                        _registry.register(xPathHandler);
                    }
                });
        registry = _registry;
    }

    private static final XPathInjectionPreventionHandler INSTANCE = new XPathInjectionPreventionHandler();

    public static XPathInjectionPreventionHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public String apply(String xpathParameter) {
        XPathHandler OWASP_XPATH_ENCODER = registry.get("owasp-xpath-encoder");
        if (OWASP_XPATH_ENCODER != null) {
            return OWASP_XPATH_ENCODER.transform(xpathParameter);
        }
        return super.apply(xpathParameter);
    }
}
