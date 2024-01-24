package com.ercanbeyen.bankingapplication.controller;

import com.ercanbeyen.bankingapplication.dto.TransactionDto;
import com.ercanbeyen.bankingapplication.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<?> getTransactions() {
        List<TransactionDto> transactionDtoList = transactionService.getTransactions();
        return ResponseEntity.ok(transactionDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransaction(@PathVariable("id") String id) {
        TransactionDto transactionDto = transactionService.getTransaction(id);
        return ResponseEntity.ok(transactionDto);
    }
}
