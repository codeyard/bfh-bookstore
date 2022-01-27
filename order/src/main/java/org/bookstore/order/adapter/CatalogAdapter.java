package org.bookstore.order.adapter;

import org.bookstore.order.entity.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CatalogAdapter {

    private final RestTemplate restTEmplate;

    @Value("${bookstore.catalog.api-url}")
    private String catalogApiUrl;

    public CatalogAdapter(RestTemplateBuilder restTemplateBuilder) {
        this.restTEmplate = restTemplateBuilder.build();
    }

    public Book findBook(String isbn) {
        return restTEmplate.getForObject(catalogApiUrl + "/" + isbn, Book.class);
    }



}
