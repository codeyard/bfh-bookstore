package ch.rgis.bookorders.shipping;

import ch.rgis.bookorders.order.entity.Order;
import ch.rgis.bookorders.order.entity.OrderStatus;
import ch.rgis.bookorders.order.repository.OrderRepository;
import ch.rgis.bookorders.shipping.dto.ShippingInfo;
import ch.rgis.bookorders.shipping.dto.ShippingOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    @Value("${shipping.order-queue}")
    private String orderQueue;
    @Value("${shipping.cancel-queue}")
    private String cancelQueue;

    public ShippingClient(OrderRepository orderRepository, JmsTemplate jmsTemplate) {
        this.orderRepository = orderRepository;
        this.jmsTemplate = jmsTemplate;
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
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @JmsListener(destination = "${shipping.info-queue}")
    public void receiveShippingInfo(Message message) throws JMSException, JsonProcessingException {
        String content = ((TextMessage) message).getText();
        ShippingInfo shippingInfo = new ObjectMapper().readValue(content, ShippingInfo.class);

        orderRepository.findById(shippingInfo.orderId())
                .ifPresent(order -> {
                    order.setStatus(shippingInfo.status());
                    orderRepository.saveAndFlush(order);
                });
    }
}
