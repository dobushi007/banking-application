package com.ercanbeyen.bankingapplication.controller;

import com.ercanbeyen.bankingapplication.dto.TransactionDto;
import com.ercanbeyen.bankingapplication.entity.TransactionView;
import com.ercanbeyen.bankingapplication.option.TransactionFilteringOptions;
import com.ercanbeyen.bankingapplication.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getTransactions(TransactionFilteringOptions options) {
        List<TransactionDto> transactionDtos = transactionService.getTransactions(options);
        return ResponseEntity.ok(transactionDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransaction(@PathVariable("id") String id) {
        TransactionDto transactionDto = transactionService.getTransaction(id);
        return ResponseEntity.ok(transactionDto);
    }

    @GetMapping("/views")
    public ResponseEntity<List<TransactionView>> getTransactionViews(
            @RequestParam(name = "senderAccountId") Integer senderAccountId,
            @RequestParam(name = "receiverAccountId") Integer receiverAccountId) {
        List<TransactionView> transactionViews = transactionService.getTransactions(senderAccountId, receiverAccountId);
        return ResponseEntity.ok(transactionViews);
    }
}
