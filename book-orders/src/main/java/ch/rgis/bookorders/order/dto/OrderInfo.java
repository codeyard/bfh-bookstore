package ch.rgis.bookorders.order.dto;

import ch.rgis.bookorders.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderInfo(Long id, LocalDateTime date, BigDecimal amount, OrderStatus status) {
}
