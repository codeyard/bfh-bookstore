package ch.rgis.bookcatalog.repository;

import ch.rgis.bookcatalog.entity.Book;

import java.util.List;

public interface CustomBookRepository {

    List<Book> findBooksByKeywords(String... keywords);
}
