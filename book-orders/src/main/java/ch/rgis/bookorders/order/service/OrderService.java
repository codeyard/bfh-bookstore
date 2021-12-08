package ch.rgis.bookorders.order.service;

import ch.rgis.bookorders.customer.exception.CustomerNotFoundException;
import ch.rgis.bookorders.order.dto.OrderInfo;
import ch.rgis.bookorders.order.entity.Order;
import ch.rgis.bookorders.order.entity.OrderItem;
import ch.rgis.bookorders.order.exception.OrderAlreadyShippedException;
import ch.rgis.bookorders.order.exception.OrderNotFoundException;
import ch.rgis.bookorders.order.exception.PaymentFailedException;

import java.util.List;

public class OrderService {

    public Order placeOrder(long customerId, List<OrderItem> items) throws CustomerNotFoundException, PaymentFailedException {
        return null;
    }

    public Order findOrder(long id) throws OrderNotFoundException {
        return null;
    }

    public List<OrderInfo> searchOrders(long customerId, int year) throws CustomerNotFoundException {
        return null;
    }

    public void cancelOrder(long id) throws OrderNotFoundException, OrderAlreadyShippedException {

    }
}
