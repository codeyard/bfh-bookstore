package ch.rgis.bookorders.bookshipping.service;

import ch.rgis.bookorders.bookshipping.dto.ShippingOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

@Service
public class ShippingService {

    @JmsListener(destination = "${shipping.order-queue}")
    public void receiveShippingOrder(Message message) throws JMSException, JsonProcessingException {
        System.out.println("receiving shipping order");
        String content = ((TextMessage) message).getText();
        ShippingOrder shippingOrder = new ObjectMapper().readValue(content, ShippingOrder.class);
        System.out.println("Shipping Order: " + shippingOrder);
    }

    public void receiveCancellation() {

    }

    public void sendShippingInfo() {

    }

    public void processOrder() {

    }

    public void shipOrder() {

    }
}
