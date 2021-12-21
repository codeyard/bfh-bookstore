package ch.rgis.bookshipping.service;

import ch.rgis.bookshipping.dto.ShippingInfo;
import ch.rgis.bookshipping.dto.ShippingOrder;
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
    private final EmailService emailService;

    @Value("${shipping.info-queue}")
    private String infoQueue;

    @Value("${shipping.delay}")
    private int delay;

    public ShippingService(JmsTemplate jmsTemplate, EmailService emailService) {
        this.jmsTemplate = jmsTemplate;
        this.emailService = emailService;
    }

    private final ShippingInfo shippingInfo = new ShippingInfo();
    private ShippingOrder shippingOrder = new ShippingOrder();

    @JmsListener(destination = "${shipping.order-queue}")
    public void receiveShippingOrder(Message message) throws JMSException, JsonProcessingException {
        String request = ((TextMessage) message).getText();
        shippingOrder = new ObjectMapper().readValue(request, ShippingOrder.class);
        shippingInfo.setOrderId(shippingOrder.getOrderId());
        processOrder();
    }


    @JmsListener(destination = "${shipping.cancel-queue}")
    public void receiveCancellation(TextMessage message) throws JMSException {
        Long orderId = Long.valueOf(message.getText());
        shippingInfo.setOrderId(orderId);
        shippingInfo.setStatus(ShippingOrder.OrderStatus.CANCELED);
        sendShippingInfo();
    }

    public void sendShippingInfo() {
        jmsTemplate.convertAndSend(infoQueue, shippingInfo);
    }

    public void processOrder() {
        shippingInfo.setStatus(ShippingOrder.OrderStatus.PROCESSING);
        sendShippingInfo();
        try {
            Thread.sleep(delay);
            shipOrder();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }

    public void shipOrder() {
        shippingInfo.setStatus(ShippingOrder.OrderStatus.SHIPPED);
        sendShippingInfo();
        emailService.sendSimpleMessage(shippingOrder);
    }
}
