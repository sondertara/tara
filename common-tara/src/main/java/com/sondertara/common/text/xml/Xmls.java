package com.sondertara.common.text.xml;

import com.sondertara.common.exception.ExceptionUtils;
import com.sondertara.common.io.IOUtils;
import com.sondertara.common.io.file.Files;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.text.xml.cutomizer.SecureDocumentBuilderFactoryCustomizer;
import com.sondertara.common.text.xml.cutomizer.SecureTransformerFactoryCustomizer;
import com.sondertara.common.text.xml.errorhandler.RaiseErrorHandler;
import com.sondertara.common.text.xml.resolver.DTDEntityResolver;
import com.sondertara.common.text.xml.resolver.NullEntityResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Xmls {
    public static final String NULL_XML_STR = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    private Xmls() {
    }

    public static Document getXmlDoc(InputStream xml) throws Exception {
        return getXmlDoc(null, xml);
    }

    public static Document getXmlDoc(EntityResolver entityResolver, final InputStream xml) throws Exception {
        return getXmlDoc(entityResolver, null, xml);
    }

    public static Document getXmlDoc(EntityResolver entityResolver, ErrorHandler errorHandler, final InputStream xml) throws Exception {
        return getXmlDoc(entityResolver, errorHandler, xml, true);
    }

    public static Document getXmlDoc(EntityResolver entityResolver, ErrorHandler errorHandler, final InputStream xml, boolean namespaceAware) throws Exception {
        return getXmlDoc(entityResolver, errorHandler, xml, false, false, namespaceAware);
    }


    public static Document getXmlDoc(
            EntityResolver entityResolver,
            ErrorHandler errorHandler,
            final InputStream xml,
            boolean ignoreComments,
            boolean ignoringElementContentWhitespace,
            boolean namespaceAware
    ) throws Exception {
        return Xmls.getXmlDoc(entityResolver, errorHandler, xml, ignoreComments, ignoringElementContentWhitespace, namespaceAware, null);
    }

    /**
     *      */
    public static Document getXmlDoc(
            EntityResolver entityResolver,
            ErrorHandler errorHandler,
            final InputStream xml,
            boolean ignoreComments,
            boolean ignoringElementContentWhitespace,
            boolean namespaceAware,
            SecureDocumentBuilderFactoryCustomizer customizer
    ) throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(ignoreComments);
        factory.setIgnoringElementContentWhitespace(ignoringElementContentWhitespace);
        factory.setNamespaceAware(namespaceAware);
        if (entityResolver != null) {
            factory.setValidating(true);
        }

        if (customizer == null) {
            customizer = new SecureDocumentBuilderFactoryCustomizer();
        }
        customizer.customize(factory);

        final DocumentBuilder builder = factory.newDocumentBuilder();
        entityResolver = ((entityResolver == null) ? new NullEntityResolver() : entityResolver);
        builder.setEntityResolver(entityResolver);
        builder.setErrorHandler(errorHandler == null ? new RaiseErrorHandler() : errorHandler);
        return builder.parse(xml);
    }

    public static Document getXmlDoc(final InputStream dtdInputStream, final String src, final boolean srcIsPath) throws Exception {
        if (srcIsPath) {
            return getXmlDoc(dtdInputStream, src);
        }
        return getXmlDoc(new DTDEntityResolver(dtdInputStream), new ByteArrayInputStream(src.getBytes(StandardCharsets.UTF_8)));
    }

    public static Document getXmlDoc(final InputStream dtdInputStream, final String xmlfilepathOrURI) throws Exception {
        final File file = new File(xmlfilepathOrURI);
        if (!file.exists()) {
            throw new FileNotFoundException(StringUtils.format("File '{}' does not exist .", xmlfilepathOrURI));
        }
        FileInputStream fis = null;
        try {
            fis = Files.openInputStream(file);
            return getXmlDoc(new DTDEntityResolver(dtdInputStream), fis);
        } finally {
            IOUtils.close(fis);
        }
    }

    public static void writeDocToOutputStream(final Document doc, final OutputStream out) throws Exception {
        final Transformer transformer = newTransformer();
        transformer.transform(new DOMSource(doc), new StreamResult(out));
    }


    public static void writeDocToFile(final Document doc, final File file) throws TransformerFactoryConfigurationError, TransformerException {
        Transformer transformer = newTransformer();
        transformer.transform(new DOMSource(doc), new StreamResult(file));
    }

    /**
     *      */
    public static Transformer newTransformer(SecureTransformerFactoryCustomizer customizer) throws TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        if (customizer == null) {
            customizer = new SecureTransformerFactoryCustomizer();
        }
        customizer.customize(factory);
        final Transformer trans = factory.newTransformer();
        return trans;
    }

    public static Transformer newTransformer() throws TransformerConfigurationException {
        return newTransformer(null);
    }

    public static <T> T handleXml(final String xmlpath, final XmlDocumentHandler<T> handler) {
        InputStream input = null;
        Document doc;
        try {
            final File file = new File(xmlpath);
            if (!file.exists()) {
                throw new FileNotFoundException("File '" + xmlpath + "' does not exist .");
            }
            input = new FileInputStream(file);
            doc = getXmlDoc(null, input);
            return handler.handle(doc);
        } catch (Exception ex) {
            throw ExceptionUtils.wrapAsRuntimeException(ex);
        } finally {
            IOUtils.close(input);
        }
    }

    /**
     *      */
    public static NodeList findNodeList(Document doc, String xpath) throws XPathExpressionException {
        NodeList nodes = new XmlAccessor(Namespaces.hasCustomNamespace(doc) ? "x" : null).getNodeList(doc, XPathFactory.newInstance(), xpath);
        return nodes;
    }

    /**
     *      */
    public static Element findElement(Document doc, String xpath) throws XPathExpressionException {
        Element element = new XmlAccessor(Namespaces.hasCustomNamespace(doc) ? "x" : null).getElement(doc, XPathFactory.newInstance(), xpath);
        return element;
    }

    /**
     *      */
    public static void setFeature(DocumentBuilderFactory factory, String feature, boolean enabled) {
        try {
            factory.setFeature(feature, enabled);
        } catch (ParserConfigurationException e) {
            // ignore it
        }
    }

    /**
     *      */
    public static void setAttribute(DocumentBuilderFactory factory, String attribute, Object value) {
        try {
            factory.setAttribute(attribute, value);
        } catch (IllegalArgumentException e) {
            // ignore it
        }
    }


    /**
     *      */
    public static void setFeature(SAXParserFactory factory, String feature, boolean enabled) {
        try {
            factory.setFeature(feature, enabled);
        } catch (Throwable e) {
            // ignore it
        }
    }

    /**
     *      */
    public static void setProperty(XMLInputFactory factory, String feature, Object value) {
        try {
            factory.setProperty(feature, value);
        } catch (IllegalArgumentException e) {
            // ignore it
        }
    }

    /**
     *      */
    public static void setAttribute(TransformerFactory factory, String feature, Object value) {
        try {
            factory.setAttribute(feature, value);
        } catch (IllegalArgumentException e) {
            // ignore it
        }
    }

    /**
     *      */
    public static void setFeature(TransformerFactory factory, String feature, boolean enabled) {
        try {
            factory.setFeature(feature, enabled);
        } catch (TransformerConfigurationException e) {
            // ignore it
        }
    }

    /**
     *      */
    public static void setProperty(SchemaFactory factory, String feature, Object value) {
        try {
            factory.setProperty(feature, value);
        } catch (Throwable e) {
            // ignore it
        }
    }

    /**
     *      */
    public static void setFeature(SchemaFactory factory, String feature, boolean enabled) {
        try {
            factory.setFeature(feature, enabled);
        } catch (Throwable e) {
            // ignore it
        }
    }

    /**
     *      */
    public static void setFeature(XPathFactory xpathFactory, String feature, boolean enabled) {
        try {
            xpathFactory.setFeature(feature, enabled);
        } catch (XPathFactoryConfigurationException e) {
            // ignore it
        }
    }

}
