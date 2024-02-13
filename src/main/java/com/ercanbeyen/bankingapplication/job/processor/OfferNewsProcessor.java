package com.ercanbeyen.bankingapplication.job.processor;

import com.ercanbeyen.bankingapplication.entity.NewsReport;
import com.ercanbeyen.bankingapplication.entity.OfferNews;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OfferNewsProcessor implements ItemProcessor<NewsReport, OfferNews> {

    @Override
    public OfferNews process(NewsReport newsReport) throws Exception {
        log.info("Processing offer news...{}", newsReport);
        OfferNews offerNews = new OfferNews();
        offerNews.setTitle(newsReport.getTitle());
        offerNews.setUrl(newsReport.getUrl());
        return offerNews;
    }
}
