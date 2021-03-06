package org.bookstore.order.repository;

import org.bookstore.customer.entity.Customer;
import org.bookstore.order.dto.CustomerOrderStatistics;
import org.bookstore.order.dto.OrderInfo;
import org.bookstore.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<OrderInfo> findAllByCustomerAndDateBetween(Customer customer, LocalDateTime dateFrom, LocalDateTime dateTo);

    @Query("""
        select  new org.bookstore.order.dto.OrderInfo(o.id, o.date, o.amount, o.status)
        from    Order o
        where   o.customer.id = :customerId
        and     o.date between :dateFrom and :dateTo
        """)
    List<OrderInfo> findOrdersByCustomerAndPeriod(Long customerId, LocalDateTime dateFrom, LocalDateTime dateTo);

    @Query(value = """
                select
                    extract(YEAR FROM o.order_date) as year,
                    o.customer_id as customerId,
                    concat(c.last_name, ' ',  c.first_name) as customerName,
                    sum(oi.book_price * oi.quantity) as totalAmount,
                    sum(oi.quantity) as numberOfBooks,
                    sum(oi.book_price * oi.quantity)/sum(oi.quantity) as averageBookPrice
                from book_order as o
                left join order_item as oi on o.id = oi.order_id
                inner join customer as c on o.customer_id = c.id
                group by extract(YEAR FROM o.order_date) , o.customer_id, concat(c.last_name, ' ',  c.first_name)
        """, nativeQuery = true)
    List<CustomerOrderStatistics> getAllCustomerOrderStatistics();
}
