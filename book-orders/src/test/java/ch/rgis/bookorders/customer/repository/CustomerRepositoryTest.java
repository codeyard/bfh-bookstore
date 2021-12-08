package ch.rgis.bookorders.customer.repository;

import ch.rgis.bookorders.customer.dto.CustomerInfoDTO;
import ch.rgis.bookorders.customer.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;


    // Query 1: Find Customer by specific E-Mail address
    @Test
    void findUserByEmail_foundOne() {
        Optional<Customer> customer = customerRepository.findByEmail("nunigu@gmail.com");

        assertTrue(customer.isPresent());
        assertEquals("Igor", customer.get().getUsername());
    }

    @Test
    void findUserByEmail_foundNone() {
        Optional<Customer> customer = customerRepository.findByEmail("bill@gates.com");

        assertTrue(customer.isEmpty());
    }


    // Query 2: Find Customer Information for Customers whose first or last name contains a specific name while ignoring its case.
    @Test
    void findCustomersByName_foundOne() {
        List<CustomerInfoDTO> customerList = customerRepository.findCustomersByName("go");

        assertEquals(1, customerList.size());
        assertEquals("Igor", customerList.get(0).firstName());
    }

    @Test
    void findCustomersByName_foundNone() {
        List<CustomerInfoDTO> customerList = customerRepository.findCustomersByName("gates");

        assertEquals(0, customerList.size());
    }
}
