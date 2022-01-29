package org.bookstore.payment.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.minidev.json.JSONObject;
import org.bookstore.payment.dto.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentControllerIT {

    private static final String BASE_PATH = "/payments";

    @LocalServerPort
    int port;

    @BeforeEach
    public void init() {
        RestAssured.port = port;
    }

    @Test
    void makePayment_successful() {
        JSONObject requestParams = buildRequestBody();


        Payment successfulPayment = given()
            .log()
            .all()
            .body(requestParams.toJSONString())
            .contentType(ContentType.JSON)
            .when().post(BASE_PATH)
            .then().statusCode(OK.value())
            .extract()
            .as(Payment.class);

        assertThat(successfulPayment.getDate().getDayOfMonth()).isEqualTo(LocalDate.now().getDayOfMonth());
        assertThat(successfulPayment.getDate().getMonth()).isEqualTo(LocalDate.now().getMonth());
        assertThat(successfulPayment.getDate().getYear()).isEqualTo(LocalDate.now().getYear());
        assertThat(successfulPayment.getAmount()).isEqualTo(BigDecimal.valueOf((double) requestParams.get("amount")));
        assertThat(successfulPayment.getCreditCardNumber()).isEqualTo("5400000000000005");
        assertNotNull(successfulPayment.getTransactionId());
    }

    @Test
    void makePayment_invalidPaymentData_throwsError() {
        JSONObject requestParams = buildRequestBody();
        requestParams.remove("amount");

        given()
            .log()
            .all()
            .body(requestParams.toJSONString())
            .contentType(ContentType.JSON)
            .when().post(BASE_PATH)
            .then().statusCode(BAD_REQUEST.value())
            .body("$", hasKey("timestamp"))
            .body("status", equalTo(400))
            .body("error", equalTo("Bad Request"))
            .body("message", equalTo("Missing payment amount"))
            .body("path", equalTo(BASE_PATH));
    }

    @Test
    void makePayment_paymentFailed_throwsError() {
        JSONObject requestParams = buildRequestBody();
        JSONObject invalidCreditCard = new JSONObject();
        invalidCreditCard.put("type", "MASTER_CARD");
        invalidCreditCard.put("number", "5400000000000005");
        invalidCreditCard.put("expirationMonth", 2);
        invalidCreditCard.put("expirationYear", LocalDate.now().getYear() - 1);
        requestParams.put("creditCard", invalidCreditCard);

        given()
            .log()
            .all()
            .body(requestParams.toJSONString())
            .contentType(ContentType.JSON)
            .when().post(BASE_PATH)
            .then().statusCode(UNPROCESSABLE_ENTITY.value())
            .body("$", hasKey("timestamp"))
            .body("status", equalTo(422))
            .body("error", equalTo("Unprocessable Entity"))
            .body("message", equalTo("Invalid credit card number or type"))
            .body("path", equalTo(BASE_PATH))
            .body("code", equalTo("INVALID_CREDIT_CARD"));
    }


    private JSONObject buildRequestBody() {
        JSONObject requestBody = new JSONObject();
        JSONObject customerObject = new JSONObject();
        customerObject.put("id", 100);
        customerObject.put("firstName", "Alice");
        customerObject.put("lastName", "Smith");
        customerObject.put("email", "alice@example.org");
        requestBody.put("customer", customerObject);
        JSONObject creditCardObject = new JSONObject();
        creditCardObject.put("type", "MASTER_CARD");
        creditCardObject.put("number", "5400000000000005");
        creditCardObject.put("expirationMonth", 2);
        creditCardObject.put("expirationYear", LocalDate.now().getYear() + 1);
        requestBody.put("creditCard", creditCardObject);
        requestBody.put("amount", 58.99);
        return requestBody;
    }
}
