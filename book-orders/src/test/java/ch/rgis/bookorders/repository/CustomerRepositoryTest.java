package ch.rgis.bookorders.repository;

import ch.rgis.bookorders.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testIt() {
        List<Customer> all = customerRepository.findAll();
        assertEquals(1, all.size());

        Customer igor = all.get(0);

        customerRepository.deleteById(igor.getId());

        List<Customer> all2 = customerRepository.findAll();

        assertEquals(0, all2.size());

    }

    @Test
    void differentBooks() {

    }

    @Test
    void hasOneItem() {
        Book book = new Book();
        book.setTitle("Test");
        book.setAuthors("Bla");
        book.setIsbn("122020");
        book.setPublisher("Publisher");
        book.setPrice(new BigDecimal("20.0"));
        OrderItem item1 = new OrderItem();
        item1.setQuantity(2);
        item1.setBook(book);

        Order order = new Order();
        order.setDate(LocalDateTime.now());
        order.setAmount(new BigDecimal("20.0"));
        order.setStatus(OrderStatus.ACCEPTED);

        List<Customer> all = customerRepository.findAll();
        Customer igor = all.get(0);
        order.setAddress(igor.getAddress());
        order.setCustomer(igor);

        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("20.0"));
        payment.setDate(LocalDateTime.now());
        payment.setCreditCardNumber(igor.getCreditCard().getNumber());
        payment.setTransactionId("1");
        order.setPayment(payment);

        Set<OrderItem> orderItemSet = new HashSet<>();
        orderItemSet.add(item1);

        order.setOrderItems(orderItemSet);

        orderRepository.saveAndFlush(order);

    }

    @Test
    void bookTest() {

    }

}