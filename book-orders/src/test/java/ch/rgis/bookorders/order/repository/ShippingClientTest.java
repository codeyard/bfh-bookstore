package ch.rgis.bookorders.order.repository;

import ch.rgis.bookorders.order.entity.OrderStatus;
import ch.rgis.bookorders.shipping.dto.ShippingInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import javax.jms.Message;
import javax.jms.TextMessage;

@SpringBootTest
public class ShippingClientTest {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${shipping.info-queue}")
    private String infoQueue;

    @Autowired
    private OrderRepository orderRepository;



    @Test
    void receiveCancellation_cancelled() {
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

        jmsTemplate.receive(infoQueue);

        orderRepository.findById(orderId).ifPresent(order -> {
            Assertions.assertEquals(order.getStatus(), OrderStatus.CANCELED);
        });


    }

}
