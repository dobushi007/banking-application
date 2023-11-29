package com.ercanbeyen.bankingapplication.controller;

import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.dto.CustomerDto;
import com.ercanbeyen.bankingapplication.entity.File;
import com.ercanbeyen.bankingapplication.response.MessageResponse;
import com.ercanbeyen.bankingapplication.service.impl.CustomerService;
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
    public ResponseEntity<?> uploadPhoto(@PathVariable("id") Integer id, @RequestParam("file") MultipartFile file) {
        log.info(LogMessages.ECHO_MESSAGE, "CustomerController", "uploadPhoto");
        String message = "";
        HttpStatus httpStatus;
        MessageResponse response;

        try {
            message = customerService.uploadPhoto(id, file);
            httpStatus = HttpStatus.OK;
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
        } catch (Exception exception) {
            httpStatus = HttpStatus.EXPECTATION_FAILED;
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
        } finally {
            log.info("Upload operation is over");
            response = new MessageResponse(message);
        }

        return new ResponseEntity<>(response, httpStatus);
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<?> downloadPhoto(@PathVariable("id") Integer id) {
        log.info(LogMessages.ECHO_MESSAGE, "CustomerController", "downloadPhoto");
        File file = customerService.downloadPhoto(id);
        String fileName = file.getName();
        log.info("file.getName(): {}", fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(file.getData());
    }
}
