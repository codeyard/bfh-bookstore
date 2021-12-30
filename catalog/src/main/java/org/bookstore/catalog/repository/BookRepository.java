package org.bookstore.catalog.repository;

import org.bookstore.catalog.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, String>, CustomBookRepository {

    Optional<Book> findBookByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

}
