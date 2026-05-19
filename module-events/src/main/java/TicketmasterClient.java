import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TicketmasterClient {

    private static final String BASE_URL = "https://app.ticketmaster.com/discovery/v2/events.json";
    private static final String API_KEY   = "ECBdzxM1AL9LLvY1uobfcP64DK6W7vNe";

    /**
     * Descarga eventos de un país concreto.
     * @param countryCode  "ES", "IT" o "FR"
     * @param size         Número de resultados (máx. 200)
     * @return             JSON en crudo como String, o null si falla
     */
    public String getEvents(String countryCode, int size) {
        try {
            String urlStr = BASE_URL
                    + "?apikey="      + API_KEY
                    + "&countryCode=" + countryCode
                    + "&size="        + size
                    + "&locale=*";    // Acepta cualquier idioma

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000); // 5 segundos para conectar
            conn.setReadTimeout(10000);   // 10 segundos para leer

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

    /**
     * Descarga eventos filtrando también por ciudad.
     * @param countryCode  "ES", "IT" o "FR"
     * @param city         Nombre de la ciudad, p.ej. "Madrid"
     * @param size         Número de resultados
     */
    public String getEventsByCity(String countryCode, String city, int size) {
        try {
            String urlStr = BASE_URL
                    + "?apikey="      + API_KEY
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