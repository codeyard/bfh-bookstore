package org.bookstore.catalog.adapter;

import java.math.BigDecimal;

public record Price(
    BigDecimal amount,
    String currencyCode
) {
}
