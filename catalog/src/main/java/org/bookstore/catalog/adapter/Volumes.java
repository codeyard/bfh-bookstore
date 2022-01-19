package org.bookstore.catalog.adapter;

import java.util.List;

public record Volumes(
    String kind,
    Integer totalItems,
    List<Volume> items
) {
}
