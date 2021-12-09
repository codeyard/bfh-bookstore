package ch.rgis.bookorders.order.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_seq")
    @SequenceGenerator(name = "order_item_seq", sequenceName = "order_item_seq")
    private Long id;

    private Integer quantity;

    @Embedded
    private Book book;


    // <editor-fold desc="Getter and Setter">

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    // </editor-fold>


    /**
     * An OrderItem is compared on a book level!
     * It cannot hold the same book twice with different quantities
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return book.equals(orderItem.book);
    }

    @Override
    public int hashCode() {
        return Objects.hash(book);
    }
}
