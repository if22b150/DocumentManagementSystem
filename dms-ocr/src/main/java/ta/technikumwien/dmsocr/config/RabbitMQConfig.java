package ta.technikumwien.dmsocr.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

//    public static final String QUEUE_NAME = "documentQueue";
    public static final String OCR_QUEUE = "OCR_QUEUE";
    public static final String RESULT_QUEUE = "RESULT_QUEUE";

    @Bean
    public Queue queue() {
        return new Queue(OCR_QUEUE, false);
    }

    @Bean
    public Queue resultQueue() {
        return new Queue(RESULT_QUEUE, true); // durable
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }
}
