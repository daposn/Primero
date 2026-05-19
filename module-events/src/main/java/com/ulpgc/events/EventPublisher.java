package com.ulpgc.events;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.List;

public class EventPublisher {
    private Connection connection;
    private Session session;
    private MessageProducer producer;

    public EventPublisher() {
        connect();
    }

    private void connect() {
        try {
            ConnectionFactory factory =
                    new ActiveMQConnectionFactory("tcp://localhost:61616");
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("Event"); // topic: "Event"
            producer = session.createProducer(topic);
        } catch (Exception e) {
            System.err.println("EventPublisher: couldn't connect to ActiveMQ");
        }
    }

    public void saveAll(List<Event> events) {
        try {
            for (Event event : events) {
                String text = new Gson().toJson(event);
                TextMessage msg = session.createTextMessage(text);
                producer.send(msg);
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeAll() {
        try {
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}