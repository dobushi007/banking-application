package com.ercanbeyen.bankingapplication.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepListener;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StepCompletionNotificationListener implements StepListener {

    @BeforeStep
    public void beforeStep(StepExecution stepExecution){
        log.info("Step {} is started time {}", stepExecution.getStepName(), stepExecution.getStartTime());
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution){
        log.info("Step {} is ended time {}", stepExecution.getStepName(), stepExecution.getEndTime());
        return (stepExecution.getExitStatus() == ExitStatus.COMPLETED) ? ExitStatus.COMPLETED : ExitStatus.FAILED;
    }
}
