open module com.sondertara.common {
    requires org.apache.commons.lang3;
    requires transitive org.slf4j;
    requires java.desktop;
    requires java.management;
    requires transitive lombok;
    requires com.google.common;
    requires org.apache.commons.codec;
    requires org.bouncycastle.provider;
    requires org.apache.commons.io;
    requires com.alibaba.fastjson2;
    requires java.compiler;
    requires java.servlet;
    exports com.sondertara.common.util;
    exports com.sondertara.common.lang.id;
    exports com.sondertara.common.io;
    exports com.sondertara.common.time;
    exports com.sondertara.common.crypto;
    exports com.sondertara.common.bean;
    exports com.sondertara.common.exception;
    exports com.sondertara.common.model;
    exports com.sondertara.common.regex;
    exports com.sondertara.common.convert;
    exports com.sondertara.common.lang.reflect;
    exports com.sondertara.common.lang;
    exports com.sondertara.common.function;
}