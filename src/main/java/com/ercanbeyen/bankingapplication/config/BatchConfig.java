package com.ercanbeyen.bankingapplication.config;

import com.ercanbeyen.bankingapplication.entity.BankNews;
import com.ercanbeyen.bankingapplication.entity.NewsReport;
import com.ercanbeyen.bankingapplication.entity.OfferNews;
import com.ercanbeyen.bankingapplication.job.processor.BankNewsProcessor;
import com.ercanbeyen.bankingapplication.job.processor.OfferNewsProcessor;
import com.ercanbeyen.bankingapplication.listener.JobCompletionNotificationListener;
import com.ercanbeyen.bankingapplication.listener.StepCompletionNotificationListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
//@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Step stepNewsReport(@Qualifier("readerNewsReportCSVFile") ItemReader<NewsReport> itemReader, @Qualifier("writerNewsReportTable") ItemWriter<NewsReport> itemWriter, StepCompletionNotificationListener listener) {
        return new StepBuilder("stepNewsReport", jobRepository)
                .<NewsReport, NewsReport>chunk(50, transactionManager)
                .listener(listener)
                .reader(itemReader)
                .writer(itemWriter)
                .build();
    }

    @Bean
    public Step stepBankNews(@Qualifier("readerNewsReportByBankNewsType") ItemReader<NewsReport> itemReader, @Qualifier("writerBankNewsTable") ItemWriter<BankNews> itemWriter, StepCompletionNotificationListener listener) {
        return new StepBuilder("stepBankNews", jobRepository)
                .<NewsReport, BankNews>chunk(50, transactionManager)
                .listener(listener)
                .reader(itemReader)
                .processor(bankNewsProcessor())
                .writer(itemWriter)
                .build();
    }

    @Bean
    public Step stepOfferNews(@Qualifier("readerNewsReportByOfferNewsType") ItemReader<NewsReport> itemReader, @Qualifier("writerOfferNewsTable") ItemWriter<OfferNews> itemWriter, StepCompletionNotificationListener listener) {
        return new StepBuilder("stepOfferNews", jobRepository)
                .<NewsReport, OfferNews>chunk(50, transactionManager)
                .listener(listener)
                .reader(itemReader)
                .processor(offerNewsProcessor())
                .writer(itemWriter)
                .build();
    }

    @Bean
    public BankNewsProcessor bankNewsProcessor() {
        return new BankNewsProcessor();
    }

    @Bean
    public OfferNewsProcessor offerNewsProcessor() {
        return new OfferNewsProcessor();
    }

    @Bean
    public Job job(JobCompletionNotificationListener listener, @Qualifier("stepNewsReport") Step stepNewsReport, @Qualifier("stepBankNews") Step stepBankNews, @Qualifier("stepOfferNews") Step stepOfferNews) {
        return new JobBuilder("job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(stepNewsReport)
                .next(stepBankNews)
                .next(stepOfferNews)
                .build();
    }
}
