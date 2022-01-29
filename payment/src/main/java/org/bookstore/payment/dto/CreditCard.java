package org.bookstore.payment.dto;

import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Validated
public class CreditCard {

    @Valid
    @NotNull(message = "Missing credit card type")
    private CreditCardType type;
    @NotEmpty(message = "Missing credit card number")
    private String number;
    @NotNull(message = "Missing expiration month")
    private Integer expirationMonth;
    @NotNull(message = "Missing expiration year")
    private Integer expirationYear;


    //<editor-fold desc="Getter and Setter">
    public CreditCardType getType() {
        return type;
    }

    public void setType(CreditCardType type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(Integer expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public Integer getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(Integer expirationYear) {
        this.expirationYear = expirationYear;
    }
    //</editor-fold>
}
