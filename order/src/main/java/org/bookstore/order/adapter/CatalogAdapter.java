package org.bookstore.order.adapter;

import org.bookstore.order.entity.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CatalogAdapter {

    private final RestTemplate restTemplate;

    @Value("${bookstore.catalog.api-url}")
    private String catalogApiUrl;

    public CatalogAdapter(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
    }

    public Book findBook(String isbn) {
        return restTemplate.getForObject(catalogApiUrl + "/" + isbn, Book.class);
    }


}
