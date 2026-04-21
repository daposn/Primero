import java.util.Properties;

public class FlightClient{
    private final String apiKey;

    public FlightClient(){
        this.apiKey = loadApiKey();
    }

    private String loadApiKey(){
        Properties properties = new Properties();
        try(FileInputStream fis = new FileInputStream("config.properties")){
            properties.load(fis);
            return properties.getProperty("serp_api_key");
        } catch (IOException e) {
            System.err.println("Could not load config.properties.");
            return null;
        }
    }

    public void captureFlights(){
        if(apiKey == null) return; // don't run without the API key

        Map<String, String> parameter = new HashMap<>();

        parameter.put("engine", "google_flights");
        parameter.put("departure_id", "CDG");
        parameter.put("arrival_id", "AUS");
        parameter.put("currency", "USD");
        parameter.put("type", "2");
        parameter.put("outbound_date", "2026-04-22");
        parameter.put("api_key", apiKey);

        GoogleSearch search = new GoogleSearch(parameter);

        try {
            JsonObject results = search.getJson();
            var best_flights = results.get("best_flights");
        } catch (SerpApiSearchException ex) {
            System.out.println("Exception:");
            System.out.println(ex.toString());
        }
    }

}