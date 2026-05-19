package com.ulpgc.businessunit;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class HistoricalEventReader <T>{
    Class<T> type;
    Consumer<T> handler;
    private final Gson gson = new Gson();

    HistoricalEventReader(Class<T> type, Consumer<T> handler){
        this.type = type;
        this.handler = handler;
    }

    public void load(){
        Path rootPath = Path.of(String.format("eventstore/%s", type.getSimpleName()));

        if (!Files.exists(rootPath)) {
            System.out.println("No historical events for " + type.getSimpleName()); // not a problem, just skips it
            return;
        }

        try (Stream<Path> paths = Files.walk(rootPath)) {
            paths.filter(p -> p.toString().endsWith(".events"))
                    .forEach(this::loadFile);
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadFile(Path file){
        try {
            for(String line : Files.readAllLines(file)){
                T payload = gson.fromJson(line, type);
                handler.accept(payload);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
