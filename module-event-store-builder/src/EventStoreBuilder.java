import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class EventStoreBuilder {
    private String topicName;
    private Session session;
    private Connection connection;
    TopicSubscriber subscriber;
    EventStore eventStore;

    EventStoreBuilder(String topic){
        this.topicName = topic;
        eventStore = new EventStore();
        connect();
        subscribe();
    }

    private void connect() {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            connection = factory.createConnection();
            connection.setClientID("event-store-builder-" + this.topicName);
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);


        } catch (Exception e) {
            System.err.println("Connection failed");
        }
    }

    private void subscribe(){
        try{
            Topic topic = session.createTopic(this.topicName);
            subscriber = session.createDurableSubscriber(topic, this.topicName + "-subscriber");

            subscriber.setMessageListener(message -> {
                TextMessage textmessage = (TextMessage) message;
                try {
                    String json = textmessage.getText();
                    eventStore.save(json, this.topicName);
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            });
        }catch(JMSException e){
            throw new RuntimeException(e);
        }
    }

    private void closeAll(){
        try {
            subscriber.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
