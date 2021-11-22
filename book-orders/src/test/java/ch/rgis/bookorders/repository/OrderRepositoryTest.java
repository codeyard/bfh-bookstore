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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Optional<Order> order = orderRepository.findById(100023L);
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

        List<OrderInfoDTO> orders = orderRepository.findAllByCustomerAndDateBetween(
            customer, dateFrom, dateTo
        );

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

        assertEquals(1, orders.size());
        assertEquals(new BigDecimal("1685.14"), orders.get(0).amount());
    }


    // Query 7: Get Order Statistics with total amount, number of positions and average order amount of all orders grouped by year and customer

    /**
     * All Customers have at least one Order in the Test Database
     * 2 Customers have multiple Orders
     * 1 Customer has two Orders in the same Year
     */
    @Test
    void testStatistics_foundAll() {
        List<CustomerOrderStatistics> allCustomerOrderStatistics = orderRepository.getAllCustomerOrderStatistics();
        List<Customer> allCustomer = customerRepository.findAll();

        assertEquals(allCustomer.size(), allCustomerOrderStatistics.size() - 1);

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
        Map<Long, Long> ordersByCustomerInDifferentYears = allCustomerOrderStatistics
            .stream().collect(groupingBy(CustomerOrderStatistics::getCustomerId, counting()));

        List<Long> customersWithMultipleOrdersInDifferentYears = ordersByCustomerInDifferentYears.entrySet().stream()
            .filter(x -> x.getValue() > 1L)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        assertTrue(customersWithMultipleOrdersInDifferentYears.size() > 0);

        long count = allCustomerOrderStatistics.stream()
            .filter(customerOrderStatistics ->
                Objects.equals(customerOrderStatistics.getCustomerId(), customersWithMultipleOrdersInDifferentYears.get(0)))
            .map(CustomerOrderStatistics::getYear)
            .distinct().count();

        assertTrue(count > 1);
    }

    @Test
    void testStatistics_foundCustomerWithMultipleOrdersInTheSameYear() {
        List<CustomerOrderStatistics> allCustomerOrderStatistics = orderRepository.getAllCustomerOrderStatistics();

        LocalDateTime dateFrom = LocalDateTime.of(
            LocalDate.of(2021, 1, 1),
            LocalTime.of(0, 0));

        LocalDateTime dateTo = LocalDateTime.of(
            LocalDate.of(2021, 12, 31),
            LocalTime.of(23, 59));

        List<OrderInfoDTO> ordersByCustomerAndPeriod = orderRepository.findOrdersByCustomerAndPeriod(10010L, dateFrom, dateTo);
        assertEquals(2, ordersByCustomerAndPeriod.size());

        Optional<CustomerOrderStatistics> aggregatedCustomerStatistics = allCustomerOrderStatistics.stream()
            .filter(customerOrderStatistics -> customerOrderStatistics.getCustomerId() == 10010L)
            .findFirst();
        assertTrue(aggregatedCustomerStatistics.isPresent());
        assertEquals(aggregatedCustomerStatistics.get().getTotalAmount(),
            ordersByCustomerAndPeriod.stream().mapToDouble(num -> num.amount().doubleValue()).sum());

        ordersByCustomerAndPeriod.forEach(orderInfo -> {
            assertEquals(2021, orderInfo.date().getYear());
        });

        assertEquals(2021, aggregatedCustomerStatistics.get().getYear());
    }
}
