package org.bookstore.shipping.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bookstore.shipping.dto.ShippingInfo;
import org.bookstore.shipping.dto.ShippingOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ShippingService {

    private static final Logger logger = LoggerFactory.getLogger(ShippingService.class);

    private final JmsTemplate jmsTemplate;

    private static final List<ShippingInfo> shippingInfoList = new ArrayList<>();
    private final ShippingInfo shippingInfo = new ShippingInfo();
    @Value("${bookstore.shipping.info-queue}")
    private String infoQueue;
    @Value("${bookstore.shipping.processing-time}")
    private int delay;
    private ShippingOrder shippingOrder = new ShippingOrder();

    public ShippingService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @JmsListener(destination = "${bookstore.shipping.order-queue}")
    @Async
    public void receiveShippingOrder(Message message) throws JMSException, JsonProcessingException {
        String request = ((TextMessage) message).getText();
        shippingOrder = new ObjectMapper().readValue(request, ShippingOrder.class);
        shippingInfo.setOrderId(shippingOrder.getOrderId());
        shippingInfoList.add(shippingInfo);
        processOrder();
    }


    @JmsListener(destination = "${bookstore.shipping.cancel-queue}")
    public void receiveCancellation(TextMessage message) throws JMSException {
        Long orderId = Long.valueOf(message.getText());
        Optional<ShippingInfo> optionalShippingInfo = shippingInfoList.stream().filter(item -> Objects.equals(item.getOrderId(), orderId)).findFirst();
        logger.info("Receive cancel message for " + orderId);
        if (optionalShippingInfo.isPresent() && optionalShippingInfo.get().getStatus().equals(ShippingOrder.OrderStatus.PROCESSING)) {
            updateExistingOrder(ShippingOrder.OrderStatus.CANCELED);
            sendShippingInfo();
        }
    }

    public void sendShippingInfo() {
        Optional<ShippingInfo> currentOrder = shippingInfoList.stream().filter(item -> item.getOrderId().equals(shippingOrder.getOrderId())).findFirst();
        if (currentOrder.isPresent()) {
            logger.info("Send shipping message from shipping service " + currentOrder.get().getOrderId());
            jmsTemplate.convertAndSend(infoQueue, shippingInfo);
        }
    }

    public void processOrder() {
        logger.info("Processing...");
        updateExistingOrder(ShippingOrder.OrderStatus.PROCESSING);
        sendShippingInfo();
        try {
            Thread.sleep(delay);
            shipOrder();
        } catch (InterruptedException e) {
            logger.error("Error occurred in processOrder: " + e.getMessage());
            throw new RuntimeException();
        }
    }

    public void shipOrder() {
        logger.info("Shipping...");
        Optional<ShippingInfo> optionalShippingInfo = shippingInfoList.stream().filter(item -> Objects.equals(item.getOrderId(), shippingOrder.getOrderId())).findFirst();
        if (optionalShippingInfo.isPresent() && optionalShippingInfo.get().getStatus().equals(ShippingOrder.OrderStatus.PROCESSING)) {
            updateExistingOrder(ShippingOrder.OrderStatus.SHIPPED);
            sendShippingInfo();
        }
    }

    private void updateExistingOrder(ShippingOrder.OrderStatus orderStatus) {
        Optional<ShippingInfo> optionalShippingInfo = shippingInfoList.stream().filter(item -> Objects.equals(item.getOrderId(), shippingInfo.getOrderId())).findFirst();
        optionalShippingInfo.ifPresent(info -> info.setStatus(orderStatus));
    }


}
