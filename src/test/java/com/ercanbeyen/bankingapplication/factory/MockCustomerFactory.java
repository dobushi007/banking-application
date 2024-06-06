package com.ercanbeyen.bankingapplication.factory;

import com.ercanbeyen.bankingapplication.constant.enums.Gender;
import com.ercanbeyen.bankingapplication.dto.CustomerDto;
import com.ercanbeyen.bankingapplication.entity.Customer;

import java.time.LocalDate;
import java.util.List;

public class MockCustomerFactory {
    public static List<Customer> generateMockCustomers() {
        int id = 1;

        Customer customer1 = new Customer();
        customer1.setId(id);
        customer1.setName("Test-Name1");
        customer1.setSurname("Test-Surname1");
        customer1.setNationalId("12345678911");
        customer1.setEmail("test1@email.com");
        customer1.setPhoneNumber("+905328465701");
        customer1.setGender(Gender.MALE);
        customer1.setBirthDate(LocalDate.of(2005, 8, 15));

        id++;

        Customer customer2 = new Customer();
        customer2.setId(id);
        customer2.setName("Test-Name2");
        customer2.setSurname("Test-Surname2");
        customer2.setNationalId("12345678912");
        customer2.setEmail("test2@email.com");
        customer2.setPhoneNumber("+905328465702");
        customer2.setGender(Gender.FEMALE);
        customer2.setBirthDate(LocalDate.of(2007, 4, 6));

        id++;

        Customer customer3 = new Customer();
        customer3.setId(id);
        customer3.setName("Test-Name3");
        customer3.setSurname("Test-Surname3");
        customer3.setNationalId("12345678913");
        customer3.setEmail("test3@email.com");
        customer3.setPhoneNumber("+905328465702");
        customer3.setGender(Gender.FEMALE);
        customer3.setBirthDate(LocalDate.of(2003, 2, 20));

        return List.of(customer1, customer2, customer3);
    }

    public static List<CustomerDto> generateMockCustomerDtos() {
        int id = 1;

        CustomerDto customerDto1 = new CustomerDto();
        customerDto1.setId(id);
        customerDto1.setName("Test-Name1");
        customerDto1.setSurname("Test-Surname1");
        customerDto1.setNationalId("12345678911");
        customerDto1.setEmail("test1@email.com");
        customerDto1.setPhoneNumber("+905328465701");
        customerDto1.setGender(Gender.MALE);
        customerDto1.setBirthDate(LocalDate.of(2005, 8, 15));

        id++;

        CustomerDto customerDto2 = new CustomerDto();
        customerDto2.setId(id);
        customerDto2.setName("Test-Name2");
        customerDto2.setSurname("Test-Surname2");
        customerDto2.setNationalId("12345678912");
        customerDto2.setEmail("test2@email.com");
        customerDto2.setPhoneNumber("+905328465702");
        customerDto2.setGender(Gender.FEMALE);
        customerDto2.setBirthDate(LocalDate.of(2007, 4, 6));

        id++;

        CustomerDto customerDto3 = new CustomerDto();
        customerDto3.setId(id);
        customerDto3.setName("Test-Name3");
        customerDto3.setSurname("Test-Surname3");
        customerDto3.setNationalId("12345678913");
        customerDto3.setEmail("test3@email.com");
        customerDto3.setPhoneNumber("+905328465703");
        customerDto3.setGender(Gender.FEMALE);
        customerDto3.setBirthDate(LocalDate.of(2003, 2, 20));

        return List.of(customerDto1, customerDto2, customerDto3);
    }

    public static List<CustomerDto> generateCustomerDtoRequests() {
        return generateMockCustomerDtos();
    }
}
