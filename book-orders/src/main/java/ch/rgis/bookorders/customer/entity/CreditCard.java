package ch.rgis.bookorders.customer.entity;

import com.sun.istack.NotNull;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class CreditCard {
    @NotNull
    @Enumerated(EnumType.STRING)
    private CreditCardType type;
    @NotNull
    private String number;
    @NotNull
    private Integer expirationMonth;
    @NotNull
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
