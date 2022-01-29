package org.bookstore.payment.dto;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Validated
public class Customer {

    @NotNull(message = "Missing customer id")
    private Long id;
    @NotEmpty(message = "Missing first name")
    private String firstName;
    @NotEmpty(message = "Missing last name")
    private String lastName;
    @NotEmpty(message = "Missing email")
    private String email;


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

    // </editor-fold>


}
