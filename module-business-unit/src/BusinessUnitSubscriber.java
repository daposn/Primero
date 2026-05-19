import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.util.function.Consumer;

public class BusinessUnitSubscriber <T> {
    Class<T> type;
    Consumer<T> handler;

    Connection connection;
    Session session;
    TopicSubscriber subscriber;

    BusinessUnitSubscriber(Class<T> type, Consumer<T> handler){
        this.type = type;
        this.handler = handler;
        connect();
        subscribe();
    }

    private void connect() {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            this.connection = factory.createConnection();
            connection.setClientID("module-business-unit-" + type.getSimpleName());
            connection.start();
            this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        } catch (JMSException e) {
            System.err.println("Connection to ActiveMQ Failed");
        }
    }

    private void subscribe(){
        try{
            Topic topic = session.createTopic(type.getSimpleName());
            subscriber = session.createDurableSubscriber(topic, type.getSimpleName() + "-subscriber");

            subscriber.setMessageListener(message -> {
                TextMessage textmessage = (TextMessage) message;
                try {
                    String json = textmessage.getText();
                    T record = new Gson().fromJson(json, type);
                    handler.accept(record);
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            });
        }catch(JMSException e){
            throw new RuntimeException(e);
        }
    }
}
