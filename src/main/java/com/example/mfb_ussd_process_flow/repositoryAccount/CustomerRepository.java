package com.example.mfb_ussd_process_flow.repositoryAccount;

import com.example.mfb_ussd_process_flow.entityAccount.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    @Query("SELECT c FROM Customer c WHERE c.CustomerId = :customerId")
    Optional<Customer> findCustomerByCustomerId(@Param("customerId") String customerId);
}
