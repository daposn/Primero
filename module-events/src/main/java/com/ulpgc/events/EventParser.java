package com.ulpgc.events;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventParser {

    public List<Event> parse(String json) {

        List<Event> eventos = new ArrayList<>();

        if (json == null || json.isEmpty()) {
            System.err.println("EventParser: JSON vacio o nulo.");
            return eventos;
        }

        try {
            JSONObject root = new JSONObject(json);

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
                    System.err.println("EventParser: Error en evento #" + i + " -> " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("EventParser: Error al parsear el JSON -> " + e.getMessage());
        }

        return eventos;
    }

    private Event parsearEvento(JSONObject jsonEvento) {

        String id = jsonEvento.optString("id", null);

        String startDateTime = null;
        if (jsonEvento.has("dates")) {
            JSONObject dates = jsonEvento.getJSONObject("dates");
            if (dates.has("start")) {
                JSONObject start = dates.getJSONObject("start");
                startDateTime = start.optString("dateTime", null);
            }
        }

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

        return new Event(id, countryCode, city, startDateTime);
    }
}
