package com.ercanbeyen.bankingapplication.controller;

import com.ercanbeyen.bankingapplication.dto.AccountActivityDto;
import com.ercanbeyen.bankingapplication.entity.AccountActivityView;
import com.ercanbeyen.bankingapplication.option.AccountActivityFilteringOptions;
import com.ercanbeyen.bankingapplication.service.AccountActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/account-activities")
@RequiredArgsConstructor
public class AccountActivityController {
    private final AccountActivityService accountActivityService;

    @GetMapping
    public ResponseEntity<List<AccountActivityDto>> getAccountActivities(AccountActivityFilteringOptions options) {
        List<AccountActivityDto> accountActivityDtos = accountActivityService.getAccountActivities(options);
        return ResponseEntity.ok(accountActivityDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountActivityDto> getAccountActivity(@PathVariable("id") String id) {
        AccountActivityDto accountActivityDto = accountActivityService.getAccountActivity(id);
        return ResponseEntity.ok(accountActivityDto);
    }

    @GetMapping("/views")
    public ResponseEntity<List<AccountActivityView>> getAccountActivityViews(
            @RequestParam(name = "senderAccountId") Integer senderAccountId,
            @RequestParam(name = "receiverAccountId") Integer receiverAccountId) {
        List<AccountActivityView> accountActivityViews = accountActivityService.getAccountActivityViews(senderAccountId, receiverAccountId);
        return ResponseEntity.ok(accountActivityViews);
    }
}
