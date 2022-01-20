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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@RestClientTest(GoogleBooksClient.class)
class GoogleBooksClientIT {

    @Autowired
    private GoogleBooksClient googleBooksClient;

    @Test
    void listVolume() throws BookNotFoundException {
        Optional<Book> book = googleBooksClient.listVolume("9783954408665");

        book.ifPresent(book1 -> {
            assertEquals("Flower Power Letterings", book1.getTitle());
            assertEquals(2020, book1.getPublicationYear());
            assertEquals("Patrycja Woltman", book1.getAuthors());
            assertEquals(new BigDecimal("6.50").stripTrailingZeros(), book1.getPrice().stripTrailingZeros());
        });

    }

    @Test
    void listVolumes() {
        Optional<List<Book>> bookList = googleBooksClient.listVolumes("spring boot");

        bookList.ifPresent(books -> {
            assertTrue(books.size() > 0);
            books.forEach(book -> {
                assertTrue(book.getTitle() != null);
                assertTrue(book.getIsbn() != null);
                assertTrue(book.getAuthors() != null);
                assertTrue(book.getPrice() != null);
                assertTrue(book.getPublisher() != null);
            });
        });

    }
}
