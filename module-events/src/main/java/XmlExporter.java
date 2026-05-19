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

    /**
     * Exporta una lista de eventos a un archivo XML.
     * @param eventos   Lista de eventos a exportar
     * @param rutaXml   Ruta del archivo de salida, p.ej. "events.xml"
     * @return          true si se generó correctamente, false si hubo error
     */
    public boolean exportar(List<Event> eventos, String rutaXml) {

        if (eventos == null || eventos.isEmpty()) {
            System.err.println("XmlExporter: la lista de eventos está vacía.");
            return false;
        }

        try {
            // 1. Crea el documento XML vacío
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // 2. Elemento raíz <events>
            Element raiz = doc.createElement("events");
            doc.appendChild(raiz);

            // 3. Un <event> por cada objeto Event
            for (Event evento : eventos) {
                Element nodoEvento = doc.createElement("event");

                nodoEvento.appendChild(crearElemento(doc, "id",            evento.getId()));
                nodoEvento.appendChild(crearElemento(doc, "countryCode",   evento.getCountryCode()));
                nodoEvento.appendChild(crearElemento(doc, "city",          evento.getCity()));
                nodoEvento.appendChild(crearElemento(doc, "startDateTime", evento.getStartDateTime()));

                raiz.appendChild(nodoEvento);
            }

            // 4. Configura el formato de salida
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");             // Sangría
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4"); // 4 espacios

            // 5. Escribe el archivo
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(rutaXml));
            transformer.transform(source, result);

            System.out.println("XmlExporter: " + eventos.size()
                    + " eventos exportados a " + rutaXml);
            return true;

        } catch (Exception e) {
            System.err.println("XmlExporter: error al generar el XML → " + e.getMessage());
            return false;
        }
    }

    /**
     * Crea un elemento XML con texto dentro.
     * Ejemplo: <city>Madrid</city>
     */
    private Element crearElemento(Document doc, String nombre, String valor) {
        Element elemento = doc.createElement(nombre);
        elemento.setTextContent(valor != null ? valor : "");
        return elemento;
    }
}