public class Main {
    public static void main(String[] args) throws InterruptedException {
        EventStoreBuilder builder = new EventStoreBuilder("Flights");

        Thread.currentThread().join(); // keep the process alive to receive messages
    }
}
