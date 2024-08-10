package com.ercanbeyen.bankingapplication.repository;

import com.ercanbeyen.bankingapplication.entity.AccountActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountActivityRepository extends JpaRepository<AccountActivity, String> {

}
