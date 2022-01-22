package org.bookstore.catalog.controller;

import org.bookstore.catalog.entity.Book;
import org.bookstore.catalog.exception.BookAlreadyExistsException;
import org.bookstore.catalog.exception.BookNotFoundException;
import org.bookstore.catalog.service.CatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping(value = "/{isbn}", produces = "application/json")
    public Book getBook(@PathVariable String isbn) throws BookNotFoundException {
        return catalogService.findBook(isbn);
    }

    @GetMapping(params = "keywords", produces = "application/json")
    public List<Book> findBooks(@RequestParam String keywords) {
        return catalogService.searchBooks(keywords);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Book addBook(@RequestBody Book book) throws BookAlreadyExistsException {
        return catalogService.addBook(book);
    }

    @PutMapping(value = "/{isbn}", produces = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Book updateBook(@RequestBody Book book) throws BookNotFoundException {
        return catalogService.updateBook(book);
    }


}
