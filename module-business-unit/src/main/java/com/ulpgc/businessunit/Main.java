package com.ulpgc.businessunit;

import com.ulpgc.events.Event;
import com.ulpgc.events.EventFeeder;
import com.ulpgc.flights.Flight;
import com.ulpgc.eventstorebuilder.EventStoreBuilder;

import java.util.List;
import java.util.Scanner;

public class Main {
    private final static Datamart datamart = new Datamart();

    public static void main(String[] args) throws InterruptedException {

        // 1. Persistencia: los store builders escuchan ActiveMQ y guardan los mensajes en disco
        EventStoreBuilder builderFlights = new EventStoreBuilder("Flight");
        EventStoreBuilder builderEvents = new EventStoreBuilder("Event");

        // 2. Carga eventos históricos del eventstore
        HistoricalEventReader<Flight> flightHistory =
                new HistoricalEventReader<>(Flight.class, datamart::update);
        HistoricalEventReader<Event> eventHistory =
                new HistoricalEventReader<>(Event.class, datamart::update);
        flightHistory.load();
        eventHistory.load();

        // 3. Suscripción a ActiveMQ para recibir datos en tiempo real
        BusinessUnitSubscriber<Flight> flightSub =
                new BusinessUnitSubscriber<>(Flight.class, datamart::update);
        BusinessUnitSubscriber<Event> eventSub =
                new BusinessUnitSubscriber<>(Event.class, datamart::update);

        // 4. Arranca el feeder de TicketMaster (captura horaria → publica en ActiveMQ)
        new EventFeeder().start();

        // 5. Arranca la CLI en el hilo principal
        startCLI();
    }

    // ── CLI ───────────────────────────────────────────────────────────────────
    private static void startCLI() {
        Scanner scanner = new Scanner(System.in);

        printWelcome();

        while (true) {
            printMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> searchFlights(scanner);
                case "2" -> searchEvents(scanner);
                case "3" -> datamart.printSummary();
                case "0" -> {
                    System.out.println("Bye!");
                    System.exit(0);
                }
                default  -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    // ── Opción 1: buscar vuelos ───────────────────────────────────────────────
    private static void searchFlights(Scanner scanner) {
        System.out.print("From (IATA code, e.g. CDG): ");
        String from = scanner.nextLine().trim().toUpperCase();

        System.out.print("To   (IATA code, e.g. AUS): ");
        String to = scanner.nextLine().trim().toUpperCase();

        System.out.print("Date (YYYY-MM-DD): ");
        String date = scanner.nextLine().trim();
        
        List<Flight> flights = new FlightSearchService(datamart).search(from, to, date);

        if (flights.isEmpty()) {
            System.out.println("\nNo flights found for "
                    + from + " → " + to + " on " + date + ".");
        } else {
            System.out.println("\nFlights found: " + flights.size());
            System.out.println("─────────────────────────────────────────");
            for (Flight f : flights) {
                System.out.printf(
                        "  %-20s  %s → %s  |  %.2f EUR  |  %.0f min  |  %d stop(s)%n",
                        String.join("/", f.code),
                        f.from,
                        f.to,
                        f.price,
                        f.duration,
                        f.stops
                );
            }
            System.out.println("─────────────────────────────────────────");
        }
    }

    // ── Opción 2: buscar eventos por ciudad ───────────────────────────────────
    private static void searchEvents(Scanner scanner) {
        System.out.print("City (e.g. Madrid, Roma, Paris): ");
        String city = scanner.nextLine().trim();

        List<Event> events = datamart.eventsInCity(city);

        if (events.isEmpty()) {
            System.out.println("\nNo events found in " + city + ".");
        } else {
            System.out.println("\nEvents in " + city + ": " + events.size());
            System.out.println("─────────────────────────────────────────");
            for (Event e : events) {
                System.out.printf(
                        "  [%s]  %s  |  %s%n",
                        e.getCountryCode(),
                        e.getCity(),
                        e.getStartDateTime()
                );
            }
            System.out.println("─────────────────────────────────────────");
        }
    }

    // ── Helpers de UI ─────────────────────────────────────────────────────────
    private static void printWelcome() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║       Travel Business Unit CLI       ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println("Datamart loading from historical events...");
        System.out.println("Subscribing to live feed via ActiveMQ...");
        System.out.println("Ready.\n");
    }

    private static void printMenu() {
        System.out.println("\nWhat would you like to do?");
        System.out.println("  1 → Search flights");
        System.out.println("  2 → Search events in a city");
        System.out.println("  3 → Datamart summary");
        System.out.println("  0 → Exit");
        System.out.print("Option: ");
    }
}