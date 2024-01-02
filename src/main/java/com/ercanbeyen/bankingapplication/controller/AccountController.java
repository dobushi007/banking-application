package com.ercanbeyen.bankingapplication.controller;

import com.ercanbeyen.bankingapplication.dto.AccountDto;
import com.ercanbeyen.bankingapplication.service.impl.AccountService;
import com.ercanbeyen.bankingapplication.util.AccountUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController extends BaseController<AccountDto> {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        super(accountService);
        this.accountService = accountService;
    }

    @PostMapping
    @Override
    public ResponseEntity<?> createEntity(@RequestBody @Valid AccountDto request) {
        AccountUtils.checkAccountConstruction(request);
        return new ResponseEntity<>(accountService.createEntity(request), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<?> updateEntity(@PathVariable("id") Integer id, @RequestBody @Valid AccountDto request) {
        AccountUtils.checkAccountConstruction(request);
        return new ResponseEntity<>(accountService.updateEntity(id, request), HttpStatus.OK);
    }

    @PutMapping("/add/{id}")
    public ResponseEntity<?> addMoney(@PathVariable("id") Integer id, @Valid @RequestParam("amount") @Min(value = 1, message = "Minimum amount should be {value}") Double amount) {
        return new ResponseEntity<>(accountService.addMoney(id, amount), HttpStatus.OK);
    }

    @PutMapping("/withdraw/{id}")
    public ResponseEntity<?> withdrawMoney(@PathVariable("id") Integer id, @Valid @RequestParam("amount") @Min(value = 1, message = "Minimum amount should be {value}") Double amount) {
        return new ResponseEntity<>(accountService.withdrawMoney(id, amount), HttpStatus.OK);
    }
}
