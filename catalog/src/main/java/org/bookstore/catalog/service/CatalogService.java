package org.bookstore.catalog.service;

import org.bookstore.catalog.adapter.GoogleBooksClient;
import org.bookstore.catalog.entity.Book;
import org.bookstore.catalog.exception.BookAlreadyExistsException;
import org.bookstore.catalog.exception.BookNotFoundException;
import org.bookstore.catalog.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * The interface CatalogService defines a service to manage the books of a bookstore.
 *
 * @author Igor Stojanovic, Raphael Gerber
 */
@Service
public class CatalogService {

    private final BookRepository bookRepository;

    private final GoogleBooksClient googleBooksClient;

    public CatalogService(BookRepository bookRepository, GoogleBooksClient googleBooksClient) {
        this.bookRepository = bookRepository;
        this.googleBooksClient = googleBooksClient;
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
            throw new BookAlreadyExistsException(book.getIsbn());
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
        Optional<Book> bookFromRepository = bookRepository.findBookByIsbn(isbn);
        if (bookFromRepository.isPresent())
            return bookFromRepository.get();

        Optional<Book> bookFromGoogle = googleBooksClient.listVolume(isbn);
        if (bookFromGoogle.isPresent()) {
            bookRepository.saveAndFlush(bookFromGoogle.get());
            return bookFromGoogle.get();
        }

        throw new BookNotFoundException(isbn);
    }

    /**
     * Searches for books in the catalog.
     * A book is included in the result list if every keyword is contained in the title, authors or publisher field.
     *
     * @param keywords - the (space separated) keywords to search for
     * @return the list of matching books (may be empty)
     */
    public List<Book> searchBooks(String keywords) {
        Map<String, Book> combinedBooks = new HashMap<>();
        List<Book> booksFromRepository = bookRepository.findBooksByKeywords(keywords.split(" "));

        booksFromRepository.forEach(book -> combinedBooks.put(book.getIsbn(), book));

        Optional<List<Book>> booksFromGoogle = googleBooksClient.listVolumes(keywords);
        booksFromGoogle.ifPresent(googleBooks -> googleBooks.forEach(googleBook -> combinedBooks.put(googleBook.getIsbn(), googleBook)));

        return new ArrayList<>(combinedBooks.values());

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
            throw new BookNotFoundException(book.getIsbn());
        }
    }
}
