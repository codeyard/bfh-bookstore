package ch.rgis.bookcatalog.repository;

import ch.rgis.bookcatalog.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, String> {

    Book findBookByIsbn(String isbn);
}
