package com.kapture.howisthemood.dto;

import lombok.Data;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Data
public class EventCompletionJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // notify by web socket
    }

}
