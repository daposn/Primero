package com.ulpgc.eventstorebuilder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class EventStore {
    public EventStore(){

    }

    public void save(String json, String topic){
        JsonObject object = JsonParser.parseString(json).getAsJsonObject();
        String ts = object.get("ts").getAsString();
        LocalDate date = Instant.parse(ts).atZone(ZoneOffset.UTC).toLocalDate();
        String formattedDate = DateTimeFormatter.BASIC_ISO_DATE.format(date);

        String ss = object.get("ss").getAsString();

        String directory = String.format("eventstore/%s/%s", topic, ss);

        try {
            Path dir = Path.of(directory);
            System.out.println(dir.toAbsolutePath());
            Files.createDirectories(dir);

            Path file = dir.resolve(formattedDate + ".events");
            Files.writeString(file, json + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
