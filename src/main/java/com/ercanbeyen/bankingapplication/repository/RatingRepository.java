package com.ercanbeyen.bankingapplication.repository;

import com.ercanbeyen.bankingapplication.entity.Rating;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepository extends CassandraRepository<Rating, UUID> {
    @AllowFiltering
    Optional<Rating> findByYearAndUserNationalId(int year, String userNationalId);
    @AllowFiltering
    List<Rating> findByYear(int year);
    @AllowFiltering
    List<Rating> findByYearGreaterThanEqual(int year);
    @AllowFiltering
    List<Rating> findByYearLessThanEqual(int year);
    @Query(value = """
           SELECT *
           FROM ratings
           WHERE year >= :fromYear AND year <= :toYear
           ALLOW FILTERING
          """)
    List<Rating> findByYearBetweenEquals(@Param("fromYear") int fromYear, @Param("toYear") int toYear);
}
