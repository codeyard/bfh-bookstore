package org.bookstore.catalog.controller;

import org.bookstore.catalog.entity.Book;
import org.bookstore.catalog.exception.BookAlreadyExistsException;
import org.bookstore.catalog.exception.BookNotFoundException;
import org.bookstore.catalog.exception.IsbnNotMatchingException;
import org.bookstore.catalog.service.CatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

@RestController
@RequestMapping("/books")
@Validated
public class CatalogController {

    private final CatalogService catalogService;

    private final String isbnRegex = "^(?:ISBN(?:-10)?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$)[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$";

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping(value = "/{isbn}", produces = "application/json")
    public Book getBook(
        @PathVariable
        @Pattern(regexp = isbnRegex, message = "ISBN is not valid") String isbn) throws BookNotFoundException {
        return catalogService.findBook(isbn);
    }

    @GetMapping(params = "keywords", produces = "application/json")
    public List<Book> findBooks(@RequestParam @NotEmpty String keywords) {
        return catalogService.searchBooks(keywords);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Book addBook(@RequestBody @Valid Book book) throws BookAlreadyExistsException {
        return catalogService.addBook(book);
    }

    @PutMapping(value = "/{isbn}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Book updateBook(@PathVariable String isbn, @RequestBody @Valid Book book) throws BookNotFoundException, IsbnNotMatchingException {
        if (!book.getIsbn().equals(isbn))
            throw new IsbnNotMatchingException();
        return catalogService.updateBook(book);
    }


}
