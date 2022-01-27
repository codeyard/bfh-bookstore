package org.bookstore.customer.controller;

import org.bookstore.customer.entity.Customer;
import org.bookstore.customer.exception.CustomerNotFoundException;
import org.bookstore.customer.exception.IdNotMatchingException;
import org.bookstore.customer.exception.UsernameAlreadyExistsException;
import org.bookstore.customer.exception.UsernameNotMatchingException;
import org.bookstore.customer.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/customers")
@Validated
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Customer registerCustomer(@RequestBody @Valid Customer customer) throws UsernameAlreadyExistsException {
        return customerService.registerCustomer(customer);
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public Customer findCustomer(@PathVariable long id) throws CustomerNotFoundException {
        return customerService.findCustomer(id);
    }

    @PutMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Customer updateCustomer(
        @PathVariable long id,
        @RequestBody @Valid Customer customer) throws IdNotMatchingException, UsernameNotMatchingException, CustomerNotFoundException {
        if (!customer.getId().equals(id))
            throw new IdNotMatchingException();
        return customerService.updateCustomer(customer);
    }
}
