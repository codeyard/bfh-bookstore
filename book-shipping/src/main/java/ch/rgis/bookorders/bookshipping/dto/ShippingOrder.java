package ch.rgis.bookorders.bookshipping.dto;

import java.math.BigDecimal;
import java.util.List;

public class ShippingOrder {
    private Long orderId;
    private Customer customer;
    private Address address;
    private List<OrderItem> items;

    public ShippingOrder() {
    }

    public ShippingOrder(Long orderId, Customer customer, Address address, List<OrderItem> items) {
        this.orderId = orderId;
        this.customer = customer;
        this.address = address;
        this.items = items;
    }

    //<editor-fold desc="Getter and Setter">
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    //</editor-fold>


    private record Customer(Long id, String firstName, String lastName,
                            String email) {
    }

    private record Address(String street, String city, String stateProvince,
                           String postalCode, String country) {
    }

    private record OrderItem(Long id, Book book,
                             Integer quantity) {
    }

    private record Book(String isbn, String title, String authors, String publisher,
                        BigDecimal price) {
    }


    @Override
    public String toString() {
        return "ShippingOrder{" +
            "orderId=" + orderId +
            ", customer=" + customer +
            ", address=" + address +
            ", items=" + items +
            '}';
    }
}
