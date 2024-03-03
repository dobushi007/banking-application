package com.ercanbeyen.bankingapplication.listener;

import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
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
        log.info(LogMessages.Batch.STEP_STATUS, stepExecution.getStepName(), "started", stepExecution.getStartTime());
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution){
        log.info(LogMessages.Batch.STEP_STATUS, stepExecution.getStepName(), "ended", stepExecution.getEndTime());
        return (stepExecution.getExitStatus() == ExitStatus.COMPLETED) ? ExitStatus.COMPLETED : ExitStatus.FAILED;
    }
}
