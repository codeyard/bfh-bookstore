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

    @GetMapping("/{isbn}")
    public Book getBook(@PathVariable String isbn) throws BookNotFoundException {
        return catalogService.findBook(isbn);
    }

    @GetMapping(params = "keywords")
    public List<Book> findBooks(@RequestParam String keywords) {
        return catalogService.searchBooks(keywords);
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBook(@RequestBody Book book) throws BookAlreadyExistsException {
        catalogService.addBook(book);
    }

    @PutMapping("/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBook(@RequestBody Book book) throws BookNotFoundException {
        catalogService.updateBook(book);
    }


}
