package org.bookstore.payment.service;

import ebay.api.paypalapi.DoDirectPaymentReq;
import ebay.api.paypalapi.DoDirectPaymentRequestType;
import ebay.api.paypalapi.DoDirectPaymentResponseType;
import ebay.api.paypalapi.PayPalAPIAAInterface;
import ebay.apis.corecomponenttypes.BasicAmountType;
import ebay.apis.eblbasecomponents.*;
import org.bookstore.payment.dto.CreditCard;
import org.bookstore.payment.dto.Customer;
import org.bookstore.payment.dto.Payment;
import org.bookstore.payment.exception.PaymentFailedException;
import org.bookstore.payment.util.DateMapper;
import org.springframework.stereotype.Service;

import javax.xml.ws.Holder;
import java.math.BigDecimal;
@Service
public class PaymentService {

    private final PayPalAPIAAInterface paypalAPI;

    public PaymentService(PayPalAPIAAInterface payPalAPIAAInterface) {
        this.paypalAPI = payPalAPIAAInterface;
    }

    public Payment makePayment(Customer customer, CreditCard creditCard, BigDecimal amount) throws PaymentFailedException {

        DoDirectPaymentResponseType doDirectPaymentResponseType = submitPaypalPayment(customer, creditCard, amount);

        if (doDirectPaymentResponseType.getErrors().size() > 0) {
            for (ErrorType errorType : doDirectPaymentResponseType.getErrors()) {
                switch (errorType.getErrorCode()) {
                    // https://developer.paypal.com/api/nvp-soap/errors/
                    case "10563" -> throw new PaymentFailedException(errorType.getLongMessage());
                    case "10527" -> throw new PaymentFailedException(errorType.getLongMessage());
                    case "10562" -> throw new PaymentFailedException(errorType.getLongMessage());
                    case "10553" -> throw new PaymentFailedException(errorType.getLongMessage());
                    case "10414" -> throw new PaymentFailedException(errorType.getLongMessage());
                    default -> throw new PaymentFailedException("Something went wrong");
                }
            }
        }

        Payment payment = new Payment();
        payment.setDate(DateMapper.map(doDirectPaymentResponseType.getTimestamp()));
        payment.setAmount(amount);
        payment.setCreditCardNumber(creditCard.getNumber());
        payment.setTransactionId(doDirectPaymentResponseType.getTransactionID());

        return payment;
    }

    private DoDirectPaymentResponseType submitPaypalPayment(Customer customer, CreditCard creditCard, BigDecimal amount) {
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
        creditCardDetails.setCreditCardType(CreditCardTypeType.fromValue(creditCard.getType().value()));
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

        return paypalAPI.doDirectPayment(doDirectPaymentReq, header);
    }

}
