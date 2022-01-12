package org.bookstore.order.repository;

import org.bookstore.order.entity.OrderStatus;
import org.bookstore.shipping.dto.ShippingInfo;
import org.bookstore.shipping.service.EmailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;



@SpringBootTest
public class ShippingClientIT {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${shipping.info-queue}")
    private String infoQueue;

    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private EmailService emailService;


    @Test
    public void receiveCancellation_successfully() {
        Long orderId = 100020L;

        jmsTemplate.send(infoQueue, session -> {
            ShippingInfo shippingInfo = new ShippingInfo();
            shippingInfo.setOrderId(orderId);
            shippingInfo.setStatus(OrderStatus.CANCELED);
            try {
                String content = new ObjectMapper().writeValueAsString(shippingInfo);
                return session.createTextMessage(content);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        jmsTemplate.setReceiveTimeout(1000);
        jmsTemplate.receive(infoQueue);


        orderRepository.findById(orderId).ifPresent(order -> Assertions.assertEquals(order.getStatus(), OrderStatus.CANCELED));

        verify(emailService, times(1)).sendSimpleMessage(orderId);

    }

    @Test
    void receiveProcessing_successfully() {
        Long orderId = 100020L;

        jmsTemplate.send(infoQueue, session -> {
            ShippingInfo shippingInfo = new ShippingInfo();
            shippingInfo.setOrderId(orderId);
            shippingInfo.setStatus(OrderStatus.PROCESSING);
            try {
                String content = new ObjectMapper().writeValueAsString(shippingInfo);
                return session.createTextMessage(content);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        jmsTemplate.setReceiveTimeout(1000);
        jmsTemplate.receive(infoQueue);

        orderRepository.findById(orderId).ifPresent(order -> Assertions.assertEquals(order.getStatus(), OrderStatus.PROCESSING));

        verify(emailService, times(1)).sendSimpleMessage(orderId);

    }

    @Test
    void receiveShipment_successfully() {
        Long orderId = 100020L;

        jmsTemplate.send(infoQueue, session -> {
            ShippingInfo shippingInfo = new ShippingInfo();
            shippingInfo.setOrderId(orderId);
            shippingInfo.setStatus(OrderStatus.SHIPPED);
            try {
                String content = new ObjectMapper().writeValueAsString(shippingInfo);
                return session.createTextMessage(content);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        jmsTemplate.setReceiveTimeout(1000);
        jmsTemplate.receive(infoQueue);

        orderRepository.findById(orderId).ifPresent(order -> Assertions.assertEquals(order.getStatus(), OrderStatus.SHIPPED));

        verify(emailService, times(1)).sendSimpleMessage(orderId);

    }

}
