package com.ercanbeyen.bankingapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseRepository<T> extends JpaRepository<T, Integer> {

}
