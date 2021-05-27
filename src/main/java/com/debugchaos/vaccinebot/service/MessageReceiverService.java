package com.debugchaos.vaccinebot.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.debugchaos.vaccinebot.VaccineBot;
import com.debugchaos.vaccinebot.vo.PollingRequest;

@Component
public class MessageReceiverService {

	private static final Logger logger = LoggerFactory.getLogger(MessageReceiverService.class);

	private Set<PollingRequest> synchronizedRequestSet = ConcurrentHashMap.newKeySet();

	private final static Long sleepTime = 5 * 60 * 1000l;

	@Autowired
	CowinService cowinService;

	@Autowired
	VaccineBot vaccineBot;

	@Autowired
	PollingRequestService pollingRequestService;

	@JmsListener(destination = "registerQueue", containerFactory = "queueFactory")
	public void receiveMessage(PollingRequest pollingRequest) {
		logger.info("request registered for details: " + pollingRequest);

		synchronizedRequestSet.add(pollingRequest);

		pollingRequestService.saveRequest(pollingRequest);

		logger.info("Current Requests: " + synchronizedRequestSet);

	}

	@JmsListener(destination = "unregisterQueue", containerFactory = "queueFactory")
	public void receiveMessageDeletion(PollingRequest pollingRequest) {
		logger.info("request registered for deletion: " + pollingRequest);

		List<PollingRequest> toBeRemoved = synchronizedRequestSet.stream().filter(
				pRequest -> pRequest.getUserName().equalsIgnoreCase(pollingRequest.getUserName()) ? true : false)
				.collect(Collectors.toList());
		synchronizedRequestSet.removeAll(toBeRemoved);

		pollingRequestService.deleteRequestByUserName(pollingRequest.getUserName());

		logger.info("Current Requests after deletion: " + synchronizedRequestSet);

	}

	public void pollForLife() {

		while (true) {
			try {
				synchronizedRequestSet.stream().forEach(pollingRequest -> {
					List<String> messages = cowinService.checkAvailability(pollingRequest);

					if (messages != null && !messages.isEmpty()) {
						messages.forEach(message -> vaccineBot.sendMessage(pollingRequest.getChatId(), message));
					}

				});

				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void initializeRequestSet() {

		List<PollingRequest> pollingRequests = pollingRequestService.getAllPollingRequest();
		pollingRequests.forEach(request -> synchronizedRequestSet.add(request));

	}
	
}
