package com.ercanbeyen.bankingapplication.job;

import com.ercanbeyen.bankingapplication.constant.enums.NewsType;
import com.ercanbeyen.bankingapplication.entity.NewsReport;
import com.ercanbeyen.bankingapplication.repository.NewsReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class Reader {
    private final NewsReportRepository newsReportRepository;

    @Bean(name = "readerNewsReportCSVFile")
    public ItemReader<NewsReport> readerNewsReportCSVFile() {
        return new FlatFileItemReaderBuilder<NewsReport>()
                .name("readerNewsReportCSVFile")
                .resource(new ClassPathResource("/dataset/News.csv"))
                .linesToSkip(1)
                .delimited()
                .names("title", "url", "type")
                .targetType(NewsReport.class)
                .build();
    }

    @Bean(name = "readerNewsReportByBankNewsType")
    public ItemReader<NewsReport> readerNewsReportByBankNewsType() {
        return getNewsReportRepositoryItemReader(NewsType.BANK_NEWS);
    }

    @Bean(name = "readerNewsReportByOfferNewsType")
    public ItemReader<NewsReport> readerNewsReportByOfferNewsType() {
        return getNewsReportRepositoryItemReader(NewsType.OFFER_NEWS);
    }

    private RepositoryItemReader<NewsReport> getNewsReportRepositoryItemReader(NewsType newsType) {
        RepositoryItemReader<NewsReport> reader = new RepositoryItemReader<>();
        reader.setRepository(newsReportRepository);
        reader.setMethodName("findByType");
        List<Object> queryMethodArguments = new ArrayList<>();
        queryMethodArguments.add(newsType);
        reader.setArguments(queryMethodArguments);
        Map<String, Sort.Direction> sorts = new LinkedHashMap<>();
        sorts.put("id", Sort.Direction.ASC);
        reader.setSort(sorts);
        return reader;
    }

}
