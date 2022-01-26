package org.bookstore.catalog.controller;

import org.bookstore.catalog.entity.Book;
import org.bookstore.catalog.exception.BookAlreadyExistsException;
import org.bookstore.catalog.exception.BookNotFoundException;
import org.bookstore.catalog.exception.IsbnNotMatchingException;
import org.bookstore.catalog.service.CatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/books")
@Validated
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping(value = "/{isbn}", produces = "application/json")
    public Book getBook(
            @PathVariable
            @Pattern(regexp="^[0-9]{10}", message="length must be 10")  String isbn) throws BookNotFoundException {
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
        if(!book.getIsbn().equals(isbn))
            throw new IsbnNotMatchingException();
        return catalogService.updateBook(book);
    }


}
