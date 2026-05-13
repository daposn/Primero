import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class Main {
    private static int passed = 0, failed = 0;

    public static void main(String[] args) {
        System.out.println("Working directory : " + System.getProperty("user.dir"));
        System.out.println("DB path           : " + new java.io.File("flights.db").getAbsolutePath());

        String tomorrow = LocalDate.now().plusDays(1).toString();

        FlightClient client = new FlightClient();
        FlightNormalization normalization = new FlightNormalization();
        FlightPersistence persistence = new FlightPersistence();

        List<Flight> flights = normalization.normalize(client.captureFlights("CDG", "AUS", tomorrow));

        // -- Data tests --
        check(flights != null && !flights.isEmpty(),
                "normalize() returns a non-empty list");

        check(flights.stream().allMatch(f -> f.from != null && !f.from.isBlank()),
                "all flights have a departure airport");

        check(flights.stream().allMatch(f -> f.to != null && !f.to.isBlank()),
                "all flights have an arrival airport");

        check(flights.stream().allMatch(f -> f.price > 0),
                "all flights have a positive price");

        check(flights.stream().allMatch(f -> f.has_layovers == (f.stops > 0)),
                "has_layovers is consistent with stops");

        check(flights.stream().allMatch(f -> f.capturedAt != null && !f.capturedAt.isBlank()),
                "all flights have capturedAt");

        check(flights.stream().allMatch(f -> !f.code.isEmpty()),
                "all flights have at least one flight code");

        // -- Persistence tests --
        int before = countRows();
        persistence.saveAll(flights);
        int afterFirst = countRows();
        check(afterFirst == before + flights.size(),
                "saveAll persists the correct number of rows (" + flights.size() + ")");

        persistence.saveAll(flights);
        int afterSecond = countRows();
        check(afterSecond == afterFirst + flights.size(),
                "second saveAll appends rows (append-only verified)");

        System.out.printf("%n%d/%d tests passed%n", passed, passed + failed);

        // -- Preview --
        System.out.println("\n--- First 10 flights ---");
        flights.stream().limit(10).forEach(f ->
                System.out.printf("%-6s -> %-6s | $%-7.2f | stops: %d | airlines: %s | codes: %s%n",
                        f.from, f.to, f.price, f.stops,
                        String.join("+", f.airlines),
                        String.join("+", f.code)));
    }

    private static void check(boolean condition, String label) {
        if (condition) { System.out.println("PASS  " + label); passed++; }
        else           { System.out.println("FAIL  " + label); failed++; }
    }

    private static int countRows() {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:flights.db");
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM flights");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("DB count failed: " + e.getMessage());
            return -1;
        }
    }
}
