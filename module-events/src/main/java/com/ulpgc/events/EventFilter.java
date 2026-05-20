package com.ulpgc.events;

import java.util.ArrayList;
import java.util.List;

public class EventFilter {

    private static final List<String> PAISES_VALIDOS = List.of("ES", "IT", "FR");

    public List<Event> filtrarCompletos(List<Event> eventos) {
        List<Event> resultado = new ArrayList<>();

        for (Event e : eventos) {
            if (e.isCompleto()) {
                resultado.add(e);
            }
        }

        return resultado;
    }

    public List<Event> filtrarPorPais(List<Event> eventos, String countryCode) {
        List<Event> resultado = new ArrayList<>();

        for (Event e : eventos) {
            if (countryCode.equalsIgnoreCase(e.getCountryCode())) {
                resultado.add(e);
            }
        }

        return resultado;
    }

    public List<Event> filtrarPorCiudad(List<Event> eventos, String city) {
        List<Event> resultado = new ArrayList<>();

        for (Event e : eventos) {
            if (city.equalsIgnoreCase(e.getCity())) {
                resultado.add(e);
            }
        }

        return resultado;
    }

    public List<Event> filtrarPorFecha(List<Event> eventos, String fechaMinima) {
        List<Event> resultado = new ArrayList<>();

        for (Event e : eventos) {
            if (e.getStartDateTime() != null
                    && e.getStartDateTime().compareTo(fechaMinima) >= 0) {
                resultado.add(e);
            }
        }

        return resultado;
    }

    public List<Event> filtrarPaisesValidos(List<Event> eventos) {
        List<Event> resultado = new ArrayList<>();

        for (Event e : eventos) {
            if (PAISES_VALIDOS.contains(e.getCountryCode())) {
                resultado.add(e);
            }
        }

        return resultado;
    }

    public List<Event> filtrarTodo(List<Event> eventos, String fechaMinima) {
        List<Event> resultado = filtrarCompletos(eventos);
        resultado = filtrarPaisesValidos(resultado);

        if (fechaMinima != null && !fechaMinima.isEmpty()) {
            resultado = filtrarPorFecha(resultado, fechaMinima);
        }

        return resultado;
    }
}
