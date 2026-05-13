import java.util.List;

public class Flight {
    List<String> code;
    List<String> airlines;
    String from;
    List<String> layover_airports;
    String to;
    double price;
    String capturedAt;
    boolean has_layovers;
    int stops;
    double duration;

    Flight(List<String> code, List<String> airlines, String from, List<String> layover_airports,
           String to, double price, String capturedAt, boolean has_layovers, int stops, double duration) {
        this.code = code;
        this.airlines = airlines;
        this.from = from;
        this.layover_airports = layover_airports;
        this.to = to;
        this.price = price;
        this.capturedAt = capturedAt;
        this.has_layovers = has_layovers;
        this.stops = stops;
        this.duration = duration;
    }
}