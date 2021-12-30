package org.bookstore.order.dto;

public interface CustomerOrderStatistics {

    Integer getYear();

    Long getCustomerId();

    String getCustomerName();

    Double getTotalAmount();

    Integer getNumberOfBooks();

    Double getAverageBookPrice();

}
