package ch.rgis.bookorders.shipping;

import ch.rgis.bookorders.order.entity.OrderStatus;

public record ShippingInfo(Long orderId, OrderStatus status) {}
