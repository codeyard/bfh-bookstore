package ch.rgis.bookorders.order.service;

import ch.rgis.bookorders.customer.exception.CustomerNotFoundException;
import ch.rgis.bookorders.order.dto.OrderInfo;
import ch.rgis.bookorders.order.entity.Order;
import ch.rgis.bookorders.order.entity.OrderItem;
import ch.rgis.bookorders.order.exception.OrderAlreadyShippedException;
import ch.rgis.bookorders.order.exception.OrderNotFoundException;
import ch.rgis.bookorders.order.exception.PaymentFailedException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The interface OrderService defines a service to manage the orders of a bookstore.
 *
 * @author Igor Stojanovic, Raphael Gerber
 */
@Service
public class OrderService {

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
        return null;
    }

    /**
     * Finds an order by identifier.
     *
     * @param id - the identifier of the order
     * @return the data of the found order
     * @throws OrderNotFoundException - if no order with the specified identifier exists
     */
    public Order findOrder(long id) throws OrderNotFoundException {
        return null;
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
        return null;
    }

    /**
     * Tries to cancel an order.
     *
     * @param id - the identifier of the order
     * @throws OrderNotFoundException       - if no order with the specified identifier exists
     * @throws OrderAlreadyShippedException - if the order has already been shipped
     */
    public void cancelOrder(long id) throws OrderNotFoundException, OrderAlreadyShippedException {

    }
}
