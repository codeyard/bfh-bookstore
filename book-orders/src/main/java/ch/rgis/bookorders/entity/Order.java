package ch.rgis.bookorders.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "book_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_order_seq")
    @SequenceGenerator(name = "book_order_seq", sequenceName = "book_order_seq", initialValue = 100_000, allocationSize = 50)
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
     * Set:
     * We chose to have a set!
     * The reason for this is that we are convinced that sorting here does not provide any benefit for the user.
     * Rather it could lead to confusion if the sorting might not reflect the users expected behavior.
     * A Set ensures that the added OrderItems are unique and duplicates are not possible.
     *
     * Cascade:
     * The Cascading was made because of the Composition defined in the uml diagram.
     * The Cascading type was set to all as it is important that the inverse entity is always up-to-date.
     *
     * OrphanRemoval:
     * If an order item is removed from the relationship, we want to cascade the remove operation to the order item.
     *
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private Set<OrderItem> orderItems = new HashSet<>();


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
