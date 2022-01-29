package org.bookstore.payment.controller;

import org.bookstore.payment.dto.CreditCard;
import org.bookstore.payment.dto.Customer;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Validated
public class PaymentRequest {

    @Valid
    private Customer customer;
    @Valid
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
