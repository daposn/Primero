package com.ulpgc.flights;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class FlightClient implements FlightFeeder {
    private final String apiKey;
    private final OkHttpClient client = new OkHttpClient();

    public FlightClient() {
        this.apiKey = loadApiKey();
    }

    private String loadApiKey() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
            return properties.getProperty("serp_api_key");
        } catch (IOException e) {
            System.err.println("Could not load config.properties.");
            return null;
        }
    }

    @Override
    public List<Flight> fetch(String from, String to, String date) {
        return new FlightNormalization().normalize(captureFlights(from, to, date), date);
    }

    private JsonArray captureFlights(String departure_id, String arrival_id, String date){
        if (apiKey == null) return null;

        String url = "https://serpapi.com/search.json"
                + "?engine=google_flights"
                + "&departure_id=" + departure_id
                + "&arrival_id=" + arrival_id
                + "&currency=EUR"
                + "&type=2"
                + "&outbound_date=" + date
                + "&api_key=" + apiKey;

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            JsonObject results = JsonParser.parseString(body).getAsJsonObject();
            JsonArray best = results.getAsJsonArray("best_flights");

            return best;
        } catch (IOException e) {
            System.err.println("Error calling SerpAPI: " + e.getMessage());
            return null;
        }
    }
}
