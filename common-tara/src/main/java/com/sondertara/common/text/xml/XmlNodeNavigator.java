package com.sondertara.common.text.xml;

import com.sondertara.common.collection.StreamUtils;
import com.sondertara.common.logging.Loggers;
import com.sondertara.common.navigation.Navigator;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  */
public class XmlNodeNavigator implements Navigator<Node> {
    private static final Logger logger = Loggers.getLogger(XmlNodeNavigator.class);
    @NonNull
    private XPathFactory xpathFactory;
    @Nullable
    private String namespacePrefix;

    public XmlNodeNavigator() {
        this(null, null);
    }

    public XmlNodeNavigator(@Nullable XPathFactory xpathFactory) {
        this(xpathFactory, null);
    }

    public XmlNodeNavigator(String namespacePrefix) {
        this(null, namespacePrefix);
    }

    public XmlNodeNavigator(@Nullable XPathFactory xpathFactory,
                            @Nullable String namespacePrefix) {
        this.xpathFactory = xpathFactory == null ? XPathFactory.newInstance() : xpathFactory;
        this.namespacePrefix = namespacePrefix;
    }

    @Override
    public Node get(Node context, String pathExpression) {
        final XPath xpath = xpathFactory.newXPath();
        xpath.setNamespaceContext(new NodeNamespaceContext(context, this.namespacePrefix));
        try {
            pathExpression = XPathInjectionPreventionHandler.getInstance().apply(pathExpression);
            final XPathExpression exp = xpath.compile(pathExpression);
            Node node = (Node) exp.evaluate(context, XPathConstants.NODE);
            return node;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }

    }

    @Override
    public List<Node> getList(Node context, String xpathExpression) {
        final XPath xpath = xpathFactory.newXPath();
        xpath.setNamespaceContext(new NodeNamespaceContext(context, this.namespacePrefix));
        try {
            xpathExpression = XPathInjectionPreventionHandler.getInstance().apply(xpathExpression);
            final XPathExpression exp = xpath.compile(xpathExpression);

            NodeList nodeList = (NodeList) exp.evaluate(context, XPathConstants.NODESET);
         return    StreamUtils.of(new NodeListIterator(nodeList)).collect(Collectors.toList());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return new ArrayList<>();
        }
    }

    @Override
    public <T> void set(Node context, String xpath, T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> Class<E> getType(Node context, String pathExpression) {
        Node node = get(context, pathExpression);
        if (node != null) {
            int nodeType = node.getNodeType();
            Class nodeClass = Node.class;
            switch (nodeType) {
                case Node.ELEMENT_NODE:
                    nodeClass = Element.class;
                    break;
                case Node.ATTRIBUTE_NODE:
                    nodeClass = Attr.class;
                    break;
                case Node.TEXT_NODE:
                    nodeClass = Text.class;
                    break;
                case Node.CDATA_SECTION_NODE:
                    nodeClass = CDATASection.class;
                    break;
                case Node.ENTITY_NODE:
                    nodeClass = Entity.class;
                    break;
                case Node.ENTITY_REFERENCE_NODE:
                    nodeClass = EntityReference.class;
                    break;
                case Node.COMMENT_NODE:
                    nodeClass = Comment.class;
                    break;
                case Node.DOCUMENT_NODE:
                    nodeClass = Document.class;
                    break;
            }
            return nodeClass;
        }
        return null;
    }

    @Override
    public String getParentPath(String pathExpression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLeaf(String pathExpression) {
        throw new UnsupportedOperationException();
    }
}
