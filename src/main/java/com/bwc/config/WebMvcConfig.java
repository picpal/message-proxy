package com.bwc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bwc.common.interceptor.MessageGateInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final MessageGateInterceptor messageGateInterceptor;

	public WebMvcConfig(MessageGateInterceptor messageGateInterceptor) {
		this.messageGateInterceptor = messageGateInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(messageGateInterceptor)
			// .excludePathPatterns("") // 적용 제외 패턴
			.addPathPatterns("/**");
	}
}
