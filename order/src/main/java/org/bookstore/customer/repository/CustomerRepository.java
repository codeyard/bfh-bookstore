package org.bookstore.customer.repository;

import org.bookstore.customer.dto.CustomerInfo;
import org.bookstore.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    @Query("""
        select  new org.bookstore.customer.dto.CustomerInfo(c.id, c.firstName, c.lastName, c.email)
        from    Customer c
        where   upper(c.firstName) like upper(concat('%', :name, '%'))
                or upper(c.lastName) like upper(concat('%', :name, '%'))
        """)
    List<CustomerInfo> findCustomersByName(String name);

    boolean existsByUsername(String username);
}
