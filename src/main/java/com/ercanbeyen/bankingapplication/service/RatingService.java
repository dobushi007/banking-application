package com.ercanbeyen.bankingapplication.service;

import com.ercanbeyen.bankingapplication.constant.enums.RatingReason;
import com.ercanbeyen.bankingapplication.dto.RatingDto;
import com.ercanbeyen.bankingapplication.dto.response.RatingStatisticsResponse;

import java.util.List;
import java.util.UUID;

public interface RatingService {
    List<RatingDto> getRatings();
    RatingDto getRating(UUID id);
    RatingDto createRating(RatingDto ratingDto);
    RatingDto updateRating(UUID id, RatingDto ratingDto);
    RatingStatisticsResponse<RatingReason, Integer> getReasonStatistics(Integer fromYear, Integer toYear, Integer minimumFrequency);
    RatingStatisticsResponse<Integer, Integer> getRateStatistics(Integer fromYear, Integer toYear, Integer minimumFrequency);
}
