package com.ercanbeyen.bankingapplication.controller;

import com.ercanbeyen.bankingapplication.dto.*;
import com.ercanbeyen.bankingapplication.entity.File;
import com.ercanbeyen.bankingapplication.option.AccountFilteringOptions;
import com.ercanbeyen.bankingapplication.option.CustomerFilteringOptions;
import com.ercanbeyen.bankingapplication.option.AccountActivityFilteringOptions;
import com.ercanbeyen.bankingapplication.dto.response.MessageResponse;
import com.ercanbeyen.bankingapplication.service.impl.CustomerService;
import com.ercanbeyen.bankingapplication.util.PhotoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@Slf4j
public class CustomerController extends BaseController<CustomerDto, CustomerFilteringOptions> {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        super(customerService);
        this.customerService = customerService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<MessageResponse<String>> uploadProfilePhoto(@PathVariable("id") Integer id, @RequestParam("file") MultipartFile file) {
        PhotoUtils.checkPhoto(file);
        MessageResponse<String> response = new MessageResponse<>(customerService.uploadProfilePhoto(id, file));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> downloadProfilePhoto(@PathVariable("id") Integer id) {
        File file = customerService.downloadProfilePhoto(id);

        String fileName = file.getName();
        log.info("file.getName() and its length: {} - {}", fileName, fileName.length());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(file.getData());
    }

    @DeleteMapping("/{id}/photo")
    public ResponseEntity<MessageResponse<String>> deleteProfilePhoto(@PathVariable("id") Integer id) {
        MessageResponse<String> response = new MessageResponse<>(customerService.deleteProfilePhoto(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/accounts")
    public ResponseEntity<List<AccountDto>> getAccounts(@PathVariable("id") Integer id, AccountFilteringOptions options) {
        return ResponseEntity.ok(customerService.getAccountsOfCustomer(id, options));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<AccountActivityDto>> getTransactions(@PathVariable("id") Integer id, AccountActivityFilteringOptions options) {
        return ResponseEntity.ok(customerService.getAccountActivitiesOfCustomer(id, options));
    }

    @GetMapping("/{id}/notifications")
    public ResponseEntity<List<NotificationDto>> getNotifications(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(customerService.getNotifications(id));
    }

    @GetMapping("/{customerId}/accounts/{accountId}/regular-transfer-orders")
    public ResponseEntity<List<RegularTransferOrderDto>> getRegularTransfers(
            @PathVariable("customerId") Integer customerId,
            @PathVariable("accountId") Integer accountId) {
        return ResponseEntity.ok(customerService.getRegularTransferOrdersOfCustomer(customerId, accountId));
    }
}
