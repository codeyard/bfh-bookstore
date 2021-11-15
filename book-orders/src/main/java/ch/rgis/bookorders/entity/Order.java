package ch.rgis.bookorders.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "book_order")
public class Order {

    @Id
    private Long id;

    @Column(name = "order_date")
    private LocalDateTime date;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    //IS LIST CORRECT? WHY????? SHOULD WE USE SET / MAP =>
    // IT CAN'T BE SET AS YOU CAN ORDER AS YOU SHOULD BE ABLE TO
    @OneToMany(mappedBy = "order")
    private Set<OrderItem> orderItems;

    @Embedded
    private Address address;


    // <editor-fold desc="Getter and Setter">

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        //TODO Count quantity up;
        this.orderItems = orderItems;
    }

    // </editor-fold>


}
