package com.ercanbeyen.bankingapplication.dto.response;

import com.ercanbeyen.bankingapplication.constant.enums.City;

public record CustomerStatisticsResponse(String customerNationalId, String fullName, Integer accountId, City city, Double balance) {

}
