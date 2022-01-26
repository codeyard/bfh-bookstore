package org.bookstore.catalog.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.minidev.json.JSONObject;
import org.bookstore.catalog.entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CatalogControllerIT {

    private static final String BASE_PATH = "/books";

    @LocalServerPort
    int port;

    @BeforeEach
    public void init() {
        RestAssured.port = port;
    }

    @Test
    void getBook_successful() {
        Book receivedBook = given()
                .log()
                .all()
                .pathParam("isbn", "395440866X")
                .when().get(BASE_PATH + "/{isbn}")
                .then().statusCode(OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(Book.class);

        assertThat(receivedBook.getIsbn().equals("395440866X"));
        assertThat(receivedBook.getTitle().equals("Flower Power Letterings"));
        assertThat(receivedBook.getSubtitle() == null);

    }

    @Test
    void getBook_invalidPathParam_throwsError() {
        given()
                .log()
                .all()
                .pathParam("isbn", "11111")
                .when().get(BASE_PATH + "/{isbn}")
                .then().statusCode(BAD_REQUEST.value())
                .body("$", hasKey("timestamp"))
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("getBook.isbn: ISBN is not valid"))
                .body("path", equalTo("/books/11111"));
    }

    @Test
    void getBook_BookNotFound() {
        given()
                .log()
                .all()
                .pathParam("isbn", "1111111111")
                .when().get(BASE_PATH + "/{isbn}")
                .then().statusCode(NOT_FOUND.value())
                .body("$", hasKey("timestamp"))
                .body("status", equalTo(404))
                .body("error", equalTo("Not Found"))
                .body("message", equalTo("Book with ISBN 1111111111 not found"))
                .body("path", equalTo("/books/1111111111"))
                .body("code", equalTo("BOOK_NOT_FOUND"));
    }

    @Test
    void findBooks_successful() {
        Book[] booksFound = given()
                .log()
                .all()
                .queryParam("keywords", "harry potter")
                .when().get(BASE_PATH)
                .then().statusCode(OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(Book[].class);

        List<Book> list = new ArrayList(Arrays.asList(booksFound));

        assertThat(list.size() > 0);

        list.forEach(book -> {
            assertThat(book.getIsbn() != null);
            assertThat(book.getTitle() != null);
            assertThat(book.getAuthors() != null);
            assertThat(book.getPublisher() != null);
            assertThat(book.getPrice() != null);
        });

    }

    @Test
    void findBook_invalidQueryParam_throwsError() {
        given()
                .log()
                .all()
                .queryParam("keywords", "")
                .when().get(BASE_PATH)
                .then().statusCode(BAD_REQUEST.value())
                .body("$", hasKey("timestamp"))
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("findBooks.keywords: must not be empty"))
                .body("path", equalTo("/books"))
                .body("$", not(hasKey("code")));
    }

    @Test
    void addBook_successful() {
        JSONObject requestParams = buildRequestBody();

        Book savedBook = given()
                .log()
                .all()
                .body(requestParams.toJSONString())
                .contentType(ContentType.JSON)
                .when().post(BASE_PATH)
                .then().statusCode(CREATED.value())
                .extract()
                .as(Book.class);

        assertThat(savedBook.getIsbn().equals(requestParams.get("isbn")));
        assertThat(savedBook.getTitle().equals(requestParams.get("title")));
        assertThat(savedBook.getAuthors().equals(requestParams.get("authors")));
        assertThat(savedBook.getPublisher().equals(requestParams.get("publisher")));
        assertThat(savedBook.getPublicationYear().equals(requestParams.get("publicationYear")));
        assertThat(savedBook.getNumberOfPages().equals(requestParams.get("numberOfPages")));
        assertThat(savedBook.getPrice().equals(requestParams.get("price")));
        assertThat(savedBook.getSubtitle() == null);
        assertThat(savedBook.getDescription() == null);
        assertThat(savedBook.getImageUrl() == null);
    }


    @Test
    void addBook_invalidBook_throwsValidationError() {
        JSONObject requestParams = buildRequestBody();
        requestParams.remove("title");

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
                .body("message", equalTo("Missing book title"))
                .body("path", equalTo("/books"))
                .body("$", not(hasKey("code")));
    }

    @Test
    void addBook_alreadyExists_throwsValidationError() {
        JSONObject requestParams = buildRequestBody();
        String newTitle = "Das inoffizielle Harry-Potter-Kochbuch-Part-2";
        requestParams.replace("title", newTitle);
        requestParams.replace("isbn", "3959713983");

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
                .body("message", equalTo("Book with ISBN 3959713983 already exists"))
                .body("path", equalTo("/books"))
                .body("code", equalTo("BOOK_ALREADY_EXISTS"));
    }

    @Test
    void updateBook_successful() {
        JSONObject requestParams = buildRequestBody();
        String newTitle = "Das inoffizielle Harry-Potter-Kochbuch-Part-2";
        requestParams.replace("title", newTitle);
        requestParams.replace("isbn", "3959713983");
        requestParams.put("subtitle", "Some subtitle");

        Book updatedBook = given()
                .log()
                .all()
                .pathParam("isbn", "3959713983")
                .body(requestParams.toJSONString())
                .contentType(ContentType.JSON)
                .when().put(BASE_PATH + "/{isbn}")
                .then().statusCode(OK.value())
                .extract()
                .as(Book.class);

        assertThat(updatedBook.getIsbn().equals(requestParams.get("isbn")));
        assertThat(updatedBook.getTitle().equals(requestParams.get("title")));
        assertThat(updatedBook.getAuthors().equals(requestParams.get("authors")));
        assertThat(updatedBook.getPublisher().equals(requestParams.get("publisher")));
        assertThat(updatedBook.getPublicationYear().equals(requestParams.get("publicationYear")));
        assertThat(updatedBook.getNumberOfPages().equals(requestParams.get("numberOfPages")));
        assertThat(updatedBook.getPrice().equals(requestParams.get("price")));
        assertThat(updatedBook.getSubtitle().equals("Some subtitle"));
        assertThat(updatedBook.getDescription() == null);
        assertThat(updatedBook.getImageUrl() == null);
    }

    @Test
    void updateBook_InvalidPathParam_throwsError() {
        JSONObject requestParams = buildRequestBody();
        given()
                .log()
                .all()
                .pathParam("isbn", "11111")
                .body(requestParams.toJSONString())
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .when().put(BASE_PATH + "/{isbn}")
                .then().statusCode(BAD_REQUEST.value())
                .body("$", hasKey("timestamp"))
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("updateBook.isbn: ISBN is not valid"))
                .body("path", equalTo("/books/11111"));
    }

    @Test
    void updateBook_bookNotFoundException_throwsError() {
        String modifiedIsbn = "1234567893";
        JSONObject requestParams = buildRequestBody();
        requestParams.replace("isbn", modifiedIsbn);
        given()
                .log()
                .all()
                .pathParam("isbn", modifiedIsbn)
                .body(requestParams.toJSONString())
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .when().put(BASE_PATH + "/{isbn}")
                .then().statusCode(NOT_FOUND.value())
                .body("$", hasKey("timestamp"))
                .body("status", equalTo(404))
                .body("error", equalTo("Not Found"))
                .body("message", equalTo("Book with ISBN " + modifiedIsbn + " not found"))
                .body("path", equalTo("/books/" + modifiedIsbn))
                .body("code", equalTo("BOOK_NOT_FOUND"));
    }

    @Test
    void updateBook_IsbnNotMatching_throwsError() {
        String modifiedIsbn = "1234567893";
        JSONObject requestParams = buildRequestBody();
        requestParams.replace("isbn", "1234567894");
        given()
                .log()
                .all()
                .pathParam("isbn", modifiedIsbn)
                .body(requestParams.toJSONString())
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .when().put(BASE_PATH + "/{isbn}")
                .then().statusCode(BAD_REQUEST.value())
                .body("$", hasKey("timestamp"))
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("ISBN not matching"))
                .body("path", equalTo("/books/" + modifiedIsbn))
                .body("$", not(hasKey("code")));
    }


    private JSONObject buildRequestBody() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("isbn", "1234567891");
        requestParams.put("title", "Professional Java Development with the Spring Framework");
        requestParams.put("authors", "Rod Johnson, Juergen Hoeller, et al.");
        requestParams.put("publisher", "Wrox");
        requestParams.put("publicationYear", 2005);
        requestParams.put("numberOfPages", 676);
        requestParams.put("price", 676);
        return requestParams;
    }


}