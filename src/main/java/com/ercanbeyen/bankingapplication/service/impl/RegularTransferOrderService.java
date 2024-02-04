package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.enums.Entity;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.RegularTransferOrderDto;
import com.ercanbeyen.bankingapplication.entity.Account;
import com.ercanbeyen.bankingapplication.entity.RegularTransferOrder;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.mapper.RegularTransferOrderMapper;
import com.ercanbeyen.bankingapplication.option.RegularTransferOrderOptions;
import com.ercanbeyen.bankingapplication.repository.RegularTransferOrderRepository;
import com.ercanbeyen.bankingapplication.service.BaseService;
import com.ercanbeyen.bankingapplication.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegularTransferOrderService implements BaseService<RegularTransferOrderDto, RegularTransferOrderOptions> {
    private final RegularTransferOrderRepository regularTransferOrderRepository;
    private final RegularTransferOrderMapper regularTransferOrderMapper;
    private final AccountService accountService;

    @Override
    public List<RegularTransferOrderDto> getEntities(RegularTransferOrderOptions options) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        List<RegularTransferOrderDto> regularTransferOrderDtos = new ArrayList<>();

        regularTransferOrderRepository.findAll()
                .forEach(regularMoneyTransferOrder -> regularTransferOrderDtos.add(regularTransferOrderMapper.regularTransferOrderToDto(regularMoneyTransferOrder)));

        return regularTransferOrderDtos;
    }

    @Override
    public Optional<RegularTransferOrderDto> getEntity(Integer id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Optional<RegularTransferOrder> regularMoneyTransferOrderOptional = regularTransferOrderRepository.findById(id);
        return regularMoneyTransferOrderOptional.map(regularTransferOrderMapper::regularTransferOrderToDto);
    }

    @Override
    public RegularTransferOrderDto createEntity(RegularTransferOrderDto request) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Account account = checkReceiverAccountAndGetSenderAccount(request);

        RegularTransferOrder regularTransferOrder = new RegularTransferOrder();
        regularTransferOrder.setAccount(account);
        regularTransferOrder.setRegularTransfer(request.getRegularTransfer());
        regularTransferOrder.setPeriod(request.getPeriod());

        RegularTransferOrder savedRegularTransferOrder = regularTransferOrderRepository.save(regularTransferOrder);
        log.info(LogMessages.RESOURCE_CREATE_SUCCESS, Entity.REGULAR_TRANSFER_ORDER.getValue());

        return regularTransferOrderMapper.regularTransferOrderToDto(savedRegularTransferOrder);
    }

    @Override
    public RegularTransferOrderDto updateEntity(Integer id, RegularTransferOrderDto request) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        RegularTransferOrder regularTransferOrder = findRegularTransferOrderById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.REGULAR_TRANSFER_ORDER);

        Account account = checkReceiverAccountAndGetSenderAccount(request);
        regularTransferOrder.setAccount(account);
        regularTransferOrder.setRegularTransfer(request.getRegularTransfer());
        regularTransferOrder.setPeriod(regularTransferOrder.getPeriod());

        return regularTransferOrderMapper.regularTransferOrderToDto(regularTransferOrderRepository.save(regularTransferOrder));
    }

    @Override
    public void deleteEntity(Integer id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        RegularTransferOrder regularTransferOrder = findRegularTransferOrderById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.REGULAR_TRANSFER_ORDER);

        regularTransferOrderRepository.delete(regularTransferOrder);
    }

    private Account checkReceiverAccountAndGetSenderAccount(RegularTransferOrderDto request) {
        Account account = accountService.findAccount(request.getSenderAccountId());
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        if (!accountService.doesAccountExist(request.getRegularTransfer().getReceiverAccountId())) {
            throw new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.ACCOUNT.getValue()));
        }

        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        return account;
    }

    private RegularTransferOrder findRegularTransferOrderById(Integer id) {
        return regularTransferOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.REGULAR_TRANSFER_ORDER)));
    }
}
