package org.bookstore.catalog.adapter;

import java.util.List;

public record VolumeInfo(
    String title,
    String subTitle,
    List<String> authors,
    String publisher,
    String publishedDate,
    String description,
    List<Identifier> industryIdentifiers,
    Integer pageCount,
    ImageLinks imageLinks,
    String language
) {
}
