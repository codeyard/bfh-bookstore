package ch.rgis.bookorders.repository;

import ch.rgis.bookorders.dto.CustomerOrderStatistics;
import ch.rgis.bookorders.dto.OrderInfoDTO;
import ch.rgis.bookorders.entity.Book;
import ch.rgis.bookorders.entity.Customer;
import ch.rgis.bookorders.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
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
        assertEquals(5, order.get().getOrderItems().size());

        order.get().getOrderItems().stream()
            .filter(orderItem -> orderItem.getBook().getTitle().equalsIgnoreCase("All Tomorrow's Parties"))
            .peek(orderItem -> {
                Book book = orderItem.getBook();
                assertEquals("All Tomorrow's Parties", book.getTitle());
                assertEquals("Gerald Lannin", book.getAuthors());
                assertEquals("Fielding Ashdown", book.getPublisher());
            });
    }

    @Test
    void findById_findNone() {
        Optional<Order> order = orderRepository.findById(100021L);
        assertFalse(order.isPresent());
    }


    // Query 6: Find information about all orders (OrderInfo) of a certain customer in a certain period (date from/to)
    @Test
    void findOrdersByCustomerAndPeriodWithSpringJpaParameters_findOne() {
        Customer customer = customerRepository.getById(10000L);
        LocalDateTime dateTo = LocalDateTime.now();
        LocalDateTime dateFrom = LocalDateTime.of(
                LocalDate.of(2021, 2, 2),
                LocalTime.of(0, 0));

        List<OrderInfoDTO> orders = orderRepository.findAllByCustomerAndDateGreaterThanEqualAndDateLessThanEqual(
            customer, dateFrom, dateTo
        );
        orders.forEach(order -> System.out.println("ORDER " + order.amount()));

        assertEquals(1, orders.size());
        assertEquals(new BigDecimal("1685.14"), orders.get(0).amount());
    }

    @Test
    void findOrdersByCustomerAndPeriod_findOne() {
        LocalDateTime dateTo = LocalDateTime.now();
        LocalDateTime dateFrom = LocalDateTime.of(
                LocalDate.of(2021, 2, 2),
                LocalTime.of(0, 0));

        List<OrderInfoDTO> orders = orderRepository.findOrdersByCustomerAndPeriod(10000L, dateFrom, dateTo);
        orders.forEach(order -> System.out.println(order.amount()));

        assertEquals(1, orders.size());
        assertEquals(new BigDecimal("1685.14"), orders.get(0).amount());
    }

    // Query 7: Get Order Statistics with total amount, number of positions and average order amount of all orders grouped by year and customer
    /**
     * All Customers have an Order in Test Database
     */
    @Test
    void testStatistics_foundAll() {
        List<CustomerOrderStatistics> allCustomerOrderStatistics = orderRepository.getAllCustomerOrderStatistics();
        List<Customer> allCustomer = customerRepository.findAll();

        assertEquals(allCustomer.size(), allCustomerOrderStatistics.size());

    }

    @Test
    void testStatistics_foundOne() {
        List<CustomerOrderStatistics> allCustomerOrderStatistics = orderRepository.getAllCustomerOrderStatistics();

        List<CustomerOrderStatistics> cluney_angil = allCustomerOrderStatistics.stream()
                .filter(customerOrderStatistics -> customerOrderStatistics.getCustomerId() == 10006L)
                .peek(customerOrderStatistics -> {
                    assertEquals("Cluney Angil", customerOrderStatistics.getCustomerName());
                    assertEquals(5, customerOrderStatistics.getOrderItemsCount());
                    assertEquals(341.408, customerOrderStatistics.getAverageOrderValue());
                    assertEquals(1707.04, customerOrderStatistics.getTotalAmount());
                    assertEquals(2021, customerOrderStatistics.getYear());
                })
                .collect(Collectors.toList());

        assertEquals(1, cluney_angil.size());
    }

    @Test
    void testStatistics_foundCustomerWithMultipleOrdersInDifferentYears() {
        List<CustomerOrderStatistics> allCustomerOrderStatistics = orderRepository.getAllCustomerOrderStatistics();
        Map<Long, Long> mappedCount = allCustomerOrderStatistics
                .stream().collect(groupingBy(CustomerOrderStatistics::getCustomerId, counting()));

        List<Long> customersWithMultipleOrder = mappedCount.entrySet().stream()
                .filter(x -> x.getValue() > 1L)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        assertTrue(customersWithMultipleOrder.size() > 0);

        long count = allCustomerOrderStatistics.stream()
                .filter(customerOrderStatistics ->
                        Objects.equals(customerOrderStatistics.getCustomerId(), customersWithMultipleOrder.get(0)))
                .map(CustomerOrderStatistics::getYear)
                .distinct().count();

        assertTrue(count > 1);
    }




}
