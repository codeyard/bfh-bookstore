package org.bookstore.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {

    private LocalDateTime date;
    private BigDecimal amount;
    private String creditCardNumber;
    private String transactionId;


    // <editor-fold desc="Getter and Setter">

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    // </editor-fold>
}
