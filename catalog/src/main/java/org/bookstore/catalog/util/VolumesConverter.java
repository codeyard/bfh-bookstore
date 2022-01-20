package org.bookstore.catalog.util;

import org.bookstore.catalog.adapter.Volume;
import org.bookstore.catalog.adapter.Volumes;
import org.bookstore.catalog.entity.Book;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VolumesConverter {

    public static Optional<Book> convertToBook(Volume volume) {
        if (isBook(volume)) {
            Book book = new Book();
            book.setIsbn(volume.volumeInfo().industryIdentifiers().get(0).identifier());
            book.setTitle(volume.volumeInfo().title());
            book.setSubtitle(volume.volumeInfo().subTitle());
            book.setAuthors(String.join(",", volume.volumeInfo().authors()));
            book.setPublisher(volume.volumeInfo().publisher());
            book.setPrice(volume.saleInfo().listPrice().amount());
            if (volume.volumeInfo().publishedDate() != null) {
                book.setPublicationYear(LocalDate.parse(volume.volumeInfo().publishedDate()).getYear());
            }
            book.setNumberOfPages(volume.volumeInfo().pageCount());
            book.setDescription(volume.volumeInfo().description());
            book.setImageUrl(volume.volumeInfo().imageLinks().thumbnail());
            return Optional.of(book);
        } else {
            return Optional.empty();
        }
    }

    public static Optional<List<Book>> convertToBookList(Volumes volumes) {
        List<Book> bookList = new ArrayList<>();
        if (volumes.totalItems() > 0) {
            for (Volume volume : volumes.items()) {
                Optional<Book> book = convertToBook(volume);
                book.ifPresent(bookToAd -> bookList.add(bookToAd));
                }
            }
        return Optional.of(bookList);
    }

    private static boolean isBook(Volume volume) {
        return !volume.saleInfo().saleability().equals("NOT_FOR_SALE") &&
                volume.volumeInfo().industryIdentifiers() != null &&
                volume.volumeInfo().title() != null &&
                volume.volumeInfo().authors() != null &&
                volume.volumeInfo().publisher() != null &&
                volume.saleInfo().listPrice().amount() != null;
    }

}
