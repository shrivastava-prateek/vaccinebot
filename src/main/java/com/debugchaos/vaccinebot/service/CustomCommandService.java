package com.debugchaos.vaccinebot.service;

import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.AGE_PATTERN;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.INSUFFICIENT_PARAMETERS_MESSAGE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.INVALID_AGE_MESSAGE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.INVALID_PINCODE_MESSAGE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.PINCODE_PATTERN;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.REGISTERATIONDETAILS_QUEUE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.REGISTRATION_QUEUE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.UNREGISTERATION_QUEUE;

import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.debugchaos.vaccinebot.exception.InvalidRegisterRequestException;
import com.debugchaos.vaccinebot.vo.PollingRequest;

@Component
public class CustomCommandService {

	@Autowired
	JmsTemplate jmsTemplate;

	private static final Logger logger = LoggerFactory.getLogger(CustomCommandService.class);

	public void registerPollingRequest(PollingRequest pollingRequest) {
		logger.debug("going to register request: " + pollingRequest);
		jmsTemplate.convertAndSend(REGISTRATION_QUEUE, pollingRequest);

	}

	public void unregisterPollingRequest(PollingRequest pollingRequest) {
		logger.debug("going to unregister request: " + pollingRequest);
		jmsTemplate.convertAndSend(UNREGISTERATION_QUEUE, pollingRequest);

	}

	public void getRegistrationDetails(PollingRequest pollingRequest) {
		logger.debug("going to fetch registration requests: " + pollingRequest);
		jmsTemplate.convertAndSend(REGISTERATIONDETAILS_QUEUE, pollingRequest);
	}

	public void validateRegisterRequest(String[] params) throws InvalidRegisterRequestException {

		if (params.length < 3)
			throw new InvalidRegisterRequestException(INSUFFICIENT_PARAMETERS_MESSAGE);
		String pincode = params[1];
		String age = params[2];

		if (pincode == null || pincode.isBlank())
			throw new InvalidRegisterRequestException(INVALID_PINCODE_MESSAGE);
		if (age == null || age.isBlank())
			throw new InvalidRegisterRequestException(INVALID_AGE_MESSAGE);

		Matcher pincodeMatcher = PINCODE_PATTERN.matcher(pincode.trim());
		Matcher ageMatcher = AGE_PATTERN.matcher(age.trim());

		if (!pincodeMatcher.matches())
			throw new InvalidRegisterRequestException(INVALID_PINCODE_MESSAGE);
		if (!ageMatcher.matches())
			throw new InvalidRegisterRequestException(INVALID_AGE_MESSAGE);

	}

}
