package org.bookstore.customer.service;

import org.bookstore.customer.entity.Customer;
import org.bookstore.customer.exception.CustomerNotFoundException;
import org.bookstore.customer.exception.UsernameAlreadyExistsException;
import org.bookstore.customer.exception.UsernameNotMatchingException;
import org.bookstore.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

/**
 * The interface CustomerService defines a service to manage the customers of a bookstore.
 *
 * @author Igor Stojanovic, Raphael Gerber
 */
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Registers a customer.
     *
     * @param customer - the data of the customer (identifier must be null)
     * @return the data of the registered customer
     * @throws UsernameAlreadyExistsException - if the username already exists
     */
    public Customer registerCustomer(Customer customer) throws UsernameAlreadyExistsException {
        boolean usernameExists = customerRepository.existsByUsername(customer.getUsername());
        if (!usernameExists) {
            return customerRepository.saveAndFlush(customer);
        } else {
            throw new UsernameAlreadyExistsException(customer.getUsername());
        }
    }

    /**
     * Finds a customer by identifier.
     *
     * @param id - the identifier of the customer
     * @return the data of the found customer
     * @throws CustomerNotFoundException - if no customer with the specified identifier exists
     */
    public Customer findCustomer(long id) throws CustomerNotFoundException {
        return customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    /**
     * Updates the data of a customer.
     *
     * @param customer - the new data of the customer (username must not change)
     * @return the data of the updated customer
     * @throws CustomerNotFoundException    - if no customer with the corresponding identifier exists
     * @throws UsernameNotMatchingException - if the username does not match the existing username
     */
    public Customer updateCustomer(Customer customer) throws CustomerNotFoundException, UsernameNotMatchingException {
        Customer customerFound = findCustomer(customer.getId());
        if (!customerFound.getUsername().equals(customer.getUsername()))
            throw new UsernameNotMatchingException();

        return customerRepository.saveAndFlush(customer);
    }
}
