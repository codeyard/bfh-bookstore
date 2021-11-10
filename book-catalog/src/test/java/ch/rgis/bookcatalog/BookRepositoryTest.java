package ch.rgis.bookcatalog;

import ch.rgis.bookcatalog.entity.Book;
import ch.rgis.bookcatalog.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;

@SpringBootTest
public class BookRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    BookRepository bookRepository;

    @Test
    void findByIsbn() {
        Book book = bookRepository.findBookByIsbn("978-3-404-13089-4");
        assertEquals("Shining", book.getTitle());
    }
}
