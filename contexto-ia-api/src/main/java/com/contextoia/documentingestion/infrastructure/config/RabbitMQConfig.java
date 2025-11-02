package com.contextoia.documentingestion.infrastructure.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up RabbitMQ components such as queues, exchanges, and bindings.
 * This configuration defines the necessary beans to enable communication with RabbitMQ
 * using specific queues and exchange settings for PDF processing tasks.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.pdf-processing}")
    private String pdfProcessingQueue;

    @Value("${rabbitmq.queue.pdf-processing-dlq}")
    private String pdfProcessingDLQ;

    @Value("${rabbitmq.exchange.pdf-processing}")
    private String exchange;

    @Bean
    public Queue pdfProcessingQueue() {
        return QueueBuilder.durable(pdfProcessingQueue)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", pdfProcessingDLQ)
                .build();
    }

    @Bean
    public Queue pdfProcessingDLQ() {
        return new Queue(pdfProcessingDLQ, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding binding(Queue pdfProcessingQueue, DirectExchange exchange) {
        return BindingBuilder.bind(pdfProcessingQueue)
                .to(exchange)
                .with(String.valueOf(pdfProcessingQueue));
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}

