public class Event {

    private String id;
    private String countryCode;
    private String city;
    private String startDateTime;

    public Event(String id, String countryCode, String city, String startDateTime) {
        this.id            = id;
        this.countryCode   = countryCode;
        this.city          = city;
        this.startDateTime = startDateTime;
    }

    // ── Constructor vacío (necesario para JAXB) ───────────────
    public Event() {}

    // ── Getters ──────────────────────────────────────────────
    public String getId()            { return id; }
    public String getCountryCode()   { return countryCode; }
    public String getCity()          { return city; }
    public String getStartDateTime() { return startDateTime; }

    // ── Setters (necesarios para JAXB) ───────────────────────
    public void setId(String id)                       { this.id = id; }
    public void setCountryCode(String countryCode)     { this.countryCode = countryCode; }
    public void setCity(String city)                   { this.city = city; }
    public void setStartDateTime(String startDateTime) { this.startDateTime = startDateTime; }

    // ── Comprueba si el evento tiene todos los campos rellenos ─
    // EventFilter.java usará este método
    public boolean isCompleto() {
        return id            != null && !id.isEmpty()
                && countryCode   != null && !countryCode.isEmpty()
                && city          != null && !city.isEmpty()
                && startDateTime != null && !startDateTime.isEmpty();
    }

    // ── Representación legible para depurar en consola ────────
    @Override
    public String toString() {
        return "Event{" +
                "id='"             + id            + '\'' +
                ", countryCode='"  + countryCode   + '\'' +
                ", city='"         + city          + '\'' +
                ", startDateTime='" + startDateTime + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event other = (Event) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}