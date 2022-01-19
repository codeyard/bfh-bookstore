package org.bookstore.catalog.adapter;

import org.bookstore.catalog.entity.Book;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@RestClientTest(GoogleBooksClient.class)
class GoogleBooksClientIT {

    @Autowired
    private GoogleBooksClient googleBooksClient;

    @Test
    void listVolume() {
        Book book = googleBooksClient.listVolume("9783954408665");
        System.out.println(book.getTitle());
    }

    @Test
    void listVolumes() {
    }
}
