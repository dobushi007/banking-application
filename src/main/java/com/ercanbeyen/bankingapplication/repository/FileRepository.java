package com.ercanbeyen.bankingapplication.repository;

import com.ercanbeyen.bankingapplication.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, String> {

}
