package ch.rgis.bookorders.shipping.dto;

import ch.rgis.bookorders.order.entity.OrderStatus;

public class ShippingInfo {
    private Long orderId;
    private OrderStatus status;

    public ShippingInfo() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
