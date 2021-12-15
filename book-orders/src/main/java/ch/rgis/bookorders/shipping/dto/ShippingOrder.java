package ch.rgis.bookorders.shipping.dto;


import ch.rgis.bookorders.order.entity.Order;
import ch.rgis.bookorders.order.entity.OrderItem;

import java.util.List;

public class ShippingOrder {
    private Long orderId;
    private Customer customer;
    private Address address;
    private List<OrderItem> items;


    public ShippingOrder(Order order) {
        orderId = order.getId();
        customer = new Customer(order.getCustomer());
        address = new Address(order.getAddress());
        items = order.getItems();
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

    private class Customer {
        private final Long id;
        private final String firstName;
        private final String lastName;
        private final String email;

        public Customer(ch.rgis.bookorders.customer.entity.Customer customer) {
            this.id = customer.getId();
            this.firstName = customer.getFirstName();
            this.lastName = customer.getLastName();
            this.email = customer.getEmail();
        }

        //<editor-fold desc="Getter and Setter">
        public Long getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }
        //</editor-fold>
    }

    private class Address {
        private final String street;
        private final String city;
        private final String stateProvince;
        private final String postalCode;
        private final String country;

        public Address(ch.rgis.bookorders.customer.entity.Address address) {
            this.street = address.getStreet();
            this.city = address.getCity();
            this.stateProvince = address.getStateProvince();
            this.postalCode = address.getPostalCode();
            this.country = address.getCountry();
        }

        //<editor-fold desc="Getter and Setter">
        public String getStreet() {
            return street;
        }

        public String getCity() {
            return city;
        }

        public String getStateProvince() {
            return stateProvince;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public String getCountry() {
            return country;
        }
        //</editor-fold>
    }

}
