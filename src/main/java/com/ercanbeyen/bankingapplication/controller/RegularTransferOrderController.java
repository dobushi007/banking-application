package com.ercanbeyen.bankingapplication.controller;

import com.ercanbeyen.bankingapplication.dto.RegularTransferOrderDto;
import com.ercanbeyen.bankingapplication.option.RegularTransferOrderOptions;
import com.ercanbeyen.bankingapplication.service.impl.RegularTransferOrderService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/regular-transfer-orders")
public class RegularTransferOrderController extends BaseController<RegularTransferOrderDto, RegularTransferOrderOptions> {
    private final RegularTransferOrderService regularTransferOrderService;

    public RegularTransferOrderController(RegularTransferOrderService regularTransferOrderService) {
        super(regularTransferOrderService);
        this.regularTransferOrderService = regularTransferOrderService;
    }


}
