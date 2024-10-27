package com.sondertara.common.text.xml;

import com.sondertara.common.base.Named;
import com.sondertara.common.function.Transformer;
/**
 *  */
public interface XPathHandler extends Transformer<String, String>, Named {
    @Override
    String transform(String xpathParameter);

}
