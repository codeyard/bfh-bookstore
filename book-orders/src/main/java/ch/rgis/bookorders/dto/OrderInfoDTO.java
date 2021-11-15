package ch.rgis.bookorders.dto;

import ch.rgis.bookorders.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderInfoDTO(Long id, LocalDateTime date, BigDecimal amount, OrderStatus status){}
