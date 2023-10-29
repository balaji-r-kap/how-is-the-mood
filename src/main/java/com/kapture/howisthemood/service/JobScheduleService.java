package com.kapture.howisthemood.service;

import com.kapture.howisthemood.dto.EventCompletionJob;
import com.kapture.howisthemood.model.InternalEvent;
import com.kapture.howisthemood.repository.InternalEventRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JobScheduleService {

    private final InternalEventRepository internalEventRepository;
    private final Scheduler               scheduler;

    public void scheduleJobs() {
        List<InternalEvent> eventsToSchedule = internalEventRepository.findAllByJobScheduled(false);
        eventsToSchedule.forEach(event -> {
            // remove starttime updated event
            // schedule
            try {
                JobDetail jobDetail = buildJobDetail(event);
                Trigger trigger = buildTrigger(jobDetail, event.getEndTime());
                scheduler.scheduleJob(jobDetail, trigger);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        });
    }

    private JobDetail buildJobDetail(InternalEvent event) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("attendees", event.getAttendees());
        jobDataMap.put("event", Map.of("description", event.getDescription()));
        jobDataMap.put("startTime", event.getStartTime());
        jobDataMap.put("endTime", event.getEndTime());

        return JobBuilder.newJob(EventCompletionJob.class)
                .withIdentity(UUID.randomUUID().toString(), "event-completion-job")
                .withDescription("Sent event completion job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildTrigger(JobDetail jobDetail, Timestamp startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "event-completion-trigger")
                .withDescription("Sent event completion trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }

}
