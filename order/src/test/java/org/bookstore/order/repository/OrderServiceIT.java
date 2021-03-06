package org.bookstore.order.repository;

import org.bookstore.customer.entity.Customer;
import org.bookstore.customer.exception.CustomerNotFoundException;
import org.bookstore.customer.repository.CustomerRepository;
import org.bookstore.order.adapter.PaymentAdapter;
import org.bookstore.order.controller.ErrorCode;
import org.bookstore.order.controller.ErrorInfo;
import org.bookstore.order.dto.OrderInfo;
import org.bookstore.order.entity.Book;
import org.bookstore.order.entity.Order;
import org.bookstore.order.entity.OrderItem;
import org.bookstore.order.entity.OrderStatus;
import org.bookstore.order.exception.OrderAlreadyShippedException;
import org.bookstore.order.exception.OrderNotFoundException;
import org.bookstore.order.exception.PaymentFailedException;
import org.bookstore.order.service.OrderService;
import org.bookstore.shipping.ShippingClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class OrderServiceIT {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @MockBean
    private ShippingClient shippingClient;

    @MockBean
    private PaymentAdapter paymentAdapter;


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

        verify(shippingClient, times(1)).sendShippingOrder(order);

    }


    @Test
    void placeOrder_throwsPaymentFailedExceptionBecauseOfInvalidCardOrType() {
        List<OrderItem> items = createOrderItems(true);

        Assertions.assertTrue(
            items.stream()
                .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .compareTo(maxAmount) > 0);

        Optional<Customer> optionalCustomer = customerRepository.findById(10020L);

        Assertions.assertTrue(optionalCustomer.isPresent());

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setTimestamp(LocalDateTime.now());
        errorInfo.setStatus(422);
        errorInfo.setError("Unprocessable Entity");
        errorInfo.setMessage("Invalid credit card number or type");
        errorInfo.setPath("/payments");
        errorInfo.setCode(ErrorCode.INVALID_CREDIT_CARD);

        Mockito.when(paymentAdapter.makePayment(any(), any(), any())).thenThrow(new PaymentFailedException(errorInfo));

        PaymentFailedException exception = Assertions.assertThrows(PaymentFailedException.class,
            () -> orderService.placeOrder(optionalCustomer.get().getId(), items));

        Assertions.assertEquals(ErrorCode.INVALID_CREDIT_CARD, exception.getErrorInfo().getCode());
        Assertions.assertEquals("/payments", exception.getErrorInfo().getPath());
        Assertions.assertEquals(422, exception.getErrorInfo().getStatus());
        Assertions.assertEquals("Invalid credit card number or type", exception.getErrorInfo().getMessage());

    }

    @Test
    void placeOrder_throwsPaymentFailedExceptionBecauseOfInvalidPaymentData() {
        List<OrderItem> items = createOrderItems(false);

        Optional<Customer> optionalCustomer = customerRepository.findById(10020L);
        Assertions.assertTrue(optionalCustomer.isPresent());
        optionalCustomer.get().getCreditCard().setExpirationYear(LocalDateTime.now().getYear() - 1);
        customerRepository.saveAndFlush(optionalCustomer.get());

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setTimestamp(LocalDateTime.now());
        errorInfo.setStatus(400);
        errorInfo.setError("Bad Request");
        errorInfo.setMessage("Missing payment amount");
        errorInfo.setPath("/payments");

        Mockito.when(paymentAdapter.makePayment(any(), any(), any())).thenThrow(new PaymentFailedException(errorInfo));
        PaymentFailedException exception = assertThrows(PaymentFailedException.class, () -> orderService.placeOrder(optionalCustomer.get().getId(), items));

        Assertions.assertEquals("/payments", exception.getErrorInfo().getPath());
        Assertions.assertEquals(400, exception.getErrorInfo().getStatus());
        Assertions.assertEquals("Missing payment amount", exception.getErrorInfo().getMessage());
        Assertions.assertEquals("Bad Request", exception.getErrorInfo().getError());

    }

    @Test
    @Transactional
    void placeOrder_throwsCustomerNotFoundException() {
        List<OrderItem> items = createOrderItems(false);

        assertThrows(CustomerNotFoundException.class, () -> orderService.placeOrder(100030L, items));
    }

    @Test
    void findOrder_successful() throws OrderNotFoundException {
        Order order = orderService.findOrder(100000L);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        Assertions.assertEquals(LocalDateTime.parse("2021-02-03 14:17:00.000000", formatter), order.getDate());
        Assertions.assertEquals(5, order.getItems().size());
        Assertions.assertEquals(OrderStatus.PROCESSING, order.getStatus());
        Assertions.assertEquals("cscoular0@tinyurl.com", order.getCustomer().getEmail());
        Assertions.assertEquals("5100137730185616", order.getCustomer().getCreditCard().getNumber());
        Assertions.assertEquals("Bern", order.getCustomer().getAddress().getCity());
        Assertions.assertEquals(order.getAmount(), order.getItems().stream()
            .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add));


    }

    @Test
    void findOrder_throwsOrderNotFoundException() {
        assertThrows(OrderNotFoundException.class, () -> orderService.findOrder(300000L));
    }

    @Test
    void searchOrders_successful() throws CustomerNotFoundException {
        List<OrderInfo> orderInfos = orderService.searchOrders(10010L, 2021);
        Assertions.assertEquals(2, orderInfos.size());
        orderInfos.forEach(orderInfo -> {
            Assertions.assertEquals(2021, orderInfo.date().getYear());
            Assertions.assertNotNull(orderInfo.amount());
        });
    }

    @Test
    void searchOrders_throwsCustomerNotFoundException() {
        assertThrows(CustomerNotFoundException.class, () -> orderService.searchOrders(30000L, 2021));
    }


    @Test
    void cancelOrder_successful() throws OrderNotFoundException, OrderAlreadyShippedException {
        long id = 100022L;

        orderService.cancelOrder(id);
        Optional<Order> cancelledOrder = orderRepository.findById(id);
        Assertions.assertTrue(cancelledOrder.isPresent());
        Assertions.assertEquals(OrderStatus.ACCEPTED, cancelledOrder.get().getStatus());

        verify(shippingClient, times(1)).sendCancellation(id);
    }

    @Test
    void cancelOrder_throwsOrderNotFoundException() {
        assertThrows(OrderNotFoundException.class, () -> orderService.cancelOrder(30000L));
    }

    @Test
    void cancelOrder_throwsOrderAlreadyShippedException() {
        assertThrows(OrderAlreadyShippedException.class, () -> orderService.cancelOrder(100016L));
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
