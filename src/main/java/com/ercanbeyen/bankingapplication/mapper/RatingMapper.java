package com.ercanbeyen.bankingapplication.mapper;

import com.ercanbeyen.bankingapplication.dto.RatingDto;
import com.ercanbeyen.bankingapplication.entity.Rating;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RatingMapper {
    RatingDto entityToDto(Rating rating);
    Rating dtoToEntity(RatingDto ratingDto);
}
