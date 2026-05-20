package com.ulpgc.flights;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        String tomorrow = LocalDate.now().plusDays(1).toString();

        FlightFeeder feeder = new FlightClient();
        FlightSerializer serializer = new FlightPublisher();
        FlightController controller = new FlightController(feeder, serializer);

        controller.run("LPA", "MXP", tomorrow);
    }
}
