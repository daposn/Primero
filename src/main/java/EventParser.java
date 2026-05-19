import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventParser {

    /**
     * Parsea el JSON devuelto por la API y devuelve una lista de Event.
     * @param json  String con el JSON en crudo
     * @return      Lista de eventos, vacía si no hay datos o hay error
     */
    public List<Event> parse(String json) {

        List<Event> eventos = new ArrayList<>();

        if (json == null || json.isEmpty()) {
            System.err.println("EventParser: JSON vacío o nulo.");
            return eventos;
        }

        try {
            JSONObject root = new JSONObject(json);

            // Comprueba que existen eventos en la respuesta
            if (!root.has("_embedded")) {
                System.out.println("EventParser: La respuesta no contiene eventos.");
                return eventos;
            }

            JSONArray jsonEventos = root
                    .getJSONObject("_embedded")
                    .getJSONArray("events");

            for (int i = 0; i < jsonEventos.length(); i++) {
                try {
                    Event evento = parsearEvento(jsonEventos.getJSONObject(i));
                    if (evento != null) {
                        eventos.add(evento);
                    }
                } catch (Exception e) {
                    // Si un evento falla, lo saltamos y seguimos con los demás
                    System.err.println("EventParser: Error en evento #" + i + " → " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("EventParser: Error al parsear el JSON → " + e.getMessage());
        }

        return eventos;
    }

    /**
     * Convierte un JSONObject individual en un objeto Event.
     */
    private Event parsearEvento(JSONObject jsonEvento) {

        // --- id ---
        String id = jsonEvento.optString("id", null);

        // --- startDateTime ---
        // Está en: dates → start → dateTime
        String startDateTime = null;
        if (jsonEvento.has("dates")) {
            JSONObject dates = jsonEvento.getJSONObject("dates");
            if (dates.has("start")) {
                JSONObject start = dates.getJSONObject("start");
                startDateTime = start.optString("dateTime", null);
            }
        }

        // --- city y countryCode ---
        // Están en: _embedded → venues[0] → city.name / country.countryCode
        String city        = null;
        String countryCode = null;

        if (jsonEvento.has("_embedded")) {
            JSONObject embedded = jsonEvento.getJSONObject("_embedded");

            if (embedded.has("venues") && embedded.getJSONArray("venues").length() > 0) {
                JSONObject venue = embedded.getJSONArray("venues").getJSONObject(0);

                if (venue.has("city")) {
                    city = venue.getJSONObject("city").optString("name", null);
                }
                if (venue.has("country")) {
                    countryCode = venue.getJSONObject("country").optString("countryCode", null);
                }
            }
        }

        // Crea y devuelve el Event con los datos extraídos
        return new Event(id, countryCode, city, startDateTime);
    }
}