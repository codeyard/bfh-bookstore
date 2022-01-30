package org.bookstore.catalog;

import org.bookstore.catalog.entity.Book;
import org.bookstore.catalog.exception.BookAlreadyExistsException;
import org.bookstore.catalog.exception.BookNotFoundException;
import org.bookstore.catalog.repository.BookRepository;
import org.bookstore.catalog.service.CatalogService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;


@SpringBootTest
public class CatalogServiceIT {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CatalogService catalogService;


    @Test
    void updateBook_succeeded() throws BookNotFoundException {
        Optional<Book> bookByIsbn = bookRepository.findBookByIsbn("978-3-404-13089-4");
        if (bookByIsbn.isPresent()) {
            Assertions.assertEquals("Shining", bookByIsbn.get().getTitle());
            bookByIsbn.get().setTitle("The Shining");
            Book updatedBook = catalogService.updateBook(bookByIsbn.get());
            Assertions.assertEquals("The Shining", updatedBook.getTitle());
        }
    }

    @Test
    void updateBook_throwsBookNotFoundException() {
        Optional<Book> bookByIsbn = bookRepository.findBookByIsbn("978-3-404-13089-4");
        if (bookByIsbn.isPresent()) {
            bookByIsbn.get().setIsbn("978-3-404-1111-4");
            Assertions.assertThrows(BookNotFoundException.class, () -> catalogService.updateBook(bookByIsbn.get()));
        }
    }

    @Test
    void searchBooks_succeeded() {
        List<Book> bookList = catalogService.searchBooks("shining king bastei");
        Assertions.assertTrue(bookList.size() > 0);
    }

    @Test
    void searchBooks_returnsEmptyArray() {
        List<Book> bookList = catalogService.searchBooks("ascasciasiscasacih");
        Assertions.assertEquals(0, bookList.size());
    }

    @Test
    void findBook_bookFound() throws BookNotFoundException {
        Book book = catalogService.findBook("978-3-404-13089-4");
        Assertions.assertEquals("Shining", book.getTitle());
    }

    @Test
    void findBook_throwsBookNotFoundException() {
        Assertions.assertThrows(BookNotFoundException.class, () -> catalogService.findBook("978-3-404-13089-1"));
    }

    @Test
    void addBook_bookAdded() throws BookAlreadyExistsException, BookNotFoundException {
        Book book = createBook();
        book.setIsbn("978-3-404-13000-9");
        catalogService.addBook(book);
        Book savedBook = catalogService.findBook(book.getIsbn());
        Assertions.assertEquals("The Mining", savedBook.getTitle());
    }

    @Test
    void addBook_throwsBookAlreadyExistsException() throws BookAlreadyExistsException {
        Book book = createBook();
        book.setIsbn("978-3-404-13000-1");
        catalogService.addBook(book);
        Book book2 = createBook();
        book2.setIsbn("978-3-404-13000-1");
        Assertions.assertThrows(BookAlreadyExistsException.class, () -> catalogService.addBook(book2));
    }


    @Test
    void addBook_throwsBookAlreadyExistsExceptionBecauseOfDuplicatedISBN() {
        Book book = createBook();
        book.setIsbn("978-3-404-13089-4");
        Assertions.assertThrows(BookAlreadyExistsException.class, () -> catalogService.addBook(book));
    }

    @Test
    void findBook_savesBookFromGoogle_successful() {
        String isbn = "3423438657";

        Assertions.assertDoesNotThrow(() -> catalogService.findBook(isbn));
        Assertions.assertTrue(bookRepository.existsByIsbn(isbn));
        Optional<Book> bookByIsbn = bookRepository.findBookByIsbn(isbn);
        Assertions.assertTrue(bookByIsbn.isPresent());
        bookByIsbn.ifPresent(book1 -> {
            Assertions.assertEquals("Ein anderes Land", book1.getTitle());
            Assertions.assertEquals("James Baldwin", book1.getAuthors());
            Assertions.assertEquals("Deutscher Taschenbuch Verlag", book1.getPublisher());
            Assertions.assertEquals(new BigDecimal("22.00"), book1.getPrice());
        });
    }

    @Test
    void searchBooks_DeliversNoDuplicates_successful() {
        List<Book> harry_potter = catalogService.searchBooks("harry potter");

        Map<String, Long> collect = harry_potter.stream().collect(Collectors.groupingBy(Book::getIsbn, Collectors.counting()));
        Assertions.assertEquals(1L, (long) collect.get("3959713983"));
    }

    private Book createBook() {
        Book book = new Book();
        book.setTitle("The Mining");
        book.setSubtitle("How to get the Wäutherschaft");
        book.setAuthors("Raphael Gerber, Igor Stojanovic");
        book.setDescription("The most amazing book ever");
        book.setPrice(new BigDecimal("222.2"));
        book.setPublisher("Anonymous");
        book.setPublicationYear(2021);
        book.setNumberOfPages(2);
        book.setImageUrl("https://www.amazingdevs.com/theMining");
        return book;
    }


}
