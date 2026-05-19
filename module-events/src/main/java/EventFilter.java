import java.util.ArrayList;
import java.util.List;

public class EventFilter {

    // Países permitidos en el proyecto
    private static final List<String> PAISES_VALIDOS = List.of("ES", "IT", "FR");

    /**
     * Devuelve solo los eventos que tienen los 4 campos rellenos.
     * Usa el método isCompleto() del propio Event.
     */
    public List<Event> filtrarCompletos(List<Event> eventos) {
        List<Event> resultado = new ArrayList<>();

        for (Event e : eventos) {
            if (e.isCompleto()) {
                resultado.add(e);
            } else {
                System.out.println("EventFilter: descartado evento incompleto → " + e);
            }
        }

        System.out.println("EventFilter.filtrarCompletos: "
                + resultado.size() + "/" + eventos.size() + " eventos válidos.");
        return resultado;
    }

    /**
     * Devuelve solo los eventos del país indicado.
     * @param countryCode  "ES", "IT" o "FR"
     */
    public List<Event> filtrarPorPais(List<Event> eventos, String countryCode) {
        List<Event> resultado = new ArrayList<>();

        for (Event e : eventos) {
            if (countryCode.equalsIgnoreCase(e.getCountryCode())) {
                resultado.add(e);
            }
        }

        System.out.println("EventFilter.filtrarPorPais [" + countryCode + "]: "
                + resultado.size() + " eventos.");
        return resultado;
    }

    /**
     * Devuelve solo los eventos de la ciudad indicada (ignorando mayúsculas).
     * @param city  Nombre de la ciudad, p.ej. "Madrid", "Roma", "Paris"
     */
    public List<Event> filtrarPorCiudad(List<Event> eventos, String city) {
        List<Event> resultado = new ArrayList<>();

        for (Event e : eventos) {
            if (city.equalsIgnoreCase(e.getCity())) {
                resultado.add(e);
            }
        }

        System.out.println("EventFilter.filtrarPorCiudad [" + city + "]: "
                + resultado.size() + " eventos.");
        return resultado;
    }

    /**
     * Devuelve eventos cuya fecha de inicio sea posterior a la indicada.
     * El formato esperado es ISO-8601: "2025-07-01T00:00:00Z"
     * La comparación es alfabética, que funciona correctamente con este formato.
     * @param fechaMinima  Fecha mínima en formato ISO-8601
     */
    public List<Event> filtrarPorFecha(List<Event> eventos, String fechaMinima) {
        List<Event> resultado = new ArrayList<>();

        for (Event e : eventos) {
            if (e.getStartDateTime() != null
                    && e.getStartDateTime().compareTo(fechaMinima) >= 0) {
                resultado.add(e);
            }
        }

        System.out.println("EventFilter.filtrarPorFecha [>= " + fechaMinima + "]: "
                + resultado.size() + " eventos.");
        return resultado;
    }

    /**
     * Descarta eventos cuyo countryCode no sea ES, IT o FR.
     * Útil como segunda validación tras el parseo.
     */
    public List<Event> filtrarPaisesValidos(List<Event> eventos) {
        List<Event> resultado = new ArrayList<>();

        for (Event e : eventos) {
            if (PAISES_VALIDOS.contains(e.getCountryCode())) {
                resultado.add(e);
            } else {
                System.out.println("EventFilter: país no permitido → " + e.getCountryCode());
            }
        }

        System.out.println("EventFilter.filtrarPaisesValidos: "
                + resultado.size() + "/" + eventos.size() + " eventos.");
        return resultado;
    }

    /**
     * Aplica todos los filtros en cadena:
     *   1. Descarta incompletos
     *   2. Descarta países no permitidos
     *   3. (Opcional) filtra por fecha si se indica una
     *
     * Es el método que usará Main.java en el flujo principal.
     * @param fechaMinima  Puede ser null para no filtrar por fecha
     */
    public List<Event> filtrarTodo(List<Event> eventos, String fechaMinima) {
        List<Event> resultado = filtrarCompletos(eventos);
        resultado = filtrarPaisesValidos(resultado);

        if (fechaMinima != null && !fechaMinima.isEmpty()) {
            resultado = filtrarPorFecha(resultado, fechaMinima);
        }

        System.out.println("\nEventFilter.filtrarTodo → " + resultado.size() + " eventos finales.");
        return resultado;
    }
}