package ch.heig.amt.vineward.configuration;

import jakarta.jms.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

/**
 * Configuration for JMS messaging as used in moderation services.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Configuration(proxyBeanMethods = false)
public class JmsConfig {

    public static final String REVIEW_QUEUE = "review-moderation-queue";

    @Bean
    public MessageConverter messageConverter() {
        var converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    public JmsTemplate jmsTemplate(
        ConnectionFactory connectionFactory,
        MessageConverter messageConverter
    ) {
        var jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setMessageConverter(messageConverter);
        jmsTemplate.setDefaultDestinationName(REVIEW_QUEUE);
        return jmsTemplate;
    }

}
