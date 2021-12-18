package ch.rgis.bookorders.bookshipping.service;

import ch.rgis.bookorders.bookshipping.dto.ShippingInfo;
import ch.rgis.bookorders.bookshipping.dto.ShippingOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

@Service
public class ShippingService {

    private final JmsTemplate jmsTemplate;

    @Value("${shipping.info-queue}")
    private String infoQueue;

    public ShippingService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    private ShippingInfo shippingInfo = new ShippingInfo();

    @JmsListener(destination = "${shipping.order-queue}")
    public void receiveShippingOrder(Message message) throws JMSException, JsonProcessingException {
        String request = ((TextMessage) message).getText();
        ShippingOrder shippingOrder = new ObjectMapper().readValue(request, ShippingOrder.class);
        shippingInfo.setOrderId(shippingOrder.getOrderId());
        processOrder();
    }


    @JmsListener(destination = "${shipping.cancel-queue}")
    public void receiveCancellation(TextMessage message) throws JMSException {
        System.out.println("RECEIVING CANCELLATION....");
        Long orderId = Long.valueOf(message.getText());
        shippingInfo.setOrderId(orderId);
        shippingInfo.setStatus(ShippingOrder.OrderStatus.CANCELED);
        message.getJMSCorrelationID();
        sendShippingInfo();
    }

    public void sendShippingInfo() {
        System.out.println("SENDING CANCELLATION TO CLIENT....");
        jmsTemplate.send(infoQueue, session -> {
            String content;
            try {
                content = new ObjectMapper().writeValueAsString(shippingInfo);
            } catch (JsonProcessingException e) {
                throw new RuntimeException();
            }
            return session.createTextMessage(content);
        });

    }

    public void processOrder() {
        shippingInfo.setStatus(ShippingOrder.OrderStatus.PROCESSING);
        sendShippingInfo();
        try {
            Thread.sleep(30000);
            shipOrder();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }

    public void shipOrder() {
        shippingInfo.setStatus(ShippingOrder.OrderStatus.SHIPPED);
        sendShippingInfo();
    }
}
