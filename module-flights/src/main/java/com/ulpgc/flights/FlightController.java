package com.ulpgc.flights;

import java.util.List;

public class FlightController {
    private final FlightFeeder feeder;
    private final FlightSerializer serializer;

    public FlightController(FlightFeeder feeder, FlightSerializer serializer) {
        this.feeder = feeder;
        this.serializer = serializer;
    }

    public void run(String from, String to, String date) {
        List<Flight> flights = feeder.fetch(from, to, date);
        serializer.saveAll(flights);
    }
}
