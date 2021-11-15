package ch.rgis.bookorders.repository;

import ch.rgis.bookorders.dto.CustomerInfoDTO;
import ch.rgis.bookorders.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    @Query("""
        select  new ch.rgis.bookorders.dto.CustomerInfoDTO(c.id, c.firstName, c.lastName, c.email)
        from    Customer c
        where   upper(c.firstName) like upper(concat('%', :name, '%'))
                or upper(c.lastName) like upper(concat('%', :name, '%'))
        """)
    List<CustomerInfoDTO> findCustomersByName(String name);
}
