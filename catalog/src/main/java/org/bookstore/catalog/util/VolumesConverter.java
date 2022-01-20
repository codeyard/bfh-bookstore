package org.bookstore.catalog.util;

import org.bookstore.catalog.adapter.Volume;
import org.bookstore.catalog.adapter.Volumes;
import org.bookstore.catalog.entity.Book;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VolumesConverter {

    public static Book convertToBook(Volume volume) {
        try {
            if (!volume.saleInfo().saleability().equals("NOT_FOR_SALE")) {
                String isbn = (volume.volumeInfo().industryIdentifiers() != null) ? volume.volumeInfo().industryIdentifiers().get(0).identifier() : "";
                String title = (volume.volumeInfo().title() != null) ? volume.volumeInfo().title() : "";
                String authors = (volume.volumeInfo().authors().size() > 0) ? String.join(",", volume.volumeInfo().authors()) : "";
                String publisher = (volume.volumeInfo().publisher() != null) ? volume.volumeInfo().publisher() : "";
                BigDecimal price = (volume.saleInfo().listPrice().amount() != null) ? volume.saleInfo().listPrice().amount() : null;
                if (!isbn.isEmpty() && !title.isEmpty() && !authors.isEmpty() && !publisher.isEmpty() && price != null) {
                    Book book = new Book();
                    book.setIsbn(isbn);
                    book.setTitle(title);
                    book.setSubtitle(volume.volumeInfo().subTitle());
                    book.setAuthors(authors);
                    book.setPublisher(publisher);
                    if (volume.volumeInfo().publishedDate() != null) {
                        LocalDate publicationDate = LocalDate.parse(volume.volumeInfo().publishedDate());
                        book.setPublicationYear(publicationDate.getYear());
                    }
                    book.setNumberOfPages(volume.volumeInfo().pageCount());
                    book.setDescription(volume.volumeInfo().description());
                    book.setImageUrl(volume.volumeInfo().imageLinks().thumbnail());
                    book.setPrice(price);
                    return book;
                } else {
                    return null;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Book> convertToBookList(Volumes volumes) {
        List<Book> bookList = new ArrayList<>();
        if (volumes.totalItems() > 0) {
            for (Volume volume : volumes.items()) {
                Book book = convertToBook(volume);
                if (book != null) {
                    bookList.add(book);
                }
            }
        }
        return bookList;
    }


}
