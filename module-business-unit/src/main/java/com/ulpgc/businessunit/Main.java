package com.ulpgc.businessunit;

import com.ulpgc.events.Event;
import com.ulpgc.events.EventFeeder;
import com.ulpgc.flights.Flight;
import com.ulpgc.eventstorebuilder.EventStoreBuilder;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
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
                case "1" -> buildTrip(scanner);
                case "2" -> searchFlightsFromCode(scanner);
                case "3" -> searchEventsScanning(scanner);
                case "4" -> datamart.printSummary();
                case "0" -> {
                    System.out.println("Bye!");
                    System.exit(0);
                }
                default  -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void buildTrip(Scanner scanner){
        System.out.println("Which city are you going to? ");
        String eventCity = scanner.nextLine().trim();
        searchEvents(eventCity);

        System.out.println("Which event are you going to? (Name)");
        String eventName = scanner.nextLine().trim();

        System.out.println("When? (YYYY-MM-DD)");
        String eventDate = scanner.nextLine().trim();

        searchFlightsFromCity(scanner, eventCity, eventDate);
        System.out.println("Which flight are you taking? (Write the first code of the flight) ");
        String code = scanner.nextLine().trim().toUpperCase();

        finalOutput(code, eventName, eventCity, eventDate);
    }

    private static void finalOutput(String code, String eventName, String eventCity, String eventDate){
        Optional<Flight> flight = datamart.findFlightByFirstCode(code);

        if (flight.isEmpty()) {
            System.out.println("\nNo flight found with code " + code + ".");
            return;
        }

        System.out.printf("Congratulations! You're going to %s in %s the %s. Your flight will be:%n",
                eventName, eventCity, eventDate);
        displayFlight(flight.get());
    }

    private static void searchFlightsFromCity(Scanner scanner, String eventCity, String dateEvent){

        System.out.println("What city are you flying from? ");
        String fromCity = scanner.nextLine().trim();

        IataResolver resolver = new IataResolver();
        String fromCode = resolver.toCode(fromCity).getFirst();
        String toCode = resolver.toCode(eventCity).getFirst();

        searchFlights(scanner, fromCode, toCode, dateEvent);
    }

    private static void searchFlightsFromCode(Scanner scanner){
        System.out.print("From (IATA code, e.g. MAD): ");
        String from = scanner.nextLine().trim().toUpperCase();

        System.out.print("To   (IATA code, e.g. LPA): ");
        String to = scanner.nextLine().trim().toUpperCase();

        System.out.print("Date (YYYY-MM-DD): ");
        String date = scanner.nextLine().trim();

        searchFlights(scanner, from, to, date);
    }

    // ── Opción 1: buscar vuelos ───────────────────────────────────────────────
    private static void searchFlights(Scanner scanner, String from, String to, String date) {
        
        List<Flight> flights = new FlightSearchService(datamart).search(from, to, date);

        if (flights.isEmpty()) {
            System.out.println("\nNo flights found for "
                    + from + " → " + to + " on " + date + ".");
        } else {
            System.out.println("\nFlights found: " + flights.size());
            System.out.println("─────────────────────────────────────────");
            System.out.printf("  %-20s  %-9s  |  %-10s  |  %-8s  |  %s%n",
                    "CODE(s)", "ROUTE", "PRICE", "DURATION", "STOP(s)");
            for (Flight f : flights) {
                displayFlight(f);
            }
            System.out.println("─────────────────────────────────────────");
        }
    }

    private static void displayFlight(Flight f){
        System.out.printf(
                "  %-20s  %s → %s  |  %.2f EUR  |  %s  |  %d stop(s)%n",
                String.join("/", f.code),
                f.from,
                f.to,
                f.price,
                formatDuration(f.duration),
                f.stops
        );
    }

    private static void searchEventsScanning(Scanner scanner){
        System.out.print("City (e.g. Madrid, Roma, Paris): ");
        String city = scanner.nextLine().trim();

        searchEvents(city);
    }

    // ── Opción 2: buscar eventos por ciudad ───────────────────────────────────
    private static void searchEvents(String city) {
        List<Event> events = datamart.eventsInCity(city);

        if (events.isEmpty()) {
            System.out.println("\nNo events found in " + city + ".");
        } else {
            System.out.println("\nEvents in " + city + ": " + events.size());
            System.out.println("─────────────────────────────────────────");
            for (Event e : events) {
                if(e.getName() != null){
                    System.out.printf(
                            "  [%s]  %s  |  %s  | %s%n",
                            e.getCountryCode(),
                            e.getCity(),
                            e.getStartDateTime(),
                            e.getName()
                    );
                }
            }
            System.out.println("─────────────────────────────────────────");
        }
    }

    // ── Helpers de UI ─────────────────────────────────────────────────────────

    // Convierte la duración (en minutos) a un formato legible "Xh Ym".
    private static String formatDuration(double minutes) {
        Duration d = Duration.ofMinutes(Math.round(minutes));
        return String.format("%2dh %02dm", d.toHours(), d.toMinutesPart());
    }

    private static void printWelcome() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║       Travel Business Unit CLI       ║");
        System.out.println("╚══════════════════════════════════════╝");
    }

    private static void printMenu() {
        System.out.println("\nWhat would you like to do?");
        System.out.println("  1 → Build my trip");
        System.out.println("  2 → Search flights");
        System.out.println("  3 → Search events in a city");
        System.out.println("  4 → Datamart summary");
        System.out.println("  0 → Exit");
        System.out.print("Option: ");
    }
}