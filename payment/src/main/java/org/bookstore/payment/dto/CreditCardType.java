package org.bookstore.payment.dto;

public enum CreditCardType {
    MASTER_CARD("MasterCard"), VISA("Visa");

    private final String value;

    CreditCardType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

}
