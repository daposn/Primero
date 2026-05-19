package com.ulpgc.flights;

import java.util.List;

public class Flight {
    public List<String> code;
    public List<String> airlines;
    public String from;
    public List<String> layover_airports;
    public String to;
    public double price;
    public boolean has_layovers;
    public int stops;
    public double duration;
    public String arrival_date;
    public String ts;
    public String ss;

    public Flight(List<String> code, List<String> airlines, String from, List<String> layover_airports,
           String to, double price, boolean has_layovers, int stops, double duration, String arrival_date, String ts) {
        this.code = code;
        this.airlines = airlines;
        this.from = from;
        this.layover_airports = layover_airports;
        this.to = to;
        this.price = price;
        this.has_layovers = has_layovers;
        this.stops = stops;
        this.duration = duration;
        this.arrival_date = arrival_date;
        this.ts = ts;
        this.ss = "flight-feeder";
    }

    public Flight() {}
}
