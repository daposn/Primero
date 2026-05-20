package com.ulpgc.businessunit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class IataResolver {

    private static final String RESOURCE = "/iata-codes.csv";

    private final Map<String, List<String>> codesByCity;

    IataResolver() {
        this.codesByCity = load();
    }

    public List<String> toCode(String city) {
        if (city == null) return Collections.emptyList();
        return codesByCity.getOrDefault(normalize(city), Collections.emptyList());
    }

    private Map<String, List<String>> load() {
        Map<String, List<String>> result = new HashMap<>();
        try (InputStream in = IataResolver.class.getResourceAsStream(RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Missing classpath resource " + RESOURCE);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(line, result);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + RESOURCE, e);
        }
        return result;
    }

    private void parseLine(String line, Map<String, List<String>> result) {
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) return;

        String[] parts = trimmed.split(";", 2);
        if (parts.length < 2) return;

        String city = normalize(parts[0]);
        List<String> codes = Arrays.stream(parts[1].split(","))
                .map(String::trim)
                .filter(code -> !code.isEmpty())
                .toList();

        if (!city.isEmpty() && !codes.isEmpty()) {
            result.put(city, codes);
        }
    }

    // normalization to match the table keys
    private String normalize(String value) {
        String withoutAccents = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return withoutAccents.toLowerCase(Locale.ROOT).trim();
    }
}
