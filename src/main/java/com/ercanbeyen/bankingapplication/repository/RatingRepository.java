package com.ercanbeyen.bankingapplication.repository;

import com.ercanbeyen.bankingapplication.entity.Rating;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepository extends CassandraRepository<Rating, UUID> {
    @AllowFiltering
    Optional<Rating> findByYearAndUserNationalId(Integer year, String userNationalId);
}
