package com.ercanbeyen.bankingapplication.repository;

import com.ercanbeyen.bankingapplication.entity.Customer;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CustomerRepository extends BaseRepository<Customer> {
    Optional<Customer> findByNationalId(String nationalId);
    boolean existsByNationalId(String nationalId);
}
