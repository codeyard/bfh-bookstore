package ch.rgis.bookorders.customer.service;

import ch.rgis.bookorders.customer.entity.Customer;
import ch.rgis.bookorders.customer.exception.CustomerNotFoundException;
import ch.rgis.bookorders.customer.exception.UsernameAlreadyExistsException;
import ch.rgis.bookorders.customer.repository.CustomerRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        try {
            return customerRepository.saveAndFlush(customer);
        } catch (DataIntegrityViolationException e) {
            throw new UsernameAlreadyExistsException();
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
        return customerRepository.findById(id).orElseThrow(CustomerNotFoundException::new);
    }

    /**
     * Updates the data of a customer.
     *
     * @param customer - the new data of the customer (username must not change)
     * @return the data of the updated customer
     * @throws CustomerNotFoundException      - if no customer with the corresponding identifier exists
     * @throws UsernameAlreadyExistsException - if the username is to be changed and the new username already exists
     */
    public Customer updateCustomer(Customer customer) throws CustomerNotFoundException, UsernameAlreadyExistsException {
        findCustomer(customer.getId());
        try {
            return customerRepository.saveAndFlush(customer);
        } catch (DataIntegrityViolationException e) {
            throw new UsernameAlreadyExistsException();
        }
    }
}
