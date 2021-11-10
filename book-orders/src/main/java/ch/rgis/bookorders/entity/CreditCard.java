package ch.rgis.bookorders.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CreditCard {

    @Column(nullable = false)
    private CreditCardType type;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private Integer expirationMonth;

    @Column(nullable = false)
    private Integer expirationYear;
}
