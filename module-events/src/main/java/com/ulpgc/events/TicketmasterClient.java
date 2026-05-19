package com.ulpgc.events;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class TicketmasterClient {

    private static final String BASE_URL = "https://app.ticketmaster.com/discovery/v2/events.json";
    private final String apiKey;

    public TicketmasterClient() {
        this.apiKey = loadApiKey();
    }

    private String loadApiKey() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
            return properties.getProperty("ticketmaster_api_key");
        } catch (IOException e) {
            System.err.println("Could not load config.properties.");
            return null;
        }
    }

    public String getEvents(String countryCode, int size) {
        if (apiKey == null) return null;
        try {
            String urlStr = BASE_URL
                    + "?apikey="      + apiKey
                    + "&countryCode=" + countryCode
                    + "&size="        + size
                    + "&locale=*";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();

            } else {
                System.err.println("Error HTTP " + responseCode + " para countryCode=" + countryCode);
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error al conectar con la API: " + e.getMessage());
            return null;
        }
    }

    public String getEventsByCity(String countryCode, String city, int size) {
        if (apiKey == null) return null;
        try {
            String urlStr = BASE_URL
                    + "?apikey="      + apiKey
                    + "&countryCode=" + countryCode
                    + "&city="        + city.replace(" ", "%20")
                    + "&size="        + size
                    + "&locale=*";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();

            } else {
                System.err.println("Error HTTP " + responseCode
                        + " para countryCode=" + countryCode + ", city=" + city);
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error al conectar con la API: " + e.getMessage());
            return null;
        }
    }
}
