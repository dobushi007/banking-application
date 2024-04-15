package com.ercanbeyen.bankingapplication.service;

import com.ercanbeyen.bankingapplication.dto.RatingDto;

import java.util.List;
import java.util.UUID;

public interface RatingService {
    List<RatingDto> getRatings();
    RatingDto getRating(UUID id);
    RatingDto createRating(RatingDto ratingDto);
    RatingDto updateRating(UUID id, RatingDto ratingDto);
}
