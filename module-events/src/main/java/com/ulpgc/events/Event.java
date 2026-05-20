package com.ulpgc.events;

import java.time.Instant;

public class Event {

    private String id;
    private String countryCode;
    private String city;
    private String startDateTime;
    private String name;
    private String ts;
    private String ss;

    public Event(String id, String countryCode, String city, String startDateTime, String name) {
        this.id            = id;
        this.countryCode   = countryCode;
        this.city          = city;
        this.startDateTime = startDateTime;
        this.name = name;
        this.ts            = Instant.now().toString();
        this.ss            = "ticketmaster-feeder";
    }

    public Event() {}

    public String getId()            { return id; }
    public String getCountryCode()   { return countryCode; }
    public String getCity()          { return city; }
    public String getStartDateTime() { return startDateTime; }
    public String getName()          {return name;}
    public String getTs()            { return ts; }
    public String getSs()            { return ss; }

    public void setId(String id)                       { this.id = id; }
    public void setCountryCode(String countryCode)     { this.countryCode = countryCode; }
    public void setCity(String city)                   { this.city = city; }
    public void setStartDateTime(String startDateTime) { this.startDateTime = startDateTime; }
    public void setName(String name)                   {this.name = name;}
    public void setTs(String ts)                       { this.ts = ts; }
    public void setSs(String ss)                       { this.ss = ss; }

    public boolean isCompleto() {
        return id            != null && !id.isEmpty()
                && countryCode   != null && !countryCode.isEmpty()
                && city          != null && !city.isEmpty()
                && startDateTime != null && !startDateTime.isEmpty();
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='"             + id            + '\'' +
                ", countryCode='"  + countryCode   + '\'' +
                ", city='"         + city          + '\'' +
                ", startDateTime='" + startDateTime + '\'' +
                ", name=" + name + '\'' +
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
