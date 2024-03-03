package com.ercanbeyen.bankingapplication.service;

import com.ercanbeyen.bankingapplication.constant.enums.NewsType;
import com.ercanbeyen.bankingapplication.dto.NewsDto;

import java.util.List;

public interface NewsService {
    List<NewsDto> getNews(NewsType type, int pageNumber, int pageSize);
}
