package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.enums.Entity;
import com.ercanbeyen.bankingapplication.constant.enums.RatingReason;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.RatingDto;
import com.ercanbeyen.bankingapplication.dto.response.RatingStatisticsResponse;
import com.ercanbeyen.bankingapplication.entity.Rating;
import com.ercanbeyen.bankingapplication.exception.ResourceExpectationFailedException;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.mapper.RatingMapper;
import com.ercanbeyen.bankingapplication.repository.RatingRepository;
import com.ercanbeyen.bankingapplication.service.RatingService;
import com.ercanbeyen.bankingapplication.util.LoggingUtils;
import com.ercanbeyen.bankingapplication.util.StatisticsUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final CustomerService customerService;

    @Override
    public List<RatingDto> getRatings() {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(), LoggingUtils.getCurrentMethodName());

        List<RatingDto> ratingDtos = new ArrayList<>();

        ratingRepository.findAll()
                .forEach(rating -> ratingDtos.add(ratingMapper.entityToDto(rating)));

        return ratingDtos;

    }

    @Override
    public RatingDto getRating(UUID id) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(), LoggingUtils.getCurrentMethodName());

        Rating rating = findById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.RATING.getValue());

        return ratingMapper.entityToDto(rating);
    }

    @Override
    public RatingDto createRating(RatingDto ratingDto) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(), LoggingUtils.getCurrentMethodName());

        checkRatingBeforeCreate(ratingDto);

        Rating rating = ratingMapper.dtoToEntity(ratingDto);
        rating.setId(UUID.randomUUID());
        rating.setExplanation(ratingDto.explanation());

        LocalDateTime now = LocalDateTime.now();
        rating.setCreatedAt(now);
        rating.setUpdatedAt(now);
        rating.setYear(now.getYear());


        log.info(LogMessages.RESOURCE_FOUND, Entity.CUSTOMER.getValue());

        Rating savedRating = ratingRepository.save(rating);
        log.info(LogMessages.RESOURCE_CREATE_SUCCESS, Entity.RATING.getValue(), savedRating.getId());

        return ratingMapper.entityToDto(savedRating);
    }

    @Override
    public RatingDto updateRating(UUID id, RatingDto ratingDto) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(), LoggingUtils.getCurrentMethodName());

        Rating rating = findById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.RATING.getValue());

        rating.setRate(ratingDto.rate());
        rating.setReason(ratingDto.reason());
        rating.setExplanation(ratingDto.explanation());
        rating.setUpdatedAt(LocalDateTime.now());

        Rating savedRating = ratingRepository.save(rating);
        log.info(LogMessages.RESOURCE_CREATE_SUCCESS, Entity.RATING.getValue(), savedRating.getId());

        return ratingMapper.entityToDto(savedRating);
    }

    @Override
    public RatingStatisticsResponse<RatingReason, Integer> getReasonStatistics(Integer fromYear, Integer toYear, Integer minimumFrequency) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(), LoggingUtils.getCurrentMethodName());

        List<RatingReason> reasons = getRatingsBetweenYears(fromYear, toYear)
                .stream()
                .map(Rating::getReason)
                .toList();

        return new RatingStatisticsResponse<>(StatisticsUtils.getFrequencies(reasons, minimumFrequency));
    }

    @Override
    public RatingStatisticsResponse<Integer, Integer> getRateStatistics(Integer fromYear, Integer toYear, Integer minimumFrequency) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(), LoggingUtils.getCurrentMethodName());

        List<Rating> ratings = getRatingsBetweenYears(fromYear, toYear);
        List<Integer> rates = ratings.stream()
                .map(Rating::getRate)
                .toList();

        return new RatingStatisticsResponse<>(StatisticsUtils.getFrequencies(rates, minimumFrequency));
    }

    private Rating findById(UUID id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.RATING.getValue())));
    }

    private void checkRatingBeforeCreate(RatingDto ratingDto) {
        if (!customerService.existsByNationalId(ratingDto.userNationalId())) {
            log.error(LogMessages.RESOURCE_NOT_FOUND, Entity.CUSTOMER.getValue());
            throw new ResourceExpectationFailedException("User national id is not in database");
        }

        int currentYear = LocalDateTime.now().getYear();

        if (ratingRepository.findByYearAndUserNationalId(currentYear, ratingDto.userNationalId()).isPresent()) {
            log.error(LogMessages.RESOURCE_FOUND, Entity.RATING.getValue());
            throw new ResourceExpectationFailedException(String.format("Customer is already rated in %d", currentYear));
        }
    }

    private List<Rating> getRatingsBetweenYears(Integer fromYear, Integer toYear) {
        List<Rating> ratings;

        if (fromYear != null && toYear != null) {
            ratings = (Objects.equals(fromYear, toYear)) ? ratingRepository.findByYear(fromYear)
                    : ratingRepository.findByYearBetweenEquals(fromYear, toYear);
        } else if (fromYear != null) {
            ratings = ratingRepository.findByYearGreaterThanEqual(fromYear);
        } else if (toYear != null) {
            ratings = ratingRepository.findByYearLessThanEqual(toYear);
        } else {
            ratings = ratingRepository.findAll();
        }

        return ratings;
    }
}
