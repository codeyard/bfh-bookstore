package ch.rgis.bookorders.repository;

import ch.rgis.bookorders.dto.OrderInfoDTO;
import ch.rgis.bookorders.entity.Book;
import ch.rgis.bookorders.entity.Customer;
import ch.rgis.bookorders.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    CustomerRepository customerRepository;

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


    // Query 6: Find information about all orders (OrderInfo) of a certain customer in a certain period (date from/to)
    @Test
    void findOrdersByCustomerAndPeriodWithSpringJpaParameters_findOne() {
        Customer customer = customerRepository.getById(10000L);
        LocalDateTime dateTo = LocalDateTime.now();
        LocalDateTime dateFrom = LocalDateTime.now().minusDays(10);

        List<OrderInfoDTO> orders = orderRepository.findAllByCustomerAndDateGreaterThanEqualAndDateLessThanEqual(
            customer, dateFrom, dateTo
        );
        orders.forEach(order -> System.out.println(order.amount()));

        assertEquals(1, orders.size());
        assertEquals(new BigDecimal("77.85"), orders.get(0).amount());
    }

    @Test
    void findOrdersByCustomerAndPeriod_findOne() {
        LocalDateTime dateTo = LocalDateTime.now();
        LocalDateTime dateFrom = LocalDateTime.now().minusDays(10);

        List<OrderInfoDTO> orders = orderRepository.findOrdersByCustomerAndPeriod(10000L, dateFrom, dateTo);
        orders.forEach(order -> System.out.println(order.amount()));

        assertEquals(1, orders.size());
        assertEquals(new BigDecimal("77.85"), orders.get(0).amount());
    }
}
