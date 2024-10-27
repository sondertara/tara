package com.sondertara.common.text.xml;

import com.sondertara.common.collection.StreamUtils;
import org.jspecify.annotations.NonNull;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static javax.xml.XMLConstants.*;

public class Namespaces {

    /**
     * 绝大部分 xml中，都会有 xsi这个命名空间的，
     */
    public static final String NAMESPACE_XSI_NAME = "xsi";
    private Namespaces(){

    }
    public static String getNodeNamespaceURI(@NonNull Node node) {
        return node.getNamespaceURI();
    }

    public static boolean isNamespaceAttribute(Attr attribute) {
        return XMLNS_ATTRIBUTE.equals(attribute.getLocalName()) || (XMLNS_ATTRIBUTE + ":" + attribute.getLocalName()).equals(attribute.getName());
    }

    public static Map<String, Namespace> getDocumentNamespaces(Node node) {
        Element root = node.getOwnerDocument().getDocumentElement();
        return findNamespaces(root);
    }

    public static Map<String, Namespace> findNamespaces(@NonNull Node node) {
        Map<String, Namespace> ret = new HashMap<String, Namespace>();
        if (node.getParentNode() != null) {
            ret.putAll(findNamespaces(node.getParentNode()));
        }
        ret.putAll(StreamUtils.of(new NodeAttributesIterator(node)).filter(new Predicate<Attr>() {
            @Override
            public boolean test(Attr attribute) {
                return isNamespaceAttribute(attribute);
            }
        }).map(attr -> new Namespace(attr.getLocalName(), attr.getValue())).collect(Collectors.toMap(Namespace::getPrefix,Function.identity(),(k1, k2)->k1)));
        return ret;

    }

    public static boolean isW3cXmlNamespace(Node node) {
        return isCustomNamespace(node.getNamespaceURI());
    }

    public static boolean isDefaultNamespace(Namespace namespace) {
        return XMLNS_ATTRIBUTE.equals(namespace.getPrefix());
    }

    /**
     * 当只有标准的命名空间时，才返回 false
     */
    public static boolean hasCustomNamespace(Document document) {
        return hasCustomNamespace(document, false);
    }

    /**
     * 当只有标准的命名空间时，才返回 false.
     *
     * @param isDefaultIfJustHasXsi
     *   只有 1个命名空间，且名称 为xsi时，是否认定为默认命名空间
     *      */
    public static boolean hasCustomNamespace(Document document, boolean isDefaultIfJustHasXsi) {
        Map<String, Namespace> namespaceMap = getDocumentNamespaces(document.getDocumentElement());
        if (StreamUtils.of(namespaceMap.values()).anyMatch(new Predicate<Namespace>() {
            @Override
            public boolean test(Namespace namespace) {
                return isCustomNamespace(namespace);
            }
        })) {
            return !isDefaultIfJustHasXsi || namespaceMap.size() != 1 || !namespaceMap.containsKey(NAMESPACE_XSI_NAME);
        }

        return false;
    }

    public static boolean isCustomNamespace(Namespace namespace) {
        return !isW3cXmlNamespace(namespace);
    }

    public static boolean isCustomNamespace(String namespaceURI) {
        return !isW3cXmlNamespace(namespaceURI);
    }

    public static boolean isW3cXmlNamespace(Namespace namespace) {
        return isW3cSchemaInstanceNamespace(namespace) || isW3cSchemaNamespace(namespace);
    }

    public static boolean isW3cXmlNamespace(String namespaceURI) {
        return isW3cSchemaInstanceNamespace(namespaceURI) || isW3cSchemaNamespace(namespaceURI);
    }

    public static boolean isW3cSchemaNamespace(Namespace namespace) {
        return isW3cSchemaNamespace(namespace.getUri());
    }

    public static boolean isW3cSchemaNamespace(String namespaceURI) {
        return W3C_XML_SCHEMA_NS_URI.equals(namespaceURI);
    }

    public static boolean isW3cSchemaInstanceNamespace(Namespace namespace) {
        return isW3cSchemaInstanceNamespace(namespace.getUri());
    }

    public static boolean isW3cSchemaInstanceNamespace(String namespaceURI) {
        return W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(namespaceURI);
    }
}
