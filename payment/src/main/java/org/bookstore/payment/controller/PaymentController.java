package org.bookstore.payment.controller;

import org.bookstore.payment.dto.Payment;
import org.bookstore.payment.exception.PaymentFailedException;
import org.bookstore.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/payments")
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Payment makePayment(@RequestBody @Valid PaymentRequest payment) throws PaymentFailedException {
        return paymentService.makePayment(payment.getCustomer(), payment.getCreditCard(), payment.getAmount());
    }

}
