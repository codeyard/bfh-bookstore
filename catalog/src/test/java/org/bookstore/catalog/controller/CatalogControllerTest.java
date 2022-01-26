package org.bookstore.catalog.controller;

import org.bookstore.catalog.service.CatalogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CatalogService service;

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