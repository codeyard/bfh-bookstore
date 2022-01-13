package org.bookstore.payment.service;

import ebay.api.paypalapi.DoDirectPaymentReq;
import ebay.api.paypalapi.DoDirectPaymentRequestType;
import ebay.api.paypalapi.PayPalAPIAAInterface;
import ebay.apis.corecomponenttypes.BasicAmountType;
import ebay.apis.eblbasecomponents.*;
import org.bookstore.payment.dto.CreditCard;
import org.bookstore.payment.dto.Customer;
import org.bookstore.payment.dto.Payment;
import org.bookstore.payment.exception.PaymentFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.ws.Holder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Service
public class PaymentService {

    private final PayPalAPIAAInterface paypalAPI;
    @Value("${payment.maxAmount:1000}")
    private BigDecimal maxAmount;

    public PaymentService(PayPalAPIAAInterface payPalAPIAAInterface) {
        this.paypalAPI = payPalAPIAAInterface;
    }

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

        DoDirectPaymentReq doDirectPaymentReq = new DoDirectPaymentReq();

        DoDirectPaymentRequestType doDirectPaymentRequest = new DoDirectPaymentRequestType();
        doDirectPaymentRequest.setVersion("204.0");

        DoDirectPaymentRequestDetailsType doDirectPaymentRequestDetails = new DoDirectPaymentRequestDetailsType();
        PaymentDetailsType paymentDetails = new PaymentDetailsType();

        BasicAmountType basicAmount = new BasicAmountType();
        basicAmount.setCurrencyID(CurrencyCodeType.CHF);
        basicAmount.setValue(String.valueOf(amount));
        paymentDetails.setOrderTotal(basicAmount);

        CreditCardDetailsType creditCardDetails = new CreditCardDetailsType();
        creditCardDetails.setCreditCardType(CreditCardTypeType.fromValue(creditCard.getType().toString()));
        creditCardDetails.setCreditCardNumber(creditCard.getNumber());
        creditCardDetails.setExpMonth(creditCard.getExpirationMonth());
        creditCardDetails.setExpYear(creditCard.getExpirationYear());

        PayerInfoType cardOwner = new PayerInfoType();
        cardOwner.setPayer(customer.getEmail());
        PersonNameType payerName = new PersonNameType();
        payerName.setFirstName(customer.getFirstName());
        payerName.setLastName(customer.getLastName());

        cardOwner.setPayerName(payerName);
        creditCardDetails.setCardOwner(cardOwner);


        doDirectPaymentRequestDetails.setPaymentAction(PaymentActionCodeType.SALE);
        doDirectPaymentRequestDetails.setCreditCard(creditCardDetails);
        doDirectPaymentRequestDetails.setPaymentDetails(paymentDetails);

        doDirectPaymentRequest.setDoDirectPaymentRequestDetails(doDirectPaymentRequestDetails);
        doDirectPaymentReq.setDoDirectPaymentRequest(doDirectPaymentRequest);


        CustomSecurityHeaderType requesterCredentials = new CustomSecurityHeaderType();
        UserIdPasswordType credentials = new UserIdPasswordType();
        credentials.setAppId("APP-80W284485P519543T");
        credentials.setUsername("test63094-facilitator_api1.gmail.com");
        credentials.setPassword("72WKET5JWR8GVZG9");
        credentials.setSignature("Ava.9fn09rkP1ewTi..a9clsi45HATCS2XaGQ2WdpnzUaD70cMY555-E");

        requesterCredentials.setCredentials(credentials);

        Holder<CustomSecurityHeaderType> header = new Holder<>(requesterCredentials);

        paypalAPI.doDirectPayment(doDirectPaymentReq, header);

        return payment;
    }

}
