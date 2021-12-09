package ch.rgis.bookorders.order.service;

import ch.rgis.bookorders.customer.entity.CreditCard;
import ch.rgis.bookorders.customer.entity.Customer;
import ch.rgis.bookorders.customer.exception.CustomerNotFoundException;
import ch.rgis.bookorders.customer.service.CustomerService;
import ch.rgis.bookorders.order.dto.OrderInfo;
import ch.rgis.bookorders.order.entity.Order;
import ch.rgis.bookorders.order.entity.OrderItem;
import ch.rgis.bookorders.order.entity.OrderStatus;
import ch.rgis.bookorders.order.entity.Payment;
import ch.rgis.bookorders.order.exception.OrderAlreadyShippedException;
import ch.rgis.bookorders.order.exception.OrderNotFoundException;
import ch.rgis.bookorders.order.exception.PaymentFailedException;
import ch.rgis.bookorders.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

/**
 * The interface OrderService defines a service to manage the orders of a bookstore.
 *
 * @author Igor Stojanovic, Raphael Gerber
 */
@Service
public class OrderService {

    @Value("${payment.maxAmount:1000}")
    private BigDecimal maxAmount;

    private final OrderRepository orderRepository;
    private final CustomerService customerService;

    public OrderService(OrderRepository orderRepository, CustomerService customerService) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
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

        // Case 1: Total order amount too high
        BigDecimal totalAmount = items.stream().
            map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("Total Amount: " + totalAmount);
        if (totalAmount.compareTo(maxAmount) > 0) {
            throw new PaymentFailedException();
        }

        // Case 2: Credit card expired
        CreditCard creditCard = customer.getCreditCard();
        LocalDate initial = LocalDate.of(creditCard.getExpirationYear(), creditCard.getExpirationMonth(), 1);
        LocalDate expirationDate = initial.with(lastDayOfMonth());
        if (expirationDate.isAfter(LocalDate.now())) {
            throw new PaymentFailedException();
        }

        // Case 3: Credit card number invalid
        String regex = "(^[0-9]{16}$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(creditCard.getNumber().replaceAll("-", ""));
        if (!matcher.matches()) {
            throw new PaymentFailedException();
        }

        Order order = new Order();
        order.setDate(LocalDateTime.now());
        order.setAmount(totalAmount);
        order.setStatus(OrderStatus.ACCEPTED);
        order.setAddress(customer.getAddress());

        Payment payment = new Payment();
        payment.setDate(LocalDateTime.now());
        payment.setAmount(totalAmount);
        payment.setCreditCardNumber(creditCard.getNumber());
        payment.setTransactionId("1");

        order.setPayment(payment);
        order.setCustomer(customer);
        order.setItems(items);

        return orderRepository.saveAndFlush(order);
    }

    /**
     * Finds an order by identifier.
     *
     * @param id - the identifier of the order
     * @return the data of the found order
     * @throws OrderNotFoundException - if no order with the specified identifier exists
     */
    public Order findOrder(long id) throws OrderNotFoundException {
        return orderRepository.findById(id).orElseThrow(OrderNotFoundException::new);
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
        customerService.findCustomer(customerId);

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
        Order order = orderRepository.findById(id).orElseThrow(OrderNotFoundException::new);

        if (order.getStatus().equals(OrderStatus.SHIPPED)) {
            throw new OrderAlreadyShippedException();
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.saveAndFlush(order);
    }
}
