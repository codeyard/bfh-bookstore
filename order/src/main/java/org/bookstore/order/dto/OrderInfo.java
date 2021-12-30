package org.bookstore.order.dto;

import org.bookstore.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderInfo(Long id, LocalDateTime date, BigDecimal amount, OrderStatus status) {
}
