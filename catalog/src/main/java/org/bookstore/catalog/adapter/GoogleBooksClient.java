package org.bookstore.catalog.adapter;

import org.bookstore.catalog.entity.Book;
import org.bookstore.catalog.exception.BookNotFoundException;
import org.bookstore.catalog.util.VolumesConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Component
public class GoogleBooksClient {

    @Value("${google.books.api-url}")
    private String baseUrl;

    @Value("${google.books.max-results}")
    private String maxResults;

    private final RestTemplate restTemplate = new RestTemplate();

    public Optional<Book> listVolume(String isbn) throws BookNotFoundException {
        Volumes volumes = restTemplate.getForObject(baseUrl + "isbn:" + isbn, Volumes.class);
        Optional<Book> book;
        if (volumes != null && volumes.totalItems() > 0) {
            book = VolumesConverter.convertToBook(volumes.items().get(0));
            return book;
        }
        throw new BookNotFoundException(isbn);
    }

    public Optional<List<Book>> listVolumes(String searchTerms) {
        Volumes volumes = restTemplate.getForObject(baseUrl + searchTerms + "&maxResults=" + maxResults, Volumes.class);
        if (volumes == null) {
            return Optional.empty();
        }
        return VolumesConverter.convertToBookList(volumes);
    }
}
