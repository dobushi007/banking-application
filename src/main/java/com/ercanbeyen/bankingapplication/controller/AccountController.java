package com.ercanbeyen.bankingapplication.controller;

import com.ercanbeyen.bankingapplication.dto.AccountDto;
import com.ercanbeyen.bankingapplication.service.impl.AccountService;
import com.ercanbeyen.bankingapplication.util.AccountUtils;
import jakarta.validation.Valid;
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
}
