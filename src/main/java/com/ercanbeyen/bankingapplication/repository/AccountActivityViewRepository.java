package com.ercanbeyen.bankingapplication.repository;

import com.ercanbeyen.bankingapplication.view.AccountActivityView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountActivityViewRepository extends JpaRepository<AccountActivityView, String> {
    List<AccountActivityView> findBySenderAccountIdAndReceiverAccountId(Integer senderAccountId, Integer receiverAccountId);
}
