package com.bwc.config.scheduler;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

// import com.bwc.message.v1.sender.jobs.LguV1MessageSyncJob; // 삭제된 클래스
// import com.bwc.message.v1.sender.jobs.LguV2MessageSyncJob; // 삭제된 클래스
// import com.bwc.message.v1.sender.jobs.MtsMessageSyncJob; // 삭제된 클래스

// @Configuration // TODO: 새로운 통합 메시징 구조에서 스케줄러 재구현 필요
public class SchedulerConfig {
	@Bean
	public AutowiringSpringBeanJobFactory autowiringSpringBeanJobFactory(AutowireCapableBeanFactory beanFactory) {
		return new AutowiringSpringBeanJobFactory(beanFactory);
	}

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean(AutowiringSpringBeanJobFactory jobFactory) {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

		// Job Factory Bean 설정
		schedulerFactoryBean.setJobFactory(jobFactory);

		// Job Detail Bean 설정 (현재 비활성화)
		// schedulerFactoryBean.setJobDetails();
		return schedulerFactoryBean;
	}

	// TODO: 새로운 구조에서 필요시 스케줄러 Job 재구현
	/*
	@Bean
	public JobDetail mtsMessageSyncJobDetail() {
		return JobBuilder.newJob(MtsMessageSyncJob.class)
			.withIdentity("MtsMessageSyncJob")
			.storeDurably()
			.build();
	}

	@Bean
	public JobDetail lguV1MessageSyncJobDetail() {
		return JobBuilder.newJob(LguV1MessageSyncJob.class)
			.withIdentity("LguV1MessageSyncJob")
			.storeDurably()
			.build();
	}

	@Bean
	public JobDetail lguV2MessageSyncJobDetail() {
		return JobBuilder.newJob(LguV2MessageSyncJob.class)
			.withIdentity("LguV2MessageSyncJob")
			.storeDurably()
			.build();
	}
	*/

}
