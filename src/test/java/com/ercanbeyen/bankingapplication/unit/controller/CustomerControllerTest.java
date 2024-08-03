package com.ercanbeyen.bankingapplication.unit.controller;

import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.controller.CustomerController;
import com.ercanbeyen.bankingapplication.dto.CustomerDto;
import com.ercanbeyen.bankingapplication.factory.MockCustomerFactory;
import com.ercanbeyen.bankingapplication.option.CustomerFilteringOptions;
import com.ercanbeyen.bankingapplication.service.impl.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {
    @InjectMocks
    private CustomerController customerController;
    @Mock
    private CustomerService customerService;

    public static final String TESTED_CLASS = "Customer Controller";
    private List<CustomerDto> customerDtos;

    @BeforeEach
    void start() {
      log.info(LogMessages.Test.START, LogMessages.Test.UNIT, TESTED_CLASS);
    }

    @AfterAll
    static void end() {
        log.info(LogMessages.Test.END, LogMessages.Test.UNIT, TESTED_CLASS);
    }

    @BeforeEach
    void setUp() {
        log.info(LogMessages.Test.SETUP);
        customerDtos = MockCustomerFactory.generateMockCustomerDtos();
    }

    @AfterEach
    void tearDown() {
        log.info(LogMessages.Test.TEAR_DOWN);
    }

    @Test
    @DisplayName("Happy path test: Get customers case")
    void givenFilteringOptions_whenGetEntity_thenReturnCustomerDtos() {
        // given
        CustomerFilteringOptions filteringOptions = new CustomerFilteringOptions();
        filteringOptions.setBirthDate(LocalDate.of(2005, 8, 15));

        doReturn(customerDtos)
                .when(customerService)
                .getEntities(any());

        // when
        ResponseEntity<List<CustomerDto>> responseEntity = customerController.getEntities(filteringOptions);

        verify(customerService, times(1)).getEntities(any());

        assumeFalse(responseEntity.getBody() == null);
        assertEquals(customerDtos.size(), responseEntity.getBody().size());

    }

    @Test
    @DisplayName("Happy path test: Get Customer case")
    void givenId_whenGetCustomer_thenReturnCustomerDto() {
        // given
        doReturn(customerDtos.getFirst())
                .when(customerService)
                .getEntity(any());

        // when
        ResponseEntity<CustomerDto> responseEntity = customerController.getEntity(1);

        // then
        verify(customerService, times(1)).getEntity(any());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Happy path test: Create customer case")
    void givenCustomerDto_whenCreateEntity_thenReturnCustomerDto() {
        // given
        doReturn(customerDtos.getFirst())
                .when(customerService)
                .createEntity(any());

        // when
        ResponseEntity<CustomerDto> responseEntity = customerController.createEntity(MockCustomerFactory.generateCustomerDtoRequests().getFirst());

        // then
        verify(customerService, times(1)).createEntity(any());

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Happy path test: Update customer case")
    void givenIdAndCustomerDto_whenUpdateEntity_thenReturnCustomerDto() {
        // given
        String email = "test_updated@email.com";
        CustomerDto request = getUpdateMockCustomerDtoRequest(email);

        doReturn(request)
                .when(customerService)
                .updateEntity(anyInt(), any());

        // when
        ResponseEntity<CustomerDto> responseEntity = customerController.updateEntity(1, request);

        // then
        verify(customerService, times(1)).updateEntity(anyInt(), any());

        assumeFalse(responseEntity.getBody() == null);
        assertEquals(email, responseEntity.getBody().getEmail());
    }

    @Test
    @DisplayName("Happy path test: Delete customer case")
    void givenExistingId_whenDeleteEntity_thenReturnNothing() {
        // given
        doNothing()
                .when(customerService)
                .deleteEntity(anyInt());

        // when
        ResponseEntity<Void> responseEntity = customerController.deleteEntity(1);

        // then
        verify(customerService, times(1)).deleteEntity(anyInt());

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    private CustomerDto getUpdateMockCustomerDtoRequest(String email) {
        CustomerDto request = customerDtos.getFirst();
        request.setEmail(email);
        return request;
    }
}
