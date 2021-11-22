package ch.rgis.bookorders.dto;

public interface CustomerOrderStatistics {

    Integer getYear();

    Long getCustomerId();

    String getCustomerName();

    Double getTotalAmount();

    Integer getOrderItemsCount();

    Double getAverageOrderValue();

}
