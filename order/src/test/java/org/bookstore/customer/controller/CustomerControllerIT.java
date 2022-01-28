package org.bookstore.customer.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.minidev.json.JSONObject;
import org.bookstore.customer.entity.CreditCardType;
import org.bookstore.customer.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerIT {

    private static final String BASE_PATH = "/customers";

    @LocalServerPort
    int port;

    @BeforeEach
    public void init() {
        RestAssured.port = port;
    }

    @Test
    void registerCustomer_successful() {
        JSONObject requestParams = buildRequestBody();

        Customer savedCustomer = given()
            .log()
            .all()
            .body(requestParams.toJSONString())
            .contentType(ContentType.JSON)
            .when().post(BASE_PATH)
            .then().statusCode(CREATED.value())
            .extract()
            .as(Customer.class);

        assertThat(savedCustomer.getUsername().equals(requestParams.get("username"))).isTrue();
        assertThat(savedCustomer.getFirstName().equals(requestParams.get("firstName"))).isTrue();
        assertThat(savedCustomer.getLastName().equals(requestParams.get("lastName"))).isTrue();
        assertThat(savedCustomer.getEmail().equals(requestParams.get("email"))).isTrue();
        assertThat(savedCustomer.getAddress() != null).isTrue();
        assertThat(savedCustomer.getCreditCard() != null).isTrue();
    }

    @Test
    void registerCustomer_missingEmailAddress() {
        JSONObject requestParams = buildRequestBody();
        requestParams.remove("email");

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
            .body("message", equalTo("Missing email address"))
            .body("path", equalTo("/customers"));
    }

    @Test
    void registerCustomer_usernameAlreadyExists() {
        JSONObject requestParams = buildRequestBody();
        requestParams.replace("username", "acluney6");

        given()
            .log()
            .all()
            .body(requestParams.toJSONString())
            .contentType(ContentType.JSON)
            .when().post(BASE_PATH)
            .then().statusCode(CONFLICT.value())
            .body("$", hasKey("timestamp"))
            .body("status", equalTo(409))
            .body("error", equalTo("Conflict"))
            .body("message", equalTo("Username 'acluney6' already exists"))
            .body("path", equalTo("/customers"))
            .body("code", equalTo("USERNAME_ALREADY_EXISTS"));
    }

    @Test
    void findCustomer_successful() {
        Long customerId = 10020L;

        Customer receivedCustomer = given()
            .log()
            .all()
            .pathParam("id", customerId)
            .when().get(BASE_PATH + "/{id}")
            .then().statusCode(OK.value())
            .contentType(ContentType.JSON)
            .extract()
            .as(Customer.class);

        assertThat(receivedCustomer.getId().equals(customerId)).isTrue();
        assertThat(receivedCustomer.getUsername().equals("Igor")).isTrue();
        assertThat(receivedCustomer.getFirstName().equals("Igor")).isTrue();
        assertThat(receivedCustomer.getLastName().equals("Stojanovic")).isTrue();
        assertThat(receivedCustomer.getEmail().equals("nunigu@gmail.com")).isTrue();
        assertThat(receivedCustomer.getCreditCard().getType().equals(CreditCardType.VISA)).isTrue();
    }

    @Test
    void findCustomer_customerNotFound() {
        Long customerId = 11111L;

        given()
            .log()
            .all()
            .pathParam("id", customerId)
            .when().get(BASE_PATH + "/{id}")
            .then().statusCode(NOT_FOUND.value())
            .body("$", hasKey("timestamp"))
            .body("status", equalTo(404))
            .body("error", equalTo("Not Found"))
            .body("message", equalTo("Customer " + customerId + " not found"))
            .body("path", equalTo("/customers/" + customerId))
            .body("code", equalTo("CUSTOMER_NOT_FOUND"));
    }

    @Test
    void updateCustomer_successful() {
        JSONObject requestParams = buildRequestBody();
        Long customerId = 10019L;
        requestParams.put("id", customerId);
        requestParams.replace("username", "mchatburnj");
        requestParams.replace("email", "smith@example.org");

        Customer updateCustomer = given()
            .log()
            .all()
            .pathParam("id", customerId)
            .body(requestParams.toJSONString())
            .contentType(ContentType.JSON)
            .when().put(BASE_PATH + "/{id}")
            .then().statusCode(OK.value())
            .extract()
            .as(Customer.class);

        assertThat(updateCustomer.getId().equals(customerId)).isTrue();
        assertThat(updateCustomer.getUsername().equals(requestParams.get("username"))).isTrue();
        assertThat(updateCustomer.getFirstName().equals(requestParams.get("firstName"))).isTrue();
        assertThat(updateCustomer.getLastName().equals(requestParams.get("lastName"))).isTrue();
        assertThat(updateCustomer.getEmail().equals(requestParams.get("email"))).isTrue();
        assertThat(updateCustomer.getAddress() != null).isTrue();
        assertThat(updateCustomer.getCreditCard() != null).isTrue();
    }

    @Test
    void updateCustomer_identifierNotMatching() {
        JSONObject requestParams = buildRequestBody();
        Long customerId = 10019L;
        requestParams.put("id", 10020L);

        given()
            .log()
            .all()
            .pathParam("id", customerId)
            .body(requestParams.toJSONString())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .when().put(BASE_PATH + "/{id}")
            .then().statusCode(BAD_REQUEST.value())
            .body("$", hasKey("timestamp"))
            .body("status", equalTo(400))
            .body("error", equalTo("Bad Request"))
            .body("message", equalTo("Identifier not matching"))
            .body("path", equalTo("/customers/" + customerId));
    }

    @Test
    void updateCustomer_customerNotFound() {
        JSONObject requestParams = buildRequestBody();
        Long customerId = 99999L;
        requestParams.put("id", customerId);

        given()
            .log()
            .all()
            .pathParam("id", customerId)
            .body(requestParams.toJSONString())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .when().put(BASE_PATH + "/{id}")
            .then().statusCode(NOT_FOUND.value())
            .body("$", hasKey("timestamp"))
            .body("status", equalTo(404))
            .body("error", equalTo("Not Found"))
            .body("message", equalTo("Customer " + customerId + " not found"))
            .body("path", equalTo("/customers/" + customerId))
            .body("code", equalTo("CUSTOMER_NOT_FOUND"));
    }

    @Test
    void updateCustomer_usernameNotMatching() {
        JSONObject requestParams = buildRequestBody();
        Long customerId = 10019L;
        requestParams.put("id", customerId);
        requestParams.replace("username", "barney_stinson");

        given()
            .log()
            .all()
            .pathParam("id", customerId)
            .body(requestParams.toJSONString())
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .when().put(BASE_PATH + "/{id}")
            .then().statusCode(CONFLICT.value())
            .body("$", hasKey("timestamp"))
            .body("status", equalTo(409))
            .body("error", equalTo("Conflict"))
            .body("message", equalTo("Username must not change"))
            .body("path", equalTo("/customers/" + customerId))
            .body("code", equalTo("USERNAME_NOT_MATCHING"));

    }


    private net.minidev.json.JSONObject buildRequestBody() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("username", "alice");
        requestBody.put("firstName", "Alice");
        requestBody.put("lastName", "Smith");
        requestBody.put("email", "alice@example.org");

        JSONObject address = new JSONObject();
        address.put("street", "123 Maple Street");
        address.put("city", "Mill Valley");
        address.put("stateProvince", "CA");
        address.put("postalCode", 90952);
        address.put("country", "US");

        JSONObject creditCard = new JSONObject();
        creditCard.put("type", "MASTER_CARD");
        creditCard.put("number", "5400000000000005");
        creditCard.put("expirationMonth", 1);
        creditCard.put("expirationYear", 2025);

        requestBody.put("address", address);
        requestBody.put("creditCard", creditCard);

        return requestBody;
    }
}
