package com.ulpgc.events;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

public class XmlExporter {

    public boolean exportar(List<Event> events, String rutaXml) {

        if (events == null || events.isEmpty()) {
            System.err.println("XmlExporter: la lista de eventos esta vacia.");
            return false;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element raiz = doc.createElement("events");
            doc.appendChild(raiz);

            for (Event event : events) {
                Element eventNode = doc.createElement("event");

                eventNode.appendChild(createElement(doc, "id",            event.getId()));
                eventNode.appendChild(createElement(doc, "countryCode",   event.getCountryCode()));
                eventNode.appendChild(createElement(doc, "city",          event.getCity()));
                eventNode.appendChild(createElement(doc, "startDateTime", event.getStartDateTime()));

                raiz.appendChild(eventNode);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(rutaXml));
            transformer.transform(source, result);

            System.out.println("XmlExporter: " + events.size()
                    + " eventos exportados a " + rutaXml);
            return true;

        } catch (Exception e) {
            System.err.println("XmlExporter: error al generar el XML -> " + e.getMessage());
            return false;
        }
    }

    private Element createElement(Document doc, String nombre, String valor) {
        Element element = doc.createElement(nombre);
        element.setTextContent(valor != null ? valor : "");
        return element;
    }
}
