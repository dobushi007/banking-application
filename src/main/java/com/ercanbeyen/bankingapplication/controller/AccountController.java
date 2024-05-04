package com.ercanbeyen.bankingapplication.controller;

import com.ercanbeyen.bankingapplication.constant.enums.AccountOperation;
import com.ercanbeyen.bankingapplication.constant.enums.AccountType;
import com.ercanbeyen.bankingapplication.constant.enums.City;
import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import com.ercanbeyen.bankingapplication.dto.AccountDto;
import com.ercanbeyen.bankingapplication.dto.request.TransferRequest;
import com.ercanbeyen.bankingapplication.option.AccountFilteringOptions;
import com.ercanbeyen.bankingapplication.dto.response.MessageResponse;
import com.ercanbeyen.bankingapplication.dto.response.CustomerStatisticsResponse;
import com.ercanbeyen.bankingapplication.service.impl.AccountService;
import com.ercanbeyen.bankingapplication.util.AccountUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController extends BaseController<AccountDto, AccountFilteringOptions> {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        super(accountService);
        this.accountService = accountService;
    }

    @PostMapping
    @Override
    public ResponseEntity<AccountDto> createEntity(@RequestBody @Valid AccountDto request) {
        AccountUtils.checkAccountConstruction(request);
        return new ResponseEntity<>(accountService.createEntity(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<AccountDto> updateEntity(@PathVariable("id") Integer id, @RequestBody @Valid AccountDto request) {
        AccountUtils.checkAccountConstruction(request);
        return new ResponseEntity<>(accountService.updateEntity(id, request), HttpStatus.OK);
    }

    @PutMapping("/{id}/individual")
    public ResponseEntity<MessageResponse<String>> updateBalance(@PathVariable("id") Integer id, @RequestParam("operation") AccountOperation operation, @Valid @RequestParam("amount") @Min(value = 1, message = "Minimum amount should be {value}") Double amount) {
        MessageResponse<String> response = new MessageResponse<>(accountService.applyUnidirectionalAccountOperation(id, operation, amount));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/deposit")
    public ResponseEntity<MessageResponse<String>> updateBalanceOfDepositAccount(@PathVariable("id") Integer id) {
        MessageResponse<String> response = new MessageResponse<>(accountService.addMoneyToDepositAccount(id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/transfer")
    public ResponseEntity<MessageResponse<String>> transferMoney(@RequestBody @Valid TransferRequest request) {
        AccountUtils.checkMoneyTransferRequest(request);
        MessageResponse<String> response = new MessageResponse<>(accountService.transferMoney(request));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/total")
    public ResponseEntity<MessageResponse<String>> getTotalAccounts(
            @RequestParam("city") City city,
            @RequestParam("type") AccountType type,
            @RequestParam("currency") Currency currency) {
        MessageResponse<String> response = new MessageResponse<>(accountService.getTotalAccounts(city, type, currency));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/statistics/maximum-balances")
    public ResponseEntity<MessageResponse<List<CustomerStatisticsResponse>>> getCustomerInformationWithMaximumBalance(
            @RequestParam("type") AccountType type,
            @RequestParam("currency") Currency currency,
            @RequestParam(name = "city", required = false) City city) {
        MessageResponse<List<CustomerStatisticsResponse>> response = new MessageResponse<>(
                accountService.getCustomersHaveMaximumBalance(type, currency, city));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
