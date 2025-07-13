package com.bwc.message.v1.sender.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LguV1MessageSyncJob implements Job {

	private final ScheduleService scheduleService;

	public LguV1MessageSyncJob(ScheduleService scheduleService) {
		this.scheduleService = scheduleService;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		scheduleService.executeJob(context, "lguV1");
	}
}
