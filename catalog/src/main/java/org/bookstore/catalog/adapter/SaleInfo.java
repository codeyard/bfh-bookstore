package org.bookstore.catalog.adapter;

public record SaleInfo(
    String country,
    String saleability,
    Boolean isEbook,
    Price listPrice,
    Price retailPrice
) {
}
