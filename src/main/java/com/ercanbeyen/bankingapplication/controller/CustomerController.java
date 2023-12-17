package com.ercanbeyen.bankingapplication.controller;

import com.ercanbeyen.bankingapplication.dto.CustomerDto;
import com.ercanbeyen.bankingapplication.entity.File;
import com.ercanbeyen.bankingapplication.response.MessageResponse;
import com.ercanbeyen.bankingapplication.service.impl.CustomerService;
import com.ercanbeyen.bankingapplication.util.PhotoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/customers")
@Slf4j
public class CustomerController extends BaseController<CustomerDto> {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        super(customerService);
        this.customerService = customerService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> uploadProfilePhoto(@PathVariable("id") Integer id, @RequestParam("file") MultipartFile file) {
        PhotoUtils.checkPhoto(file);
        String message = customerService.uploadProfilePhoto(id, file);
        MessageResponse response = new MessageResponse(message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<?> downloadProfilePhoto(@PathVariable("id") Integer id) {
        File file = customerService.downloadProfilePhoto(id);

        String fileName = file.getName();
        log.info("file.getName() and its length: {} - {}", fileName, fileName.length());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(file.getData());
    }

    @DeleteMapping("{id}/photo")
    public ResponseEntity<?> deleteProfilePhoto(@PathVariable("id") Integer id) {
        String message = customerService.deleteProfilePhoto(id);
        MessageResponse response = new MessageResponse(message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
