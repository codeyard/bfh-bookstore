package org.bookstore.order.adapter;

import org.bookstore.customer.entity.CreditCard;
import org.bookstore.customer.entity.Customer;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PaymentRequest {

    @NotNull
    private Customer customer;
    @NotNull
    private CreditCard creditCard;
    @NotNull(message = "Missing payment amount")
    private BigDecimal amount;

    // <editor-fold desc="Getter and Setter">

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    // </editor-fold>
}
