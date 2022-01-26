package org.bookstore.catalog.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CatalogControllerIT {

    @LocalServerPort
    int port;
    @BeforeEach
    public void init() { RestAssured.port = port; }


    @Test
    void getBook() {
    }

    @Test
    void findBooks() {
    }

    @Test
    void addBook() {
    }

    @Test
    void updateBook() {
    }
}