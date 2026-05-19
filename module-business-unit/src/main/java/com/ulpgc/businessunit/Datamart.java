package com.ulpgc.businessunit;

import com.ulpgc.events.Event;
import com.ulpgc.flights.Flight;

import java.util.List;
import java.util.function.Consumer;

public class Datamart {
    //TODO decide the implementation (for example an HashMap)
    Datamart(){}

    public List<Flight> fetchFlights(String from, String to, String date){
        return null; //TODO look for flights already present in the datamart
    }
    public List<Event> eventsInCity(String city) {
        return null; // TODO query to look for cities in the feeder. event feeder already saves through the broker a lot of events
    }

    public void update(Flight flight){
        //TODO
    }
    public void update(Event event){
        //TODO 
    }
}
