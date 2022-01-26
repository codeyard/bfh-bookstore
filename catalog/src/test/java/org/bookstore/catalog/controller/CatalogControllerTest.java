package org.bookstore.catalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bookstore.catalog.entity.Book;
import org.bookstore.catalog.exception.BookAlreadyExistsException;
import org.bookstore.catalog.exception.BookNotFoundException;
import org.bookstore.catalog.service.CatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CatalogControllerTest {

    private static final String BASE_PATH = "/books";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CatalogService catalogService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    class withProvidedBook {
        @BeforeEach
        public void configureMockBean() throws BookNotFoundException, BookAlreadyExistsException {
            Book book = createBook();
            Mockito.when(catalogService.addBook(any())).thenReturn(book);
            Mockito.when(catalogService.findBook(any())).thenReturn(book);
            Mockito.when(catalogService.searchBooks(any())).thenReturn(singletonList(book));
            Mockito.when(catalogService.updateBook(any())).thenReturn(book);
        }

        @Test
        void getBook_bookFound() throws Exception {
            mockMvc.perform(get(BASE_PATH + "/395440866X"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("title").value("Flower Power Letterings"));
        }

        @Test
        void getBook_invalidIsbn() throws Exception {
            mockMvc.perform(get(BASE_PATH + "/12345"))
                .andExpect(status().isBadRequest());
        }

        @Test
        void findBooks_foundOne() throws Exception {
            mockMvc.perform(get(BASE_PATH + "?keywords=flower power"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$..title").value("Flower Power Letterings"));
        }
    }

    @Test
    void getBook_bookNotFound() throws Exception {
        Mockito.when(catalogService.findBook(any())).thenThrow(new BookNotFoundException("Book not found"));
        mockMvc.perform(get(BASE_PATH + "/1111111111"))
            .andExpect(status().isNotFound());
    }

    @Test
    void findBooks_noneFound() throws Exception {
        Mockito.when(catalogService.searchBooks(any())).thenReturn(new ArrayList<>());
        mockMvc.perform(get(BASE_PATH + "?keywords=asdfasdfadsfasfd"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void addBook_bookAdded() throws Exception {
        String newIsbn = "1234567890";
        Book book = createBook();
        book.setIsbn(newIsbn);
        Mockito.when(catalogService.addBook(any())).thenReturn(book);
        mockMvc.perform(post(BASE_PATH).contentType(APPLICATION_JSON).content(asJson(book)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("isbn").value(newIsbn))
            .andExpect(jsonPath("title").value("Flower Power Letterings"));
    }

    private Book createBook() {
        Book book = new Book();
        book.setIsbn("395440866X");
        book.setTitle("Flower Power Letterings");
        book.setAuthors("Patrycja Woltman");
        book.setPublisher("Christophorus Verlag");
        book.setPublicationYear(2020);
        book.setNumberOfPages(96);

        return book;
    }

    private String asJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }
}
