package ch.rgis.bookorders.order.entity;

import ch.rgis.bookorders.customer.entity.Address;
import ch.rgis.bookorders.customer.entity.Customer;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_order_seq")
    @SequenceGenerator(name = "book_order_seq", sequenceName = "book_order_seq")
    private Long id;

    @Column(name = "order_date")
    private LocalDateTime date;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Embedded
    private Address address;

    /**
     * CascadeType:
     * The Cascading was made because of the Composition defined in the uml diagram.
     * Since the payment Entity only needs to be modified when the Order is created or deleted, there is
     * no other reason to handle its state in the same operations as its owning side.
     */
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Payment payment;

    @ManyToOne()
    private Customer customer;

    /**
     * List:
     * Used to be a set. List was suggested according to the API documentation provided!
     * <p>
     * Cascade:
     * The Cascading was made because of the Composition defined in the uml diagram.
     * The Cascading type was set to all as it is important that the inverse entity is always up-to-date.
     * <p>
     * OrphanRemoval:
     * If an order item is removed from the relationship, we want to cascade the remove operation to the order item.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private List<OrderItem> items = new ArrayList<>();


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

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    // </editor-fold>

}
