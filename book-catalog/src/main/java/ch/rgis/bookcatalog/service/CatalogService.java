package ch.rgis.bookcatalog.service;

import ch.rgis.bookcatalog.entity.Book;
import ch.rgis.bookcatalog.exception.BookAlreadyExistsException;
import ch.rgis.bookcatalog.exception.BookNotFoundException;
import ch.rgis.bookcatalog.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CatalogService {

    private final BookRepository bookRepository;

    @Autowired
    public CatalogService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book addBook(Book book) throws BookAlreadyExistsException {
        boolean bookExists = bookRepository.existsByIsbn(book.getIsbn());
        if (!bookExists) {
            return bookRepository.saveAndFlush(book);
        } else {
            throw new BookAlreadyExistsException();
        }
    }

    public Book findBook(String isbn) throws BookNotFoundException {
        Optional<Book> bookByIsbn = bookRepository.findBookByIsbn(isbn);
        return bookByIsbn.orElseThrow(BookNotFoundException::new);
    }

    public List<Book> searchBooks(String keywords) {
        return bookRepository.findBooksByKeywords(keywords.split(" "));
    }

    public Book updateBook(Book book) throws BookNotFoundException {
        boolean bookExists = bookRepository.existsByIsbn(book.getIsbn());
        if (bookExists) {
            return bookRepository.saveAndFlush(book);
        } else {
            throw new BookNotFoundException();
        }
    }
}
