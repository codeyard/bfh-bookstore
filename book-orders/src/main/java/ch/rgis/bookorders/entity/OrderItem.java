package ch.rgis.bookorders.entity;

import javax.persistence.*;

@Entity
public class OrderItem {

    @Id
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @Embedded
    private Book book;

    @ManyToOne(optional = false)
    private Order order;


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


}
