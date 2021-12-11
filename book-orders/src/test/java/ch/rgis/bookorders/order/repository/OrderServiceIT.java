package ch.rgis.bookorders.order.repository;

import ch.rgis.bookorders.customer.entity.Customer;
import ch.rgis.bookorders.customer.exception.CustomerNotFoundException;
import ch.rgis.bookorders.customer.repository.CustomerRepository;
import ch.rgis.bookorders.order.entity.Book;
import ch.rgis.bookorders.order.entity.Order;
import ch.rgis.bookorders.order.entity.OrderItem;
import ch.rgis.bookorders.order.entity.OrderStatus;
import ch.rgis.bookorders.order.exception.PaymentFailedException;
import ch.rgis.bookorders.order.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;

@SpringBootTest
public class OrderServiceIT {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Value("${payment.maxAmount}")
    private BigDecimal maxAmount;

    @Test
    void placeOrder_successful() throws CustomerNotFoundException, PaymentFailedException {
        List<OrderItem> items = createOrderItems(false);
        Assertions.assertTrue(
                items.stream()
                        .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .compareTo(maxAmount) < 0);


        Optional<Customer> optionalCustomer = customerRepository.findById(10020L);
        Assertions.assertTrue(optionalCustomer.isPresent());
        optionalCustomer.get().getCreditCard().setExpirationYear(LocalDateTime.now().getYear() + 1);

        Order order = orderService.placeOrder(optionalCustomer.get().getId(), items);

        Optional<Order> savedOrderOptional = orderRepository.findById(order.getId());
        Assertions.assertTrue(savedOrderOptional.isPresent());
        Order savedOrder = savedOrderOptional.get();


        Assertions.assertEquals(OrderStatus.ACCEPTED, savedOrder.getStatus());
        Assertions.assertEquals(optionalCustomer.get().getAddress(), savedOrder.getAddress());
        Assertions.assertEquals(2, savedOrder.getItems().size());

        Assertions.assertEquals(0, items.stream()
                .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .compareTo(savedOrder.getAmount()));

        Assertions.assertEquals(optionalCustomer.get().getId(), savedOrder.getCustomer().getId());
        Assertions.assertEquals(LocalDate.now(), savedOrder.getDate().toLocalDate());

        Assertions.assertEquals(0, items.stream()
                .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .compareTo(savedOrder.getPayment().getAmount()));

    }


    @Test
    void placeOrder_throwsPaymentFailedExceptionBecauseOfAmount() {
        List<OrderItem> items = createOrderItems(true);

        Assertions.assertTrue(
                items.stream()
                        .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .compareTo(maxAmount) > 0);

        Optional<Customer> optionalCustomer = customerRepository.findById(10020L);

        Assertions.assertTrue(optionalCustomer.isPresent());

        Assertions.assertThrows(PaymentFailedException.class,
                () -> orderService.placeOrder(optionalCustomer.get().getId(), items));
    }

    @Test
    @Transactional
    void placeOrder_throwsPaymentFailedExceptionBecauseOfExpiredCard() {
        List<OrderItem> items = createOrderItems(false);

        Optional<Customer> optionalCustomer = customerRepository.findById(10020L);
        Assertions.assertTrue(optionalCustomer.isPresent());
        optionalCustomer.get().getCreditCard().setExpirationYear(LocalDateTime.now().getYear()-1);
        customerRepository.saveAndFlush(optionalCustomer.get());

        assertThrows(PaymentFailedException.class, () -> orderService.placeOrder(optionalCustomer.get().getId(), items));
    }

    @Test
    @Transactional
    void placeOrder_throwsPaymentFailedExceptionBecauseOfInvalidCardNumber() {
        List<OrderItem> items = createOrderItems(false);

        Optional<Customer> optionalCustomer = customerRepository.findById(10020L);
        Assertions.assertTrue(optionalCustomer.isPresent());
        optionalCustomer.get().getCreditCard().setNumber("1111");
        customerRepository.saveAndFlush(optionalCustomer.get());

        assertThrows(PaymentFailedException.class, () -> orderService.placeOrder(optionalCustomer.get().getId(), items));
    }

    @Test
    @Transactional
    void placeOrder_throwsCustomerNotFoundExceptin() {
        List<OrderItem> items = createOrderItems(false);

        assertThrows(CustomerNotFoundException.class, () -> orderService.placeOrder(100030L, items));
    }





    private List<OrderItem> createOrderItems(boolean failingAmount) {

        OrderItem orderItem1 = new OrderItem();

        orderItem1.setQuantity(3);
        Book book1 = new Book();
        book1.setIsbn("1111-1111-111-1");
        book1.setAuthors("Igor Stojanovic, Raphael Geber");
        book1.setPublisher("BFH");
        book1.setTitle("The amazing broCode");
        book1.setPrice(new BigDecimal("22.85"));
        orderItem1.setBook(book1);

        OrderItem orderItem2 = new OrderItem();

        orderItem2.setQuantity(5);
        Book book2 = new Book();
        book2.setIsbn("2222-2222-222-2");
        book2.setAuthors("Raphael Geber");
        book2.setPublisher("BFH");
        book2.setTitle("The amazing playBook");
        book2.setPrice(new BigDecimal("33.80"));
        orderItem2.setBook(book2);


        if (failingAmount) {
            orderItem1.setQuantity(orderItem1.getQuantity() * 20);
            orderItem2.setQuantity(orderItem2.getQuantity() * 20);
        }

        return new ArrayList<>(Arrays.asList(orderItem1, orderItem2));

    }


}
