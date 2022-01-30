package org.bookstore.customer.entity;


import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.validation.constraints.*;

@Embeddable
public class CreditCard {
    @NotNull
    @Enumerated(EnumType.STRING)
    @Valid
    private CreditCardType type;
    @NotNull
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
