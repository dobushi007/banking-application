package com.ercanbeyen.bankingapplication.controller;

import com.ercanbeyen.bankingapplication.dto.AddressDto;
import com.ercanbeyen.bankingapplication.service.impl.AddressService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController extends BaseController<AddressDto> {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        super(addressService);
        this.addressService = addressService;
    }

}
