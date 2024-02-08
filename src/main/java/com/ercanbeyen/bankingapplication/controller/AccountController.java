package com.ercanbeyen.bankingapplication.controller;

import com.ercanbeyen.bankingapplication.constant.enums.AccountOperation;
import com.ercanbeyen.bankingapplication.dto.AccountDto;
import com.ercanbeyen.bankingapplication.dto.request.TransferRequest;
import com.ercanbeyen.bankingapplication.option.AccountFilteringOptions;
import com.ercanbeyen.bankingapplication.response.MessageResponse;
import com.ercanbeyen.bankingapplication.service.impl.AccountService;
import com.ercanbeyen.bankingapplication.util.AccountUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return new ResponseEntity<>(accountService.createEntity(request), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<AccountDto> updateEntity(@PathVariable("id") Integer id, @RequestBody @Valid AccountDto request) {
        AccountUtils.checkAccountConstruction(request);
        return new ResponseEntity<>(accountService.updateEntity(id, request), HttpStatus.OK);
    }

    @PutMapping("/{id}/individual")
    public ResponseEntity<MessageResponse> updateBalance(@PathVariable("id") Integer id, @RequestParam("operation") AccountOperation operation, @Valid @RequestParam("amount") @Min(value = 1, message = "Minimum amount should be {value}") Double amount) {
        String message = accountService.applyUnidirectionalAccountOperation(id, operation, amount);
        MessageResponse response = new MessageResponse(message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/deposit")
    public ResponseEntity<MessageResponse> updateBalanceOfDepositAccount(@PathVariable("id") Integer id) {
        String message = accountService.addMoneyToDepositAccount(id);
        MessageResponse response = new MessageResponse(message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/transfer")
    public ResponseEntity<MessageResponse> transferMoney(@RequestBody @Valid TransferRequest request) {
        AccountUtils.checkMoneyTransferRequest(request);
        String message = accountService.transferMoney(request);
        MessageResponse response = new MessageResponse(message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
