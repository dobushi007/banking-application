package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.enums.Entity;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.RatingDto;
import com.ercanbeyen.bankingapplication.entity.Rating;
import com.ercanbeyen.bankingapplication.exception.ResourceExpectationFailedException;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.mapper.RatingMapper;
import com.ercanbeyen.bankingapplication.repository.RatingRepository;
import com.ercanbeyen.bankingapplication.service.RatingService;
import com.ercanbeyen.bankingapplication.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final CustomerService customerService;

    @Override
    public List<RatingDto> getRatings() {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        List<RatingDto> ratingDtos = new ArrayList<>();

        ratingRepository.findAll()
                .forEach(rating -> ratingDtos.add(ratingMapper.ratingToDto(rating)));

        return ratingDtos;

    }

    @Override
    public RatingDto getRating(UUID id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Rating rating = findRatingById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.RATING.getValue());

        return ratingMapper.ratingToDto(rating);
    }

    @Override
    public RatingDto createRating(RatingDto ratingDto) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        checkRatingBeforeCreate(ratingDto);

        Rating rating = ratingMapper.dtoToRating(ratingDto);
        rating.setId(UUID.randomUUID());
        rating.setExplanation(ratingDto.explanation());
        LocalDateTime now = LocalDateTime.now();
        rating.setCreatedAt(now);
        rating.setUpdatedAt(now);
        rating.setYear(now.getYear());

        log.info(LogMessages.RESOURCE_FOUND, Entity.CUSTOMER.getValue());

        Rating savedRating = ratingRepository.save(rating);
        log.info(LogMessages.RESOURCE_CREATE_SUCCESS, Entity.RATING.getValue(), savedRating.getId());

        return ratingMapper.ratingToDto(savedRating);
    }

    @Override
    public RatingDto updateRating(UUID id, RatingDto ratingDto) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Rating rating = findRatingById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.RATING.getValue());

        rating.setRate(ratingDto.rate());
        rating.setReason(ratingDto.reason());
        rating.setExplanation(ratingDto.explanation());
        rating.setUpdatedAt(LocalDateTime.now());

        Rating savedRating = ratingRepository.save(rating);
        log.info(LogMessages.RESOURCE_CREATE_SUCCESS, Entity.RATING.getValue(), savedRating.getId());

        return ratingMapper.ratingToDto(savedRating);
    }

    private Rating findRatingById(UUID id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.RATING.getValue())));
    }

    private void checkRatingBeforeCreate(RatingDto ratingDto) {
        if (!customerService.doesCustomerExist(ratingDto.userNationalId())) {
            log.error(LogMessages.RESOURCE_NOT_FOUND, Entity.CUSTOMER.getValue());
            throw new ResourceExpectationFailedException("User national id is not in database");
        }

        int currentYear = LocalDateTime.now().getYear();

        if (ratingRepository.findByYearAndUserNationalId(currentYear, ratingDto.userNationalId()).isPresent()) {
            log.error(LogMessages.RESOURCE_FOUND, Entity.RATING.getValue());
            throw new ResourceExpectationFailedException(String.format("Customer is already rated in %d", currentYear));
        }
    }
}
