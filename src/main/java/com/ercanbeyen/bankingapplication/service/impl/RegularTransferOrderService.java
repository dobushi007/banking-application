package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.enums.Entity;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.RegularTransferOrderDto;
import com.ercanbeyen.bankingapplication.embeddable.RegularTransfer;
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

import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegularTransferOrderService implements BaseService<RegularTransferOrderDto, RegularTransferOrderOptions> {
    private final RegularTransferOrderRepository regularTransferOrderRepository;
    private final RegularTransferOrderMapper regularTransferOrderMapper;
    private final AccountService accountService;

    @Override
    public List<RegularTransferOrderDto> getEntities(RegularTransferOrderOptions options) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(), LoggingUtils.getCurrentMethodName());

        List<RegularTransferOrderDto> regularTransferOrderDtos;

        Predicate<RegularTransferOrder> regularTransferOrderPredicate = regularTransferOrder -> (options.getSenderAccountId() == null || options.getSenderAccountId().equals(regularTransferOrder.getSenderAccount().getId()))
                && (options.getReceiverAccountId() == null || options.getReceiverAccountId().equals(regularTransferOrder.getRegularTransfer().getReceiverAccount().getId())
                && (options.getPeriod() == null || options.getPeriod().equals(regularTransferOrder.getPeriod()))
                && (options.getCreateTime() == null || options.getCreateTime().toLocalDate().isEqual(options.getCreateTime().toLocalDate())));

        regularTransferOrderDtos = regularTransferOrderRepository.findAll()
                .stream()
                .filter(regularTransferOrderPredicate)
                .map(regularTransferOrderMapper::entityToDto)
                .toList();

        return regularTransferOrderDtos;
    }

    @Override
    public RegularTransferOrderDto getEntity(Integer id) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(), LoggingUtils.getCurrentMethodName());
        RegularTransferOrder regularTransferOrder = findById(id);
        return regularTransferOrderMapper.entityToDto(regularTransferOrder);
    }

    @Override
    public RegularTransferOrderDto createEntity(RegularTransferOrderDto request) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(), LoggingUtils.getCurrentMethodName());

        RegularTransferOrder regularTransferOrder = createRegularTransferOrder(request);
        RegularTransferOrder savedRegularTransferOrder = regularTransferOrderRepository.save(regularTransferOrder);
        log.info(LogMessages.RESOURCE_CREATE_SUCCESS, Entity.REGULAR_TRANSFER_ORDER.getValue(), savedRegularTransferOrder.getId());

        return regularTransferOrderMapper.entityToDto(savedRegularTransferOrder);
    }

    @Override
    public RegularTransferOrderDto updateEntity(Integer id, RegularTransferOrderDto request) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(), LoggingUtils.getCurrentMethodName());

        RegularTransferOrder regularTransferOrder = findById(id);

        List<Account> accounts = getAccountsFromRegularTransferDto(request);
        regularTransferOrder.setSenderAccount(accounts.getFirst());

        RegularTransfer regularTransfer = regularTransferOrder.getRegularTransfer();
        regularTransfer.setReceiverAccount(accounts.get(1));
        regularTransfer.setAmount(request.getRegularTransferDto().amount());
        regularTransfer.setExplanation(request.getRegularTransferDto().explanation());

        regularTransferOrder.setPeriod(regularTransferOrder.getPeriod());

        return regularTransferOrderMapper.entityToDto(regularTransferOrderRepository.save(regularTransferOrder));
    }

    @Override
    public void deleteEntity(Integer id) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(), LoggingUtils.getCurrentMethodName());
        RegularTransferOrder regularTransferOrder = findById(id);
        regularTransferOrderRepository.delete(regularTransferOrder);
        log.info(LogMessages.RESOURCE_DELETE_SUCCESS, Entity.REGULAR_TRANSFER_ORDER.getValue(), id);
    }

    private RegularTransferOrder createRegularTransferOrder(RegularTransferOrderDto request) {
        List<Account> accounts = getAccountsFromRegularTransferDto(request);

        RegularTransferOrder regularTransferOrder = new RegularTransferOrder();
        regularTransferOrder.setSenderAccount(accounts.getFirst());

        RegularTransfer regularTransfer = new RegularTransfer(accounts.get(1), request.getRegularTransferDto().amount(), request.getRegularTransferDto().explanation());
        regularTransferOrder.setRegularTransfer(regularTransfer);
        regularTransferOrder.setPeriod(request.getPeriod());

        return regularTransferOrder;
    }

    /***
     *
     * @param request RegularTransferOrderDto object
     * @return list which contains sender and receiver accounts respectively
     */
    private List<Account> getAccountsFromRegularTransferDto(RegularTransferOrderDto request) {
        Account senderAccount = accountService.findById(request.getSenderAccountId());
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        Account receiverAccount = accountService.findById(request.getRegularTransferDto().receiverAccountId());
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        return List.of(senderAccount, receiverAccount);
    }

    private RegularTransferOrder findById(Integer id) {
        String value = Entity.REGULAR_TRANSFER_ORDER.getValue();
        RegularTransferOrder regularTransferOrder = regularTransferOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, value)));

        log.info(LogMessages.RESOURCE_FOUND, value);

        return regularTransferOrder;
    }
}
