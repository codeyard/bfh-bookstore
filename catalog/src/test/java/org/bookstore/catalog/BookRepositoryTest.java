package org.bookstore.catalog;

import org.bookstore.catalog.entity.Book;
import org.bookstore.catalog.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    BookRepository bookRepository;


    // Query 3: Find the Book by a specific ISBN-Number
    @Test
    void findByIsbn_foundOne() {
        Optional<Book> book = bookRepository.findBookByIsbn("3404130898");
        assertTrue(book.isPresent());
        assertEquals("Shining", book.get().getTitle());
    }

    @Test
    void findByIsbn_foundNone() {
        Optional<Book> book = bookRepository.findBookByIsbn("1111111111");
        assertFalse(book.isPresent());
    }


    // Query 4: Find all Books that contain all given keywords, whether in title, authors or publisher
    @Test
    void findAllBooksByKeywords_foundOne() {
        List<Book> bookList = bookRepository.findBooksByKeywords("shining", "king", "bastei");
        assertEquals(1, bookList.size());
    }

    @Test
    void findAllBooksByKeywords_foundNone() {
        List<Book> bookList = bookRepository.findBooksByKeywords("mining", "king", "bastei");
        assertEquals(0, bookList.size());
    }

    @Test
    void bookExists_FoundOne() {
        boolean existsByIsbn = bookRepository.existsByIsbn("3404130898");
        assertTrue(existsByIsbn);
    }

    @Test
    void bookExists_FoundNone() {
        boolean existsByIsbn = bookRepository.existsByIsbn("1111111111");
        assertFalse(existsByIsbn);
    }
}
