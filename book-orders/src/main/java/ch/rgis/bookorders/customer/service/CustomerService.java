package ch.rgis.bookorders.customer.service;

import ch.rgis.bookorders.customer.entity.Customer;
import ch.rgis.bookorders.customer.exception.CustomerNotFoundException;
import ch.rgis.bookorders.customer.exception.UsernameAlreadyExistsException;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    public Customer registerCustomer(Customer customer) throws UsernameAlreadyExistsException {
        return customer;
    }

    public Customer findCustomer(long id) throws CustomerNotFoundException {
        return null;
    }

    public Customer updateCustomer(Customer customer) throws CustomerNotFoundException, UsernameAlreadyExistsException {
        return customer;
    }
}
