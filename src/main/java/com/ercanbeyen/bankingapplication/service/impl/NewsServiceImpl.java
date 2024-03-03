package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.enums.NewsType;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.dto.NewsDto;
import com.ercanbeyen.bankingapplication.entity.News;
import com.ercanbeyen.bankingapplication.exception.ResourceExpectationFailedException;
import com.ercanbeyen.bankingapplication.mapper.NewsMapper;
import com.ercanbeyen.bankingapplication.repository.BankNewsRepository;
import com.ercanbeyen.bankingapplication.repository.OfferNewsRepository;
import com.ercanbeyen.bankingapplication.service.NewsService;
import com.ercanbeyen.bankingapplication.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsServiceImpl implements NewsService {
    private final BankNewsRepository bankNewsRepository;
    private final OfferNewsRepository offerNewsRepository;
    private final NewsMapper newsMapper;
    @Override
    public List<NewsDto> getNews(NewsType type, int pageNumber, int pageSize) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        List<NewsDto> newsDtoList = new ArrayList<>();

        Sort newsSort = Sort.by("createTime", "updateTime").descending()
                .and(Sort.by("title").ascending());

        Pageable pageable = PageRequest.of(pageNumber, pageSize, newsSort);

        switch (type) {
            case BANK_NEWS -> bankNewsRepository.findAll(pageable)
                    .stream()
                    .map(News.class::cast)
                    .forEach(news -> newsDtoList.add(newsMapper.newsToDto(news)));
            case OFFER_NEWS -> offerNewsRepository.findAll(pageable)
                    .stream()
                    .map(News.class::cast)
                    .forEach(news -> newsDtoList.add(newsMapper.newsToDto(news)));
            case null, default -> throw new ResourceExpectationFailedException("Invalid news type");
        }

        return newsDtoList;
    }
}
