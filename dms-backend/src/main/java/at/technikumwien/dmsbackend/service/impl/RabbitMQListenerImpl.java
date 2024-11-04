package at.technikumwien.dmsbackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQListenerImpl {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQListenerImpl.class);

    public void receiveMessage(String message) {
        logger.info("Received message from RabbitMQ: {}", message);
    }
}
