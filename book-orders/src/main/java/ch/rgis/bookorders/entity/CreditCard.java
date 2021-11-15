package ch.rgis.bookorders.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class CreditCard {

    @Enumerated(EnumType.STRING)
    private CreditCardType type;

    private String number;

    private Integer expirationMonth;

    private Integer expirationYear;
}
