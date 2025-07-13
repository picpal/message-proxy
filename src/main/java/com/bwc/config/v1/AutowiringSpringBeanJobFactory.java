package com.bwc.config.v1;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

public class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory {

	private AutowireCapableBeanFactory beanFactory;

	public AutowiringSpringBeanJobFactory(AutowireCapableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
		final Object job = super.createJobInstance(bundle);
		beanFactory.autowireBean(job);
		return job;
	}

}
