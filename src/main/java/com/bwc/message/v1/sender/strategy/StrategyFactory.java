package com.bwc.message.v1.sender.strategy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.bwc.common.constant.ErrorCodeEnum;
import com.bwc.common.exception.MessageGateException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StrategyFactory {
	private final Map<String, MessageServiceStrategy> strategies = new HashMap<>();

	@Autowired
	public StrategyFactory(ApplicationContext context) {
		Map<String, Object> strategyBeans = context.getBeansWithAnnotation(MessageServiceType.class);
		for (Object strategyBean : strategyBeans.values()) {
			MessageServiceType typeAnnotation = strategyBean.getClass().getAnnotation(MessageServiceType.class);
			strategies.put(typeAnnotation.value(), (MessageServiceStrategy)strategyBean);
		}
	}

	public MessageServiceStrategy getStrategy(String type) {
		try {
			return strategies.get(type);
		} catch (Exception e) {
			throw new MessageGateException(ErrorCodeEnum.NOT_FOUND_STRATEGY);
		}
	}
}
