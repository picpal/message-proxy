package com.bwc.config.v1;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.bwc.message.v1.sender.jobs.LguV1MessageSyncJob;
import com.bwc.message.v1.sender.jobs.LguV2MessageSyncJob;
import com.bwc.message.v1.sender.jobs.MtsMessageSyncJob;

@Configuration
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

		// Job Detail Bean 설정
		schedulerFactoryBean.setJobDetails(
			mtsMessageSyncJobDetail(),
			lguV1MessageSyncJobDetail(),
			lguV2MessageSyncJobDetail()
		);
		return schedulerFactoryBean;
	}

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

}
