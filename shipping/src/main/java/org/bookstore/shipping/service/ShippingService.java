package org.bookstore.shipping.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bookstore.shipping.dto.ShippingInfo;
import org.bookstore.shipping.dto.ShippingOrder;
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

    private final JmsTemplate jmsTemplate;

    private static final List<ShippingInfo> shippingInfoList = new ArrayList<>();
    private final ShippingInfo shippingInfo = new ShippingInfo();
    @Value("${shipping.info-queue}")
    private String infoQueue;
    @Value("${shipping.delay}")
    private int delay;
    private ShippingOrder shippingOrder = new ShippingOrder();

    public ShippingService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @JmsListener(destination = "${shipping.order-queue}")
    @Async
    public void receiveShippingOrder(Message message) throws JMSException, JsonProcessingException {
        String request = ((TextMessage) message).getText();
        shippingOrder = new ObjectMapper().readValue(request, ShippingOrder.class);
        shippingInfo.setOrderId(shippingOrder.getOrderId());
        shippingInfoList.add(shippingInfo);
        processOrder();
    }


    @JmsListener(destination = "${shipping.cancel-queue}")
    public void receiveCancellation(TextMessage message) throws JMSException {
        Long orderId = Long.valueOf(message.getText());
        Optional<ShippingInfo> optionalShippingInfo = shippingInfoList.stream().filter(item -> Objects.equals(item.getOrderId(), orderId)).findFirst();
        System.out.println("CANCELLING...");
        if (optionalShippingInfo.isPresent() && optionalShippingInfo.get().getStatus().equals(ShippingOrder.OrderStatus.PROCESSING)) {
            updateExistingOrder(ShippingOrder.OrderStatus.CANCELED);
            sendShippingInfo();
        }
    }

    public void sendShippingInfo() {
        Optional<ShippingInfo> currentOrder = shippingInfoList.stream().filter(item -> item.getOrderId().equals(shippingOrder.getOrderId())).findFirst();
        if (currentOrder.isPresent()) {
            jmsTemplate.convertAndSend(infoQueue, shippingInfo);
        }
    }

    public void processOrder() {
        System.out.println("PROCESSING....");
        updateExistingOrder(ShippingOrder.OrderStatus.PROCESSING);
        sendShippingInfo();
        try {
            Thread.sleep(delay);
            shipOrder();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }

    public void shipOrder() {
        System.out.println("SHIPPPING....");
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
