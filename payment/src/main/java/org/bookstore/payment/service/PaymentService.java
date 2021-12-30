package org.bookstore.payment.service;

import org.bookstore.payment.dto.CreditCard;
import org.bookstore.payment.dto.Customer;
import org.bookstore.payment.dto.Payment;
import org.bookstore.payment.exception.PaymentFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Service
public class PaymentService {

    @Value("${payment.maxAmount:1000}")
    private BigDecimal maxAmount;

    public Payment makePayment(Customer customer, CreditCard creditCard, BigDecimal amount) throws PaymentFailedException {

        // Case 1: Total order amount too high
        if (amount.compareTo(maxAmount) > 0) {
            throw new PaymentFailedException(PaymentFailedException.ErrorCode.AMOUNT_EXCEEDS_LIMIT);
        }

        // Case 2: Credit card expired
        LocalDate initial = LocalDate.of(creditCard.getExpirationYear(), creditCard.getExpirationMonth(), 1);
        LocalDate expirationDate = initial.with(lastDayOfMonth());
        if (expirationDate.isBefore(LocalDate.now())) {
            throw new PaymentFailedException(PaymentFailedException.ErrorCode.CREDIT_CARD_EXPIRED);
        }

        // Case 3: Credit card number invalid
        String regex = "(r'^[0-9]{12}$|^[0-9]{14}$|^[0-9]{16}$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(creditCard.getNumber().replaceAll("-", ""));
        if (!matcher.matches()) {
            throw new PaymentFailedException(PaymentFailedException.ErrorCode.INVALID_CREDIT_CARD);
        }


        Payment payment = new Payment();
        payment.setDate(LocalDateTime.now());
        payment.setAmount(amount);
        payment.setCreditCardNumber(creditCard.getNumber());
        payment.setTransactionId("1");

        return payment;
    }

}
