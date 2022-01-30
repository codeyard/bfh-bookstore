package org.bookstore.shipping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bookstore.order.entity.Order;
import org.bookstore.order.entity.OrderStatus;
import org.bookstore.order.repository.OrderRepository;
import org.bookstore.shipping.dto.ShippingInfo;
import org.bookstore.shipping.dto.ShippingOrder;
import org.bookstore.shipping.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

@Component
public class ShippingClient {

    private static final Logger logger = LoggerFactory.getLogger(ShippingClient.class);

    private final OrderRepository orderRepository;
    private final JmsTemplate jmsTemplate;
    @Value("${bookstore.shipping.order-queue}")
    private String orderQueue;
    @Value("${bookstore.shipping.cancel-queue}")
    private String cancelQueue;
    @Value("${bookstore.shipping.info-queue}")
    private String infoQueue;

    @Value("${bookstore.shipping.mail.enabled}")
    private boolean mailEnabled;

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
                logger.info("Sending sending order to shipping service");
                return session.createTextMessage(content);
            } catch (JsonProcessingException e) {
                logger.error("Error occurred in sendShippingOrder: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });

    }

    public void sendCancellation(Long orderId) {
        jmsTemplate.send(cancelQueue, session -> {
            try {
                String content = new ObjectMapper().writeValueAsString(orderId);
                logger.info("Sending cancel order to shipping service");
                return session.createTextMessage(content);
            } catch (JsonProcessingException | JMSException e) {
                logger.error("Error occurred in sendCancellation: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    @JmsListener(destination = "${bookstore.shipping.info-queue}")
    public void receiveShippingInfo(Message message) {
        try {
            String content = ((TextMessage) message).getText();
            ShippingInfo shippingInfo = new ObjectMapper().readValue(content, ShippingInfo.class);
            logger.info("Receiving message from shipping service: " + content);
            orderRepository.findById(shippingInfo.getOrderId())
                .ifPresent(order -> {
                    order.setStatus(shippingInfo.getStatus());
                    orderRepository.saveAndFlush(order);
                    if(mailEnabled)
                        emailService.sendSimpleMessage(shippingInfo.getOrderId());
                });
        } catch (JMSException | JsonProcessingException e) {
            logger.error("Error occurred in receiveShippingInfo: " + e.getMessage());
            throw new RuntimeException();
        }
    }
}
