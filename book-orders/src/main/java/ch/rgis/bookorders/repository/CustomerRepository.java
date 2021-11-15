package ch.rgis.bookorders.repository;

import ch.rgis.bookorders.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
