package com.ulpgc.eventstorebuilder;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        EventStoreBuilder builder = new EventStoreBuilder("Flight");

        Thread.currentThread().join();
    }
}
