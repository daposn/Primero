package com.ulpgc.eventstorebuilder;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        EventStoreBuilder builderFlight = new EventStoreBuilder("Flight");
        EventStoreBuilder builderEvent = new EventStoreBuilder("Event");

        Thread.currentThread().join();
    }
}
