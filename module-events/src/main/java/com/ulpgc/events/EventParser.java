package com.ulpgc.events;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventParser {

    public List<Event> parse(String json) {

        List<Event> events = new ArrayList<>();

        if (json == null || json.isEmpty()) {
            System.err.println("EventParser: JSON empty or null.");
            return events;
        }

        try {
            JSONObject root = new JSONObject(json);

            if (!root.has("_embedded")) {
                System.out.println("EventParser: Result contains no events.");
                return events;
            }

            JSONArray jsonEventos = root
                    .getJSONObject("_embedded")
                    .getJSONArray("events");

            for (int i = 0; i < jsonEventos.length(); i++) {
                try {
                    Event evento = parsearEvent(jsonEventos.getJSONObject(i));
                    if (evento != null) {
                        events.add(evento);
                    }
                } catch (Exception e) {
                    System.err.println("EventParser: Error in event #" + i + " -> " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("EventParser: Error at parse JSON -> " + e.getMessage());
        }

        return events;
    }

    private Event parsearEvent(JSONObject jsonEvent) {

        String id = jsonEvent.optString("id", null);

        String startDateTime = null;
        if (jsonEvent.has("dates")) {
            JSONObject dates = jsonEvent.getJSONObject("dates");
            if (dates.has("start")) {
                JSONObject start = dates.getJSONObject("start");
                startDateTime = start.optString("dateTime", null);
            }
        }

        String city        = null;
        String countryCode = null;

        if (jsonEvent.has("_embedded")) {
            JSONObject embedded = jsonEvent.getJSONObject("_embedded");

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
