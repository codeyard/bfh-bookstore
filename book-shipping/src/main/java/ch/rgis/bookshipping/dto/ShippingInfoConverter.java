package ch.rgis.bookshipping.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

@Component
public class ShippingInfoConverter implements MessageConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public javax.jms.Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        try {
            String content = objectMapper.writeValueAsString(object);
            return session.createTextMessage(content);
        } catch (JsonProcessingException ex) {
            throw new MessageConversionException(ex.getMessage());
        }
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        try {
            String content = ((TextMessage) message).getText();
            return objectMapper.readValue(content, ShippingInfo.class);
        } catch (JsonProcessingException ex) {
            throw new MessageConversionException(ex.getMessage());
        }
    }
}