package com.ercanbeyen.bankingapplication.repository;

import com.ercanbeyen.bankingapplication.constant.enums.NewsType;
import com.ercanbeyen.bankingapplication.entity.NewsReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsReportRepository extends BaseRepository<NewsReport> {
    Page<NewsReport> findByType(NewsType type, Pageable pageable);
}
