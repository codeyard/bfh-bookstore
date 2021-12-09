package ch.rgis.bookorders.customer.repository;

import ch.rgis.bookorders.customer.entity.Address;
import ch.rgis.bookorders.customer.entity.CreditCard;
import ch.rgis.bookorders.customer.entity.CreditCardType;
import ch.rgis.bookorders.customer.entity.Customer;
import ch.rgis.bookorders.customer.service.CustomerService;
import ch.rgis.bookorders.customer.exception.CustomerNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertThrows;

@SpringBootTest
public class CustomerServiceIT {

    @Autowired
    private CustomerService customerService;



    @Test
    void updateCustomer_throwsCustomerNotFoundException() {
        Customer customer = createCustomer();
        assertThrows(CustomerNotFoundException.class, () -> customerService.updateCustomer(customer));
    }

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setId(10021L);
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
        address.setStateProvince("BE");
        address.setCity("Bern");
        address.setPostalCode("3001");
        address.setStreet("Bundesgasse 30");

        customer.setAddress(address);
        customer.setCreditCard(creditCard);
        return customer;
    }

}
