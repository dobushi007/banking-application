package com.ercanbeyen.bankingapplication.response;

import com.ercanbeyen.bankingapplication.constant.enums.City;

public record StatisticsResponse(String customerNationalId, String fullName, Integer accountId, City city, Double balance) {

}
