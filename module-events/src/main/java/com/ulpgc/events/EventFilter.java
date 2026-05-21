package com.ulpgc.events;

import java.util.ArrayList;
import java.util.List;

public class EventFilter {

    private static final List<String> VALID_COUNTRIES = List.of("ES", "IT", "FR");

    public List<Event> filterFullEvents(List<Event> event) {
        List<Event> result = new ArrayList<>();

        for (Event e : event) {
            if (e.isCompleto()) {
                result.add(e);
            }
        }

        return result;
    }

    public List<Event> filterByCountry(List<Event> events, String countryCode) {
        List<Event> result = new ArrayList<>();

        for (Event e : events) {
            if (countryCode.equalsIgnoreCase(e.getCountryCode())) {
                result.add(e);
            }
        }

        return result;
    }

    public List<Event> filtrarPorCiudad(List<Event> events, String city) {
        List<Event> result = new ArrayList<>();

        for (Event e : events) {
            if (city.equalsIgnoreCase(e.getCity())) {
                result.add(e);
            }
        }

        return result;
    }

    public List<Event> filterByDate(List<Event> events, String startDate) {
        List<Event> result = new ArrayList<>();

        for (Event e : events) {
            if (e.getStartDateTime() != null
                    && e.getStartDateTime().compareTo(startDate) >= 0) {
                result.add(e);
            }
        }

        return result;
    }

    public List<Event> filterValidCountries(List<Event> events) {
        List<Event> results = new ArrayList<>();

        for (Event e : events) {
            if (VALID_COUNTRIES.contains(e.getCountryCode())) {
                results.add(e);
            }
        }

        return results;
    }

    public List<Event> filterAllVars(List<Event> events, String startDate) {
        List<Event> result = filterFullEvents(events);
        result = filterValidCountries(result);

        if (startDate != null && !startDate.isEmpty()) {
            result = filterByDate(result, startDate);
        }

        return result;
    }
}
