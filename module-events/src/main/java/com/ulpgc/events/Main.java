package com.ulpgc.events;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final String[] COUNTRIES        = {"ES", "IT", "FR"};
    private static final int      EVENTS_PER_COUNTRY = 50;

    public static void main(String[] args) {
        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(
                Main::ejecutarCaptura,
                0,
                1,
                TimeUnit.HOURS
        );
    }

    private static void ejecutarCaptura() {
        System.out.println("Event feeder: captura iniciada " + java.time.Instant.now());

        TicketmasterClient client    = new TicketmasterClient();
        EventParser        parser    = new EventParser();
        EventFilter        filter    = new EventFilter();
        EventPublisher     publisher = new EventPublisher();

        List<Event> todosLosEventos = new ArrayList<>();

        for (String countryCode : COUNTRIES) {
            String json = client.getEvents(countryCode, EVENTS_PER_COUNTRY);
            if (json == null) continue;
            todosLosEventos.addAll(parser.parse(json));
        }

        List<Event> filtrados = filter.filterFullEvents(todosLosEventos);
        publisher.saveAll(filtrados);
        publisher.closeAll();

        System.out.println("Event feeder: " + filtrados.size() + " eventos publicados.");
    }
}