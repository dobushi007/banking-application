package com.ercanbeyen.bankingapplication.dto.response;

import java.util.Map;

public record RatingStatisticsResponse<T, V>(Map<T, V> response) {

}
