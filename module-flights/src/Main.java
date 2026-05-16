import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        String tomorrow = LocalDate.now().plusDays(1).toString();

        FlightFeeder feeder = new FlightClient();
        FlightSerializer serializer = new FlightPersistence();
        Controller controller = new Controller(feeder, serializer);

        controller.run("CDG", "AUS", tomorrow);
    }
}
