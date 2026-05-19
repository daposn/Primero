package com.ulpgc.events;

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

        for (String countryCode : COUNTRIES) {
            System.out.println("Descargando eventos de: " + countryCode + "...");

            String json = client.getEvents(countryCode, EVENTS_PER_COUNTRY);

            if (json == null) {
                System.err.println("  -> Error al obtener datos de " + countryCode);
                continue;
            }

            List<Event> eventosPais = parser.parse(json);
            System.out.println("  -> " + eventosPais.size() + " eventos encontrados.");

            todosLosEventos.addAll(eventosPais);
        }

        List<Event> eventosFiltrados = filter.filtrarCompletos(todosLosEventos);

        EventPublisher publisher = new EventPublisher();
        publisher.publish(eventosFiltrados);

        exporter.exportar(eventosFiltrados, "events.xml");
    }
}
