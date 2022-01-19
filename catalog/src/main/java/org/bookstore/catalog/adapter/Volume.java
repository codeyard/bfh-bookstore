package org.bookstore.catalog.adapter;

public record Volume(
    String kind,
    String id,
    String etag,
    String selfLink,
    VolumeInfo volumeInfo,
    SaleInfo saleInfo
) {
}
