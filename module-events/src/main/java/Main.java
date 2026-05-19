import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String[] COUNTRIES = {"ES", "IT", "FR"};
    private static final int EVENTS_PER_COUNTRY = 50;

    public static void main(String[] args) {

        TicketmasterClient client   = new TicketmasterClient();
        EventParser        parser   = new EventParser();
        EventFilter        filter   = new EventFilter();
        XmlExporter        exporter = new XmlExporter();

        List<Event> todosLosEventos = new ArrayList<>();

        // 1. Descarga y parsea eventos de cada país
        for (String countryCode : COUNTRIES) {
            System.out.println("Descargando eventos de: " + countryCode + "...");

            String json = client.getEvents(countryCode, EVENTS_PER_COUNTRY);

            if (json == null) {
                System.err.println("  → Error al obtener datos de " + countryCode);
                continue;
            }

            List<Event> eventosPais = parser.parse(json);
            System.out.println("  → " + eventosPais.size() + " eventos encontrados.");

            todosLosEventos.addAll(eventosPais);
        }

        // 2. Filtra eventos incompletos
        List<Event> eventosFiltrados = filter.filtrarCompletos(todosLosEventos);

        // 3. Exporta a XML
        exporter.exportar(eventosFiltrados, "events.xml");
    }
}