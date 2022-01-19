package org.bookstore.catalog.adapter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bookstore.catalog.entity.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class GoogleBooksClient {

    @Value("${google.books.api-url}")
    private String baseUrl;

    @Value("${google.books.max-results}")
    private Integer maxResults;

    private final RestTemplate restTemplate;

    public GoogleBooksClient(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public Book listVolume(String isbn) {
        Volumes volume = restTemplate.getForObject(baseUrl + "?q=isbn:" + isbn, Volumes.class);
        return objectMapper(volume).get(0);
    }

    public List<Book> listVolumes(String searchTerms) {
        Volumes volumes = restTemplate.getForObject(baseUrl + "?q=" + searchTerms + "&maxResults=" + maxResults, Volumes.class);
        return objectMapper(volumes);
    }

    private List<Book> objectMapper(Volumes volumes) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        List<Book> books = Arrays.asList(mapper.convertValue(volumes, Book[].class));
        return books;
    }
}
