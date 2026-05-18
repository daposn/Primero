import java.util.List;

public class Flight {
    List<String> code;
    List<String> airlines;
    String from;
    List<String> layover_airports;
    String to;
    double price;
    boolean has_layovers;
    int stops;
    double duration;
    String arrival_date;
    String ts;
    String ss;

    Flight(List<String> code, List<String> airlines, String from, List<String> layover_airports,
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
}