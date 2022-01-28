package org.bookstore.order.controller;

import org.bookstore.customer.exception.CustomerNotFoundException;
import org.bookstore.order.dto.OrderInfo;
import org.bookstore.order.entity.Order;
import org.bookstore.order.exception.OrderAlreadyShippedException;
import org.bookstore.order.exception.OrderNotFoundException;
import org.bookstore.order.exception.PaymentFailedException;
import org.bookstore.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Order placeOrder(@RequestBody @Valid OrderRequest orderRequest) throws CustomerNotFoundException, PaymentFailedException {
        return orderService.prepareOrder(orderRequest.getCustomerId(), orderRequest);
    }

    @GetMapping(params = {"customerId", "year"}, produces = APPLICATION_JSON_VALUE)
    public List<OrderInfo> searchOrder(@RequestParam Long customerId, @RequestParam int year) throws CustomerNotFoundException {
        return orderService.searchOrders(customerId, year);
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public Order findOrder(@PathVariable Long id) throws OrderNotFoundException {
        return orderService.findOrder(id);
    }

    @PatchMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable Long id) throws OrderNotFoundException, OrderAlreadyShippedException {
        orderService.cancelOrder(id);
    }


}
