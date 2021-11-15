package ch.rgis.bookorders.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address {

    private String street;

    private String stateProvince;

    private String postalCode;

    private String city;

    private String country;
}
