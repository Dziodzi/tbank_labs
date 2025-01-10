package io.github.dziodzi;

import com.rabbitmq.client.*;

public class RabbitMQProducer {
    private final static String QUEUE_NAME = "testQueue";
    
    public void send(String message) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
