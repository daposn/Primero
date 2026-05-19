package com.ulpgc.flights;

import java.util.List;

public interface FlightFeeder {
    List<Flight> fetch(String from, String to, String date);
}
