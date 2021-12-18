package ch.rgis.bookorders.bookshipping.dto;

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
