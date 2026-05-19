package com.ulpgc.businessunit;

import com.ulpgc.events.Event;
import com.ulpgc.flights.Flight;

public class Main {
    private final static Datamart datamart = new Datamart();

    public static void main(String[] args) throws InterruptedException {
        //load historic events -> possibly not necessary for our application
        HistoricalEventReader<Flight> flightHistory = new HistoricalEventReader<>(Flight.class, datamart::update);
        HistoricalEventReader<Event> eventHistory = new HistoricalEventReader<>(Event.class, datamart::update);
        flightHistory.load();
        eventHistory.load();

        //subscription to ActiveMQ
        BusinessUnitSubscriber<Flight> flightBusinessUnitSubscriber = new BusinessUnitSubscriber<>(Flight.class, datamart::update);
        BusinessUnitSubscriber<Event> eventBusinessUnitSubscriber = new BusinessUnitSubscriber<>(Event.class, datamart::update);

        Thread.currentThread().join(); //TODO substitute with CLI implementation
    }
}
