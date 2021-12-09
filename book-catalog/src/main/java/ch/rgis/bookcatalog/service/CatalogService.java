package ch.rgis.bookcatalog.service;

import ch.rgis.bookcatalog.entity.Book;
import ch.rgis.bookcatalog.exception.BookAlreadyExistsException;
import ch.rgis.bookcatalog.exception.BookNotFoundException;
import ch.rgis.bookcatalog.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The interface CatalogService defines a service to manage the books of a bookstore.
 *
 * @author Igor Stojanovic, Raphael Gerber
 */
@Service
public class CatalogService {

    private final BookRepository bookRepository;

    @Autowired
    public CatalogService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Adds a book to the catalog.
     *
     * @param book - the data of the book to be added
     * @return the data of the added book
     * @throws BookAlreadyExistsException - if a book with the same ISBN already exists
     */
    public Book addBook(Book book) throws BookAlreadyExistsException {
        boolean bookExists = bookRepository.existsByIsbn(book.getIsbn());
        if (!bookExists) {
            return bookRepository.saveAndFlush(book);
        } else {
            throw new BookAlreadyExistsException();
        }
    }

    /**
     * Finds a book in the catalog.
     *
     * @param isbn - the ISBN of the book
     * @return the data of the found book
     * @throws BookNotFoundException - if no book with the specified ISBN exists
     */
    public Book findBook(String isbn) throws BookNotFoundException {
        return bookRepository.findBookByIsbn(isbn).orElseThrow(BookNotFoundException::new);
    }

    /**
     * Searches for books in the catalog.
     * A book is included in the result list if every keyword is contained in the title, authors or publisher field.
     *
     * @param keywords - the (space separated) keywords to search for
     * @return the list of matching books (may be empty)
     */
    public List<Book> searchBooks(String keywords) {
        return bookRepository.findBooksByKeywords(keywords.split(" "));
    }

    /**
     * Updates a book in the catalog.
     *
     * @param book - the new data of the book
     * @return the data of the updated book
     * @throws BookNotFoundException - if no book with the corresponding ISBN exists
     */
    public Book updateBook(Book book) throws BookNotFoundException {
        boolean bookExists = bookRepository.existsByIsbn(book.getIsbn());
        if (bookExists) {
            return bookRepository.saveAndFlush(book);
        } else {
            throw new BookNotFoundException();
        }
    }
}
