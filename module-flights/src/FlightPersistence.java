import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class FlightPersistence implements FlightSerializer {
    private static final String URL = "jdbc:sqlite:flights.db";
    FlightPersistence(){
        createIfNotExists();
    }

    private void createIfNotExists(){
        String query =
        "CREATE TABLE IF NOT EXISTS flights(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "code TEXT, " +
                "airlines TEXT, " +
                "departure TEXT, " +
                "layoverAirports TEXT, " +
                "arrival TEXT, " +
                "price REAL, " +
                "capturedAt TEXT, " +
                "hasLayovers BOOLEAN, " +
                "stops INTEGER, " +
                "duration REAL);";
        try(Connection c = DriverManager.getConnection(URL);
            PreparedStatement ps = c.prepareStatement(query);){

            ps.execute();
        }catch(SQLException e){
            System.err.println("Create table failed: " + e.getMessage());
        }
    }

    public void saveAll(List<Flight> flights){
        String query = "INSERT into flights (code, airlines, departure, layoverAirports, arrival, price, capturedAt, hasLayovers, stops, duration) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection c = DriverManager.getConnection(URL);
            PreparedStatement ps = c.prepareStatement(query);){

            for(Flight flight: flights){
                ps.setString(1, String.join(",", flight.code));
                ps.setString(2, String.join(",", flight.airlines));
                ps.setString(3, flight.from);
                ps.setString(4, String.join(",", flight.layover_airports));
                ps.setString(5, flight.to);
                ps.setDouble(6, flight.price);
                ps.setString(7, flight.capturedAt);
                ps.setBoolean(8, flight.has_layovers);
                ps.setInt(9, flight.stops);
                ps.setDouble(10, flight.duration);
                ps.addBatch();
            }

            ps.executeBatch();

        }catch(SQLException e){
            System.err.println("Create table failed: " + e.getMessage());
        }
    }
}