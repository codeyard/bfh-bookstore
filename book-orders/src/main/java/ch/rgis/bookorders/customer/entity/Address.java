package ch.rgis.bookorders.customer.entity;

import com.sun.istack.NotNull;

import javax.persistence.Embeddable;

@Embeddable
public class Address {
    @NotNull
    private String street;

    private String stateProvince;
    @NotNull
    private String postalCode;
    @NotNull
    private String city;
    @NotNull
    private String country;


    //<editor-fold desc="Getter and Setter">
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    //</editor-fold>


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return street.equals(address.street) && stateProvince.equals(address.stateProvince) && postalCode.equals(address.postalCode) && city.equals(address.city) && country.equals(address.country);
    }
}
