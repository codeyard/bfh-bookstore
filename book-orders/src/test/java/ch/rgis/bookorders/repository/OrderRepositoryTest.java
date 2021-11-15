package ch.rgis.bookorders.repository;

import ch.rgis.bookorders.entity.Book;
import ch.rgis.bookorders.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    EntityManager entityManager;

    // Query 5: Find the Order by a specific number
    @Test
    void findById_findOne() {
        Optional<Order> order = orderRepository.findById(100000L);
        assertTrue(order.isPresent());
        assertEquals(1, order.get().getOrderItems().size());

        order.get().getOrderItems().stream()
            .filter(orderItem -> orderItem.getBook().getTitle().equalsIgnoreCase("shining"))
            .peek(orderItem -> {
                Book book = orderItem.getBook();
                assertEquals("Shining", book.getTitle());
                assertEquals("Stephen King", book.getAuthors());
                assertEquals("Bastei", book.getPublisher());
            });
    }

    @Test
    void findById_findNone() {
        Optional<Order> order = orderRepository.findById(100001L);
        assertFalse(order.isPresent());
    }
}
