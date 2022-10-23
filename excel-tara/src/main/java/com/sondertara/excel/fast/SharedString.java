package com.sondertara.excel.fast;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.sondertara.excel.fast.DefaultXMLInputFactory.factory;

class SharedString {
    private static final SharedString EMPTY = new SharedString();
    private final SimpleXmlReader reader;
    private final List<String> values = new ArrayList<>();

    private SharedString() {
        reader = null;
    }

    SharedString(InputStream in) throws XMLStreamException {
        reader = new SimpleXmlReader(factory, in);
    }

    static SharedString fromInputStream(InputStream in) throws XMLStreamException {
        return in == null ? EMPTY : new SharedString(in);
    }

    String getItemAt(int index) throws XMLStreamException {
        if (reader == null) {
            return null;
        }
        readUpTo(index);
        return values.get(index);
    }

    private void readUpTo(int index) throws XMLStreamException {
        while (index >= values.size()) {
            reader.goTo("si");
            values.add(reader.getValueUntilEndElement("si", "rPh"));
        }
    }
}
