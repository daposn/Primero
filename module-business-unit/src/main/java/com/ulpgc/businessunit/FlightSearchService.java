package com.ulpgc.businessunit;
import com.ulpgc.flights.Flight;
import com.ulpgc.flights.FlightClient;
import com.ulpgc.flights.FlightPublisher;

import java.util.List;

public class FlightSearchService {
    private final Datamart datamart;
    private final FlightClient client;

    public FlightSearchService(Datamart datamart) {
        this.datamart = datamart;
        this.client = new FlightClient();
    }

    public List<Flight> search(String from, String to, String date) {
        List<Flight> cached = datamart.fetchFlights(from, to, date);
        if (!cached.isEmpty()) return cached;

        List<Flight> fresh = client.fetch(from, to, date);
        new FlightPublisher().saveAll(fresh);   // publish so future runs find it cached
        return fresh;
    }
}
