package com.ercanbeyen.bankingapplication.service;

import com.ercanbeyen.bankingapplication.dto.AccountActivityDto;
import com.ercanbeyen.bankingapplication.dto.request.AccountActivityRequest;
import com.ercanbeyen.bankingapplication.view.AccountActivityView;
import com.ercanbeyen.bankingapplication.option.AccountActivityFilteringOptions;

import java.util.List;

public interface AccountActivityService {
    List<AccountActivityDto> getAccountActivities(AccountActivityFilteringOptions options);
    AccountActivityDto getAccountActivity(String id);
    void createAccountActivity(AccountActivityRequest request);
    List<AccountActivityView> getAccountActivityViews(Integer senderAccountId, Integer receiverAccountId);
}
