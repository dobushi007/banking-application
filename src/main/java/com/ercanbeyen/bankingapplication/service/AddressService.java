package com.ercanbeyen.bankingapplication.service;

import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.AddressDto;
import com.ercanbeyen.bankingapplication.entity.Address;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.mapper.AddressMapper;
import com.ercanbeyen.bankingapplication.repository.AddressRepository;
import com.ercanbeyen.bankingapplication.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService implements BaseService<AddressDto> {
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    public List<AddressDto> getEntities() {
        log.info(LogMessages.ECHO_MESSAGE, "addressService", "getEntities");
        List<AddressDto> addressDtoList = new ArrayList<>();

        addressRepository.findAll()
                .forEach(address -> addressDtoList.add(addressMapper.addressToDto(address)));

        return addressDtoList;
    }

    @Override
    public Optional<AddressDto> getEntity(Integer id) {
        log.info(LogMessages.ECHO_MESSAGE, "addressService", "getEntity");
        Optional<Address> addressOptional = addressRepository.findById(id);
        return addressOptional.map(addressMapper::addressToDto);
    }

    @Override
    public AddressDto createEntity(AddressDto request) {
        log.info(LogMessages.ECHO_MESSAGE, "addressService", "createEntity");
        Address address = createAddress(request);
        return addressMapper.addressToDto(address);
    }

    @Override
    public AddressDto updateEntity(Integer id, AddressDto request) {
        log.info(LogMessages.ECHO_MESSAGE, "addressService", "updateEntity");
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessages.NOT_FOUND));

        address.setCity(request.getCity());
        address.setPostCode(request.getPostCode());
        address.setDetails(request.getDetails());

        return addressMapper.addressToDto(addressRepository.save(address));
    }

    @Override
    public void deleteEntity(Integer id) {
        log.info(LogMessages.ECHO_MESSAGE, "addressService", "deleteEntity");
        customerRepository.deleteById(id);
    }

    public Address createAddress(AddressDto addressDto) {
        Address address = addressMapper.dtoToAddress(addressDto);
        return addressRepository.save(address);
    }
}
