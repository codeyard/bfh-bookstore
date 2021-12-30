package org.bookstore.catalog.repository;

import org.bookstore.catalog.entity.Book;

import java.util.List;

public interface CustomBookRepository {

    List<Book> findBooksByKeywords(String... keywords);
}
