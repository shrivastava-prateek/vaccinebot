package com.debugchaos.vaccinebot.service;

import com.debugchaos.vaccinebot.vo.PollingRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomCommandService {

	@Autowired
	JmsTemplate jmsTemplate;

	private static final Logger logger = LoggerFactory.getLogger(CustomCommandService.class);

	private static final String destinationRegistrationQueueName = "registerQueue";
	private static final String destinationUnRegistrationQueueName = "unregisterQueue";

	public void registerPollingRequest(PollingRequest pollingRequest) {
		logger.info("going to register request: " + pollingRequest);
		jmsTemplate.convertAndSend(destinationRegistrationQueueName, pollingRequest);

	}

	public void unregisterPollingRequest(PollingRequest pollingRequest) {
		logger.info("going to register request: " + pollingRequest);
		jmsTemplate.convertAndSend(destinationUnRegistrationQueueName, pollingRequest);

	}

}
