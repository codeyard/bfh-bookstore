package org.bookstore.customer.repository;

import org.bookstore.customer.entity.Address;
import org.bookstore.customer.entity.CreditCard;
import org.bookstore.customer.entity.CreditCardType;
import org.bookstore.customer.entity.Customer;
import org.bookstore.customer.exception.CustomerNotFoundException;
import org.bookstore.customer.exception.UsernameAlreadyExistsException;
import org.bookstore.customer.exception.UsernameNotMatchingException;
import org.bookstore.customer.service.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class CustomerServiceIT {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void registerCustomer_successful() throws UsernameAlreadyExistsException {
        Customer customer = createCustomer();

        Customer customerSaved = customerService.registerCustomer(customer);
        Assertions.assertEquals(customer.getFirstName(), customerSaved.getFirstName());
        Assertions.assertEquals(customer.getLastName(), customerSaved.getLastName());
        Assertions.assertEquals(customer.getEmail(), customerSaved.getEmail());
    }

    @Test
    void registerCustomer_throwsUsernameAlreadyExistsException() {
        Customer customer = createCustomer();
        customer.setUsername("Igor");
        Assertions.assertThrows(UsernameAlreadyExistsException.class, () -> customerService.registerCustomer(customer));
    }

    @Test
    void findCustomer_successful() throws CustomerNotFoundException {
        Customer customer = customerService.findCustomer(10000L);
        Assertions.assertEquals("Corabelle", customer.getFirstName());
        Assertions.assertEquals("Scoular", customer.getLastName());
        Assertions.assertEquals("cscoular0@tinyurl.com", customer.getEmail());
    }

    @Test
    void findCustomer_throwsCustomerNotFoundException() {
        Assertions.assertThrows(CustomerNotFoundException.class, () -> customerService.findCustomer(99999L));
    }

    @Test
    void updateCustomer_successful() {
        Optional<Customer> customer = customerRepository.findByEmail("nunigu@gmail.com");
        Assertions.assertTrue(customer.isPresent());
        Assertions.assertEquals("Igor", customer.get().getFirstName());

        customer.get().setFirstName("Barney");
        customer.get().setLastName("Stinson");
        Assertions.assertDoesNotThrow(() -> customerService.updateCustomer(customer.get()));

        Optional<Customer> customerUpdated = customerRepository.findByEmail("nunigu@gmail.com");
        Assertions.assertEquals("Barney", customerUpdated.get().getFirstName());
        Assertions.assertEquals("Stinson", customerUpdated.get().getLastName());
    }

    @Test
    void updateCustomer_throwsCustomerNotFoundException() {
        Customer customer = createCustomer();
        customer.setId(99999L);
        Assertions.assertThrows(CustomerNotFoundException.class, () -> customerService.updateCustomer(customer));
    }

    @Test
    void updateCustomer_throwsUsernameNotMatchingException() {
        Optional<Customer> customer = customerRepository.findByEmail("cpryora@huffingtonpost.com");
        Assertions.assertTrue(customer.isPresent());
        Assertions.assertEquals("Cynthia", customer.get().getFirstName());
        Assertions.assertEquals("cpryora", customer.get().getUsername());

        customer.get().setUsername("sreami");
        Assertions.assertThrows(UsernameNotMatchingException.class, () -> customerService.updateCustomer(customer.get()));
    }

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setUsername("billGates");
        customer.setEmail("bill@gates.com");
        customer.setFirstName("Bill");
        customer.setLastName("Gates");

        CreditCard creditCard = new CreditCard();
        creditCard.setType(CreditCardType.MASTER_CARD);
        creditCard.setNumber("1111222233334444");
        creditCard.setExpirationMonth(12);
        creditCard.setExpirationYear(2026);

        Address address = new Address();
        address.setStreet("Bundesgasse 30");
        address.setStateProvince("BE");
        address.setPostalCode("3001");
        address.setCity("Bern");
        address.setCountry("Switzerland");

        customer.setAddress(address);
        customer.setCreditCard(creditCard);
        return customer;
    }

}
