package org.bookstore.payment;

import org.bookstore.payment.dto.CreditCard;
import org.bookstore.payment.dto.CreditCardType;
import org.bookstore.payment.dto.Customer;
import org.bookstore.payment.dto.Payment;
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
        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("2221000000000009");
        creditCard.setType(CreditCardType.MASTER_CARD);
        creditCard.setExpirationMonth(12);
        creditCard.setExpirationYear(LocalDate.now().getYear());

        Payment payment = Assertions.assertDoesNotThrow(
            () -> paymentService.makePayment(customer, creditCard, BigDecimal.TEN));

        assertEquals(BigDecimal.TEN, payment.getAmount());

    }

    @Test
    void makePayment_throwsPaymentFailedExceptionBecauseOfAmount() {
        Customer customer = createCustomer();
        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("2221000000000009");
        creditCard.setType(CreditCardType.MASTER_CARD);
        creditCard.setExpirationMonth(12);
        creditCard.setExpirationYear(LocalDate.now().getYear());


        PaymentFailedException exception = Assertions.assertThrows(PaymentFailedException.class,
            () -> paymentService.makePayment(customer, creditCard, maxAmount));

        assertEquals("The amount exceeds the maximum amount for a single transaction.", exception.getFaultInfo());


    }

    @Test
    void makePayment_throwsPaymentFailedExceptionBecauseOfExpiredCard() {
        Customer customer = createCustomer();
        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("2221000000000009");
        creditCard.setType(CreditCardType.MASTER_CARD);
        creditCard.setExpirationMonth(1);
        creditCard.setExpirationYear(LocalDate.now().getYear() - 1);

        PaymentFailedException exception = Assertions.assertThrows(PaymentFailedException.class,
            () -> paymentService.makePayment(customer, creditCard, new BigDecimal("1000")));


        assertEquals("This transaction cannot be processed. Please enter a valid credit card expiration year.", exception.getFaultInfo());

    }

    @Test
    void makePayment_throwsPaymentFailedExceptionBecauseOfInvalidCardNumber() {
        Customer customer = createCustomer();
        CreditCard creditCard = new CreditCard();
        creditCard.setNumber("11111");
        creditCard.setType(CreditCardType.VISA);
        creditCard.setExpirationYear(LocalDate.now().getYear() + 1);

        PaymentFailedException exception = Assertions.assertThrows(PaymentFailedException.class,
            () -> paymentService.makePayment(customer, creditCard, maxAmount.subtract(BigDecimal.valueOf(1))));

        assertEquals("This transaction cannot be processed. Please enter a valid credit card number and type.", exception.getFaultInfo());
    }


    private Customer createCustomer() {
        Customer customer = new Customer();

        CreditCard creditCard = new CreditCard();
        creditCard.setExpirationYear(LocalDate.now().getYear() + 1);
        creditCard.setExpirationMonth(12);
        creditCard.setType(CreditCardType.MASTER_CARD);
        creditCard.setNumber("54001105080960");

        customer.setEmail("test@test.ch");
        customer.setFirstName("Barney");
        customer.setLastName("Stinson");
        customer.setId(10000L);

        return customer;
    }


}
