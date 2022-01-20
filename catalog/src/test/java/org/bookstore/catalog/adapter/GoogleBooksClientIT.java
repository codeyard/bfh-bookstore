package org.bookstore.catalog.adapter;

import org.bookstore.catalog.entity.Book;
import org.bookstore.catalog.exception.BookNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@RestClientTest(GoogleBooksClient.class)
class GoogleBooksClientIT {

    @Autowired
    private GoogleBooksClient googleBooksClient;

    @Test
    void listVolume() throws BookNotFoundException {
        Book book = googleBooksClient.listVolume("9783954408665");
        assertEquals("Flower Power Letterings", book.getTitle());
        assertEquals(2020, book.getPublicationYear());
        assertEquals("Patrycja Woltman", book.getAuthors());
        assertEquals(new BigDecimal("6.50").stripTrailingZeros(), book.getPrice().stripTrailingZeros());
    }

    @Test
    void listVolumes() {
        List<Book> bookList = googleBooksClient.listVolumes("spring boot");
        assertEquals(27, bookList.size());
    }
}
