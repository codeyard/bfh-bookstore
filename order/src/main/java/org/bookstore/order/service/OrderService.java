package org.bookstore.order.service;

import org.bookstore.customer.entity.Customer;
import org.bookstore.customer.exception.CustomerNotFoundException;
import org.bookstore.customer.service.CustomerService;
import org.bookstore.order.adapter.CatalogAdapter;
import org.bookstore.order.adapter.PaymentAdapter;
import org.bookstore.order.controller.OrderRequest;
import org.bookstore.order.dto.OrderInfo;
import org.bookstore.order.entity.*;
import org.bookstore.order.exception.OrderAlreadyShippedException;
import org.bookstore.order.exception.OrderNotFoundException;
import org.bookstore.order.exception.PaymentFailedException;
import org.bookstore.order.repository.OrderRepository;
import org.bookstore.security.Constants;
import org.bookstore.shipping.ShippingClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.MethodNotAllowedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The interface OrderService defines a service to manage the orders of a bookstore.
 *
 * @author Igor Stojanovic, Raphael Gerber
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final ShippingClient shippingClient;
    private final CatalogAdapter catalogAdapter;
    private final PaymentAdapter paymentAdapter;
    @Value("${payment.maxAmount:1000}")
    private BigDecimal maxAmount;

    public OrderService(OrderRepository orderRepository,
                        CustomerService customerService,
                        ShippingClient shippingClient,
                        CatalogAdapter catalogAdapter,
                        PaymentAdapter paymentAdapter) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.shippingClient = shippingClient;
        this.catalogAdapter = catalogAdapter;
        this.paymentAdapter = paymentAdapter;
    }

    public Order prepareOrder(long customerId, OrderRequest orderRequest) throws CustomerNotFoundException, PaymentFailedException {
        List<OrderItem> orderItems = new ArrayList<>();
        orderRequest.getItems().forEach(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(item.getQuantity());
            Book book = catalogAdapter.findBook(item.getIsbn());
            orderItem.setBook(book);
            orderItems.add(orderItem);
        });
        return placeOrder(customerId, orderItems);
    }

    /**
     * Places an order with the bookstore.
     *
     * @param customerId - the identifier of the customer
     * @param items      - the items to be ordered
     * @return the data of the placed order
     * @throws CustomerNotFoundException - if no customer with the specified identifier exists
     * @throws PaymentFailedException    - if the credit card payment failed
     */
    public Order placeOrder(long customerId, List<OrderItem> items) throws CustomerNotFoundException, PaymentFailedException {
        Customer customer = customerService.findCustomer(customerId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!customer.getUsername().equals(authentication.getName())) {
            throw new RuntimeException();
        }

        BigDecimal totalAmount = items.stream().
            map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Payment payment = paymentAdapter.makePayment(customer, customer.getCreditCard(), totalAmount);

        Order order = new Order();
        order.setDate(LocalDateTime.now());
        order.setAmount(totalAmount);
        order.setStatus(OrderStatus.ACCEPTED);
        order.setAddress(customer.getAddress());
        order.setPayment(payment);
        order.setCustomer(customer);
        order.setItems(items);

        orderRepository.saveAndFlush(order);
        shippingClient.sendShippingOrder(order);

        return order;
    }

    /**
     * Finds an order by identifier.
     *
     * @param id - the identifier of the order
     * @return the data of the found order
     * @throws OrderNotFoundException - if no order with the specified identifier exists
     */
    @PostAuthorize("hasRole('ROLE_EMPLOYEE') OR (hasRole('ROLE_CUSTOMER') && (authentication.name==returnObject.customer.username))")
    public Order findOrder(long id) throws OrderNotFoundException {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    /**
     * Searches for orders by customer and year.
     *
     * @param customerId - the identifier of the customer
     * @param year       - the year of the orders
     * @return the matching orders
     * @throws CustomerNotFoundException - if no customer with the specified identifier exists
     */
    public List<OrderInfo> searchOrders(long customerId, int year) throws CustomerNotFoundException {
        Customer customer = customerService.findCustomer(customerId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().contains(Constants.CUSTOMER_AUTHORITY)) {
            if (!customer.getUsername().equals(authentication.getName())) {
                throw new RuntimeException();
            }
        }

        LocalDateTime dateFrom = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        LocalDateTime dateTo = LocalDateTime.of(year, 12, 31, 23, 59, 59);
        return orderRepository.findOrdersByCustomerAndPeriod(customerId, dateFrom, dateTo);
    }

    /**
     * Tries to cancel an order.
     *
     * @param id - the identifier of the order
     * @throws OrderNotFoundException       - if no order with the specified identifier exists
     * @throws OrderAlreadyShippedException - if the order has already been shipped
     */
    public void cancelOrder(long id) throws OrderNotFoundException, OrderAlreadyShippedException {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus().equals(OrderStatus.SHIPPED)) {
            throw new OrderAlreadyShippedException(id);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().contains(Constants.CUSTOMER_AUTHORITY)) {
            if (!order.getCustomer().getUsername().equals(authentication.getName())) {
                throw new RuntimeException();
            }
        }

        orderRepository.saveAndFlush(order);
        shippingClient.sendCancellation(order.getId());
    }
}
