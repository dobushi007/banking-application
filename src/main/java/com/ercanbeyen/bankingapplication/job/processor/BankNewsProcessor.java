package com.ercanbeyen.bankingapplication.job.processor;

import com.ercanbeyen.bankingapplication.entity.BankNews;
import com.ercanbeyen.bankingapplication.entity.NewsReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BankNewsProcessor implements ItemProcessor<NewsReport, BankNews> {

    @Override
    public BankNews process(NewsReport newsReport) throws Exception {
        log.info("Processing bank news...{}", newsReport);
        BankNews bankNews = new BankNews();
        bankNews.setTitle(newsReport.getTitle());
        bankNews.setUrl(newsReport.getUrl());
        return bankNews;
    }
}
