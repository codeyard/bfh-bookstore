package ch.rgis.bookpayment;

import ch.rgis.bookpayment.dto.Address;
import ch.rgis.bookpayment.dto.CreditCard;
import ch.rgis.bookpayment.dto.CreditCardType;
import ch.rgis.bookpayment.dto.Customer;
import ch.rgis.bookpayment.exception.PaymentFailedException;
import ch.rgis.bookpayment.service.PaymentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest
public class PaymentServiceIT {

    @Autowired
    private PaymentService paymentService;

    @Value("${payment.maxAmount}")
    private BigDecimal maxAmount;

    @Test
    void makePayment_successful() {
        Customer customer = createCustomer();

        Assertions.assertDoesNotThrow(
                () -> paymentService.makePayment(customer, customer.getCreditCard(), maxAmount.subtract(BigDecimal.valueOf(1))));
    }

    @Test
    void makePayment_throwsPaymentFailedExceptionBecauseOfAmount() {
        Customer customer = createCustomer();

        PaymentFailedException exception = Assertions.assertThrows(PaymentFailedException.class,
                () -> paymentService.makePayment(customer, customer.getCreditCard(), maxAmount.add(BigDecimal.valueOf(1))));

        Assertions.assertEquals(PaymentFailedException.ErrorCode.AMOUNT_EXCEEDS_LIMIT, exception.getCode());
    }

    @Test
    void makePayment_throwsPaymentFailedExceptionBecauseOfExpiredCard() {
        Customer customer = createCustomer();
        customer.getCreditCard().setExpirationYear(LocalDate.now().getYear() - 1);

        PaymentFailedException exception = Assertions.assertThrows(PaymentFailedException.class,
                () -> paymentService.makePayment(customer, customer.getCreditCard(), maxAmount.subtract(BigDecimal.valueOf(1))));


        Assertions.assertEquals(PaymentFailedException.ErrorCode.CREDIT_CARD_EXPIRED, exception.getCode());
    }

    @Test
    void makePayment_throwsPaymentFailedExceptionBecauseOfInvalidCardNumber() {
        Customer customer = createCustomer();
        customer.getCreditCard().setNumber("11111");

        PaymentFailedException exception = Assertions.assertThrows(PaymentFailedException.class,
                () -> paymentService.makePayment(customer, customer.getCreditCard(), maxAmount.subtract(BigDecimal.valueOf(1))));

        Assertions.assertEquals(PaymentFailedException.ErrorCode.INVALID_CREDIT_CARD, exception.getCode());
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
