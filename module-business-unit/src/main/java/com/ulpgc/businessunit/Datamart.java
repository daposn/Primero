package com.ulpgc.businessunit;

import com.ulpgc.events.Event;
import com.ulpgc.flights.Flight;

import java.util.*;
import java.util.stream.Collectors;

public class Datamart {

    // Clave: "FROM-TO-DATE" → lista de vuelos para esa ruta y fecha
    private final Map<String, List<Flight>> flightMap = new HashMap<>();

    // Clave: ciudad (en minúsculas) → lista de eventos en esa ciudad
    private final Map<String, List<Event>> eventMap = new HashMap<>();

    Datamart() {}

    // ── Consultas ─────────────────────────────────────────────────────────────

    public List<Flight> fetchFlights(String from, String to, String date) {
        String key = flightKey(from, to, date);
        return flightMap.getOrDefault(key, Collections.emptyList());
    }

    public List<Event> eventsInCity(String city) {
        return eventMap.getOrDefault(city.toLowerCase(), Collections.emptyList());
    }

    // ── Updates (llamados por HistoricalEventReader y BusinessUnitSubscriber) ──

    public void update(Flight flight) {
        if (flight == null) return;
        String key = flightKey(flight.from, flight.to, flight.arrival_date);
        flightMap.computeIfAbsent(key, k -> new ArrayList<>()).add(flight);
    }

    public void update(Event event) {
        if (event == null) return;
        String city = event.getCity() != null ? event.getCity().toLowerCase() : "unknown";
        eventMap.computeIfAbsent(city, k -> new ArrayList<>()).add(event);
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    private String flightKey(String from, String to, String date) {
        return (from + "-" + to + "-" + date).toUpperCase();
    }

    public int totalFlights() {
        return flightMap.values().stream().mapToInt(List::size).sum();
    }

    public int totalEvents() {
        return eventMap.values().stream().mapToInt(List::size).sum();
    }

    public void printSummary() {
        System.out.println("Datamart summary:");
        System.out.println("  Flights: " + totalFlights()
                + " across " + flightMap.size() + " routes");
        System.out.println("  Events:  " + totalEvents()
                + " across " + eventMap.size() + " cities");
    }
}