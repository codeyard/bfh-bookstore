package ch.rgis.bookshipping.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

public class ShippingInfo {
    private Long orderId;
    private ShippingOrder.OrderStatus status;

    public ShippingInfo() {
    }

    public ShippingInfo(Long orderId, ShippingOrder.OrderStatus status) {
        this.orderId = orderId;
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public ShippingOrder.OrderStatus getStatus() {
        return status;
    }

    public void setStatus(ShippingOrder.OrderStatus status) {
        this.status = status;
    }

}
