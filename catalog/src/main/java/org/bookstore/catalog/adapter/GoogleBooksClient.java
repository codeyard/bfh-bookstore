package org.bookstore.catalog.adapter;

import org.bookstore.catalog.entity.Book;
import org.bookstore.catalog.exception.BookNotFoundException;
import org.bookstore.catalog.util.VolumesConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleBooksClient {

    @Value("${google.books.api-url}")
    private String baseUrl;

    @Value("${google.books.max-results}")
    private String maxResults;

    private final RestTemplate restTemplate = new RestTemplate();

    public Book listVolume(String isbn) throws BookNotFoundException {
        Volumes volumes = restTemplate.getForObject(baseUrl + "isbn:" + isbn, Volumes.class);
        Book book;
        if (volumes != null && volumes.totalItems() > 0) {
            book = VolumesConverter.convertToBook(volumes.items().get(0));
            return book;
        }
        throw new BookNotFoundException();
    }

    public List<Book> listVolumes(String searchTerms) {
        Volumes volumes = restTemplate.getForObject(baseUrl + searchTerms + "&maxResults=" + maxResults, Volumes.class);
        if (volumes == null) {
            return new ArrayList<>();
        }
        return VolumesConverter.convertToBookList(volumes);
    }
}
