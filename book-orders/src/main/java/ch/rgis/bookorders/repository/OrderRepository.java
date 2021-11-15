package ch.rgis.bookorders.repository;

import ch.rgis.bookorders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
