package org.bookstore.payment;

import ebay.api.paypalapi.PayPalAPIAAInterface;
import org.bookstore.payment.dto.*;
import org.bookstore.payment.exception.PaymentFailedException;
import org.bookstore.payment.service.PaymentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PaymentServiceIT {

    @Autowired
    private PaymentService paymentService;

    @Value("${payment.maxAmount}")
    private BigDecimal maxAmount;

    @Test
    void makePayment_successful() {
        Customer customer = createCustomer();
        customer.getCreditCard().setNumber("2221000000000009");
        customer.getCreditCard().setType(CreditCardType.MASTER_CARD);
        customer.getCreditCard().setExpirationMonth(12);
        customer.getCreditCard().setExpirationYear(LocalDate.now().getYear());

        Payment payment = Assertions.assertDoesNotThrow(
                () -> paymentService.makePayment(customer, customer.getCreditCard(), BigDecimal.TEN));

        assertEquals(BigDecimal.TEN, payment.getAmount());

    }

    @Test
    void makePayment_throwsPaymentFailedExceptionBecauseOfAmount() {
        Customer customer = createCustomer();
        customer.getCreditCard().setNumber("2221000000000009");
        customer.getCreditCard().setType(CreditCardType.MASTER_CARD);
        customer.getCreditCard().setExpirationMonth(12);
        customer.getCreditCard().setExpirationYear(LocalDate.now().getYear());


        PaymentFailedException exception = Assertions.assertThrows(PaymentFailedException.class,
                () -> paymentService.makePayment(customer, customer.getCreditCard(), maxAmount));

        assertEquals("The amount exceeds the maximum amount for a single transaction.", exception.getFaultInfo());


    }

    @Test
    void makePayment_throwsPaymentFailedExceptionBecauseOfExpiredCard() {
        Customer customer = createCustomer();
        customer.getCreditCard().setNumber("2221000000000009");
        customer.getCreditCard().setType(CreditCardType.MASTER_CARD);
        customer.getCreditCard().setExpirationYear(LocalDate.now().getYear() - 1);

        PaymentFailedException exception = Assertions.assertThrows(PaymentFailedException.class,
                () -> paymentService.makePayment(customer, customer.getCreditCard(), new BigDecimal("1000")));


        assertEquals("This transaction cannot be processed. Please enter a valid credit card expiration year.", exception.getFaultInfo());

    }

    @Test
    void makePayment_throwsPaymentFailedExceptionBecauseOfInvalidCardNumber() {
        Customer customer = createCustomer();
        customer.getCreditCard().setNumber("11111");

        PaymentFailedException exception = Assertions.assertThrows(PaymentFailedException.class,
                () -> paymentService.makePayment(customer, customer.getCreditCard(), maxAmount.subtract(BigDecimal.valueOf(1))));

        assertEquals("This transaction cannot be processed. Please enter a valid credit card number and type.", exception.getFaultInfo());
    }


    private Customer createCustomer() {
        Customer customer = new Customer();

        Address address = new Address();
        address.setStreet("McLarens Pub");
        address.setPostalCode("80008");
        address.setCity("New York");
        address.setCountry("USA");
        address.setStateProvince("NY");

        CreditCard creditCard = new CreditCard();
        creditCard.setExpirationYear(LocalDate.now().getYear() + 1);
        creditCard.setExpirationMonth(12);
        creditCard.setType(CreditCardType.MASTER_CARD);
        creditCard.setNumber("54001105080960");

        customer.setAddress(address);
        customer.setCreditCard(creditCard);
        customer.setEmail("test@test.ch");
        customer.setFirstName("Barney");
        customer.setLastName("Stinson");
        customer.setUsername("barnebous");
        customer.setId(10000L);

        return customer;
    }


}
