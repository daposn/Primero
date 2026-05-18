import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.List;

public class FlightPublisher implements FlightSerializer{
    private Connection connection;
    private Session session;
    private MessageProducer producer;

    FlightPublisher(){
        connect();
    }

    private void connect(){
        try{
            ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("Flights");
            producer = session.createProducer(topic);
        }catch(Exception e){
            System.err.println("Couldn't connect to ActiveMQ");
        }

    }

    @Override
    public void saveAll(List<Flight> flights) {
        try{
            for (Flight flight: flights){
                String text = new Gson().toJson(flight);
                TextMessage msg = session.createTextMessage(text);
                producer.send(msg);
            }

            producer.close();
            session.close();
            connection.close();

        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
