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

    public boolean exportar(List<Event> eventos, String rutaXml) {

        if (eventos == null || eventos.isEmpty()) {
            System.err.println("XmlExporter: la lista de eventos esta vacia.");
            return false;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element raiz = doc.createElement("events");
            doc.appendChild(raiz);

            for (Event evento : eventos) {
                Element nodoEvento = doc.createElement("event");

                nodoEvento.appendChild(crearElemento(doc, "id",            evento.getId()));
                nodoEvento.appendChild(crearElemento(doc, "countryCode",   evento.getCountryCode()));
                nodoEvento.appendChild(crearElemento(doc, "city",          evento.getCity()));
                nodoEvento.appendChild(crearElemento(doc, "startDateTime", evento.getStartDateTime()));

                raiz.appendChild(nodoEvento);
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

            System.out.println("XmlExporter: " + eventos.size()
                    + " eventos exportados a " + rutaXml);
            return true;

        } catch (Exception e) {
            System.err.println("XmlExporter: error al generar el XML -> " + e.getMessage());
            return false;
        }
    }

    private Element crearElemento(Document doc, String nombre, String valor) {
        Element elemento = doc.createElement(nombre);
        elemento.setTextContent(valor != null ? valor : "");
        return elemento;
    }
}
