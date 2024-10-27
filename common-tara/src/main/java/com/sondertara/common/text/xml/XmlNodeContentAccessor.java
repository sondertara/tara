package com.sondertara.common.text.xml;

import com.sondertara.common.accessor.BasedStringAccessor;
import com.sondertara.common.exception.ExceptionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

public class XmlNodeContentAccessor extends BasedStringAccessor<XPathExpression, Document> {
    @Override
    public Object get(XPathExpression exp) {
        Node node = null;
        Throwable exception = null;
        try {
            node = (Node) exp.evaluate(getTarget(), XPathConstants.NODE);
        } catch (XPathExpressionException ex) {
            exception = ex;
        }
        if (node != null) {
            return node.getTextContent();
        }
        if (exception != null) {
            throw ExceptionUtils.wrapAsRuntimeException(exception);
        }
        return null;
    }

    @Override
    public boolean has(XPathExpression key) {
        return get(key) != null;
    }

    @Override
    public String getString(XPathExpression exp, String defaultValue) {
        Object content = get(exp);
        if (content == null) {
            return defaultValue;
        }
        return content.toString();
    }

    @Override
    public void set(XPathExpression exp, Object value) {
        String content = value == null ? "" : value.toString();
        Node node = null;
        Throwable exception = null;
        try {
            node = (Node) exp.evaluate(getTarget(), XPathConstants.NODE);
        } catch (XPathExpressionException ex) {
            exception = ex;
        }
        if (node != null) {
            node.setTextContent(content);
        }
        if (exception != null) {
            throw ExceptionUtils.wrapAsRuntimeException(exception);
        }
    }

    @Override
    public void remove(XPathExpression exp) {
        Node node;
        Throwable exception = null;
        try {
            node = (Node) exp.evaluate(getTarget(), XPathConstants.NODE);
            if (node != null && node.getParentNode() != null) {
                node.getParentNode().removeChild(node);
            }
        } catch (XPathExpressionException ex) {
            exception = ex;
        }
        if (exception != null) {
            throw ExceptionUtils.wrapAsRuntimeException(exception);
        }
    }
}
