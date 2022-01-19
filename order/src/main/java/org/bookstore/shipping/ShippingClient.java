package org.bookstore.shipping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bookstore.order.entity.Order;
import org.bookstore.order.entity.OrderStatus;
import org.bookstore.order.repository.OrderRepository;
import org.bookstore.shipping.dto.ShippingInfo;
import org.bookstore.shipping.dto.ShippingOrder;
import org.bookstore.shipping.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

@Component
public class ShippingClient {

    private final OrderRepository orderRepository;
    private final JmsTemplate jmsTemplate;
    @Value("${bookstore.shipping.order-queue}")
    private String orderQueue;
    @Value("${bookstore.shipping.cancel-queue}")
    private String cancelQueue;
    @Value("${bookstore.shipping.info-queue}")
    private String infoQueue;

    private final EmailService emailService;

    public ShippingClient(OrderRepository orderRepository, JmsTemplate jmsTemplate, EmailService emailService) {
        this.orderRepository = orderRepository;
        this.jmsTemplate = jmsTemplate;
        this.emailService = emailService;
    }

    public void sendShippingOrder(Order order) {
        order.setStatus(OrderStatus.PROCESSING);
        orderRepository.saveAndFlush(order);

        jmsTemplate.send(orderQueue, session -> {
            try {
                ShippingOrder shippingOrder = new ShippingOrder(order);
                String content = new ObjectMapper().writeValueAsString(shippingOrder);
                return session.createTextMessage(content);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void sendCancellation(Long orderId) {
        jmsTemplate.send(cancelQueue, session -> {
            try {
                String content = new ObjectMapper().writeValueAsString(orderId);
                return session.createTextMessage(content);
            } catch (JsonProcessingException | JMSException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @JmsListener(destination = "${bookstore.shipping.info-queue}")
    public void receiveShippingInfo(Message message) {
        try {
            String content = ((TextMessage) message).getText();
            ShippingInfo shippingInfo = new ObjectMapper().readValue(content, ShippingInfo.class);

            orderRepository.findById(shippingInfo.getOrderId())
                .ifPresent(order -> {
                    order.setStatus(shippingInfo.getStatus());
                    orderRepository.saveAndFlush(order);
                    emailService.sendSimpleMessage(shippingInfo.getOrderId());
                });
        } catch (JMSException | JsonProcessingException e) {
            throw new RuntimeException();
        }
    }
}
