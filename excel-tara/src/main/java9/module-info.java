open module com.sondertara.excel {

    requires com.sondertara.common;
    requires com.google.common;
    requires java.desktop;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.slf4j;
    requires de.siegmar.fastcsv;
    requires commons.math3;
    requires SparseBitSet;
    requires java.servlet;
    requires com.fasterxml.aalto;
    requires org.apache.commons.compress;
    requires org.antlr.antlr4.runtime;
    exports com.sondertara.excel.boot;

}