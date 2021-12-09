package ch.rgis.bookorders.customer.service;

import ch.rgis.bookorders.customer.entity.Customer;
import ch.rgis.bookorders.customer.exception.CustomerNotFoundException;
import ch.rgis.bookorders.customer.exception.UsernameAlreadyExistsException;
import ch.rgis.bookorders.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer registerCustomer(Customer customer) throws UsernameAlreadyExistsException {
        Optional<Customer> customerOptional = customerRepository.findById(customer.getId());
        if (!customerOptional.isPresent()) {
            return customerRepository.saveAndFlush(customer);
        } else {
            throw new UsernameAlreadyExistsException();
        }
    }

    public Customer findCustomer(long id) throws CustomerNotFoundException {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        return optionalCustomer.orElseThrow(CustomerNotFoundException::new);
    }

    public Customer updateCustomer(Customer customer) throws CustomerNotFoundException, UsernameAlreadyExistsException {
        Optional<Customer> customerOptional = customerRepository.findById(customer.getId());
        if(customerOptional.isEmpty()) {
            throw new CustomerNotFoundException();
        } else {
            try {
                Customer updatingCustomer = customerOptional.get();
                updatingCustomer.setUsername(customer.getUsername());
                return customerRepository.saveAndFlush(updatingCustomer);
            } catch (Exception e) {
                System.out.println(e);
                throw new UsernameAlreadyExistsException();
            }
        }
    }
}
