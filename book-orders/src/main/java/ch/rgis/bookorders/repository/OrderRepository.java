package ch.rgis.bookorders.repository;

import ch.rgis.bookorders.dto.OrderInfoDTO;
import ch.rgis.bookorders.entity.Customer;
import ch.rgis.bookorders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findById(Long id);

    List<OrderInfoDTO> findAllByCustomerAndDateGreaterThanEqualAndDateLessThanEqual(Customer customer, LocalDateTime dateFrom, LocalDateTime dateTo);

    @Query("""
        select  new ch.rgis.bookorders.dto.OrderInfoDTO(o.id, o.date, o.amount, o.status)
        from    Order o
        where   o.customer.id = :customerId
        and     o.date between :dateFrom and :dateTo
        """)
    List<OrderInfoDTO> findOrdersByCustomerAndPeriod(Long customerId, LocalDateTime dateFrom, LocalDateTime dateTo);
}
