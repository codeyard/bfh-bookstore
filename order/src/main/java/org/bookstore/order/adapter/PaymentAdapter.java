package org.bookstore.order.adapter;

import org.bookstore.customer.entity.CreditCard;
import org.bookstore.customer.entity.Customer;
import org.bookstore.order.entity.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
public class PaymentAdapter {

    private final RestTemplate restTEmplate;

    @Value("${bookstore.payment.api-url}")
    private String paymentApiUrl;

    public PaymentAdapter(RestTemplateBuilder restTemplateBuilder) {
        this.restTEmplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
    }


    public Payment makePayment(Customer customer, CreditCard creditCard, BigDecimal amount) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(amount);
        paymentRequest.setCustomer(customer);
        paymentRequest.setCreditCard(creditCard);

        return this.restTEmplate.postForObject(paymentApiUrl, paymentRequest, Payment.class);
    }

}
