import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
public class FlightNormalization {

    public FlightNormalization() {}

    public List<Flight> normalize(JsonArray raw_best_flights, String date) {
        List<Flight> clean_flights = new ArrayList<>();

        if (raw_best_flights == null) {
            System.err.println("captureFlights() returned null — check that config.properties is in the working directory and contains serp_api_key");
            return clean_flights;
        }

        String ts = Instant.now().toString(); // timestamp captured just once for all the flights in one API call
        for (var element : raw_best_flights) {
            JsonObject trip = element.getAsJsonObject();

            JsonArray flights = trip.getAsJsonArray("flights");
            JsonArray layovers = trip.has("layovers") ? trip.getAsJsonArray("layovers") : new JsonArray();

            JsonObject first_flight = flights.get(0).getAsJsonObject();
            JsonObject last_flight  = flights.get(flights.size() - 1).getAsJsonObject();

            String dep_airport = first_flight.getAsJsonObject("departure_airport").get("id").getAsString();
            String arr_airport = last_flight.getAsJsonObject("arrival_airport").get("id").getAsString();
            double duration    = trip.get("total_duration").getAsDouble();
            double price       = trip.has("price") ? trip.get("price").getAsDouble() : 0.0;

            boolean has_layovers = !layovers.isEmpty();
            int stops            = layovers.size();

            List<String> code             = new ArrayList<>();
            List<String> airlines         = new ArrayList<>();
            List<String> layover_airports = new ArrayList<>();

            for (JsonElement flightEl : flights) {
                JsonObject curr_flight = flightEl.getAsJsonObject();
                if (curr_flight.has("flight_number"))
                    code.add(curr_flight.get("flight_number").getAsString());
                if (curr_flight.has("airline"))
                    airlines.add(curr_flight.get("airline").getAsString());
                layover_airports.add(curr_flight.getAsJsonObject("departure_airport").get("id").getAsString());
                layover_airports.add(curr_flight.getAsJsonObject("arrival_airport").get("id").getAsString());
            }

            clean_flights.add(new Flight(code, airlines, dep_airport, layover_airports,
                    arr_airport, price, has_layovers, stops, duration, date, ts));
        }
        return clean_flights;
    }
}
