package com.bwc.message.v1.sender.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MtsMessageSyncJob implements Job {
	private final ScheduleService scheduleService;

	public MtsMessageSyncJob(ScheduleService scheduleService) {
		this.scheduleService = scheduleService;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		scheduleService.executeJob(context, "mts");
	}
}
