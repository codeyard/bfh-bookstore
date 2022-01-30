package org.bookstore.customer.entity;

import com.sun.istack.NotNull;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Entity
@Validated
public class Customer {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
    @SequenceGenerator(name = "customer_seq", sequenceName = "customer_seq")
    private Long id;

    @NotNull
    @NotBlank(message = "Missing first name")
    private String firstName;
    @NotNull
    @NotBlank(message = "Missing last name")
    private String lastName;
    @NotNull
    @NotBlank(message = "Missing email address")
    private String email;
    @NotNull
    @Column(unique = true)
    @NotBlank(message = "Missing username")
    private String username;
    @NotNull
    @Embedded()
    @Valid
    private Address address;
    @NotNull
    @Embedded()
    @Valid
    private CreditCard creditCard;


    // <editor-fold desc="Getter and Setter">

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    // </editor-fold>


}
