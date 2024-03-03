package com.ercanbeyen.bankingapplication.job;

import com.ercanbeyen.bankingapplication.entity.BankNews;
import com.ercanbeyen.bankingapplication.entity.NewsReport;
import com.ercanbeyen.bankingapplication.entity.OfferNews;
import com.ercanbeyen.bankingapplication.repository.BankNewsRepository;
import com.ercanbeyen.bankingapplication.repository.NewsReportRepository;
import com.ercanbeyen.bankingapplication.repository.OfferNewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Writer {
    private final NewsReportRepository newsReportRepository;
    private final BankNewsRepository bankNewsRepository;
    private final OfferNewsRepository offerNewsRepository;
    private static final String SAVE_METHOD = "save";

    @Bean(name = "writerNewsReportTable")
    public RepositoryItemWriter<NewsReport> writerNewsReportTable() {
        RepositoryItemWriter<NewsReport> writer = new RepositoryItemWriter<>();
        writer.setRepository(newsReportRepository);
        writer.setMethodName(SAVE_METHOD);
        return writer;
    }

    @Bean(name = "writerBankNewsTable")
    public RepositoryItemWriter<BankNews> writerBankNewsTable() {
        RepositoryItemWriter<BankNews> writer = new RepositoryItemWriter<>();
        writer.setRepository(bankNewsRepository);
        writer.setMethodName(SAVE_METHOD);
        return writer;
    }

    @Bean(name = "writerOfferNewsTable")
    public RepositoryItemWriter<OfferNews> writerOfferNewsTable() {
        RepositoryItemWriter<OfferNews> writer = new RepositoryItemWriter<>();
        writer.setRepository(offerNewsRepository);
        writer.setMethodName(SAVE_METHOD);
        return writer;
    }
}
