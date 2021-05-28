package com.debugchaos.vaccinebot.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.debugchaos.vaccinebot.VaccineBot;
import com.debugchaos.vaccinebot.vo.PollingRequest;

@Component
public class MessageReceiverService {

	@Value("${pauseinms}")
	private String sleepTime;

	@Autowired
	CowinService cowinService;

	@Autowired
	VaccineBot vaccineBot;

	@Autowired
	PollingRequestService pollingRequestService;

	private static final Logger logger = LoggerFactory.getLogger(MessageReceiverService.class);
	private Map<String, Set<PollingRequest>> requestMap = new ConcurrentHashMap<>();
	private static final String DDOS_MESSAGE = "You have reached the maximum limit of registrations.\n"
			+ "Please unregister using /unregister command and then register again.";

	@JmsListener(destination = "registerQueue", containerFactory = "queueFactory")
	public void receiveMessage(PollingRequest pollingRequest) {
		logger.info("request registered for details: " + pollingRequest);

		Set<PollingRequest> requests = requestMap.get(pollingRequest.getUserName());
		if (requests != null && requests.size() >= 20) {
			vaccineBot.sendMessage(pollingRequest.getChatId(), DDOS_MESSAGE);
		} else if (requests != null) {
			requests.add(pollingRequest);
		} else {
			requests = ConcurrentHashMap.newKeySet();
			requests.add(pollingRequest);
			requestMap.put(pollingRequest.getUserName(), requests);
		}

		pollingRequestService.saveRequest(pollingRequest);

		logger.info("Current Requests: " + requestMap);

	}

	@JmsListener(destination = "unregisterQueue", containerFactory = "queueFactory")
	public void receiveMessageDeletion(PollingRequest pollingRequest) {
		logger.info("request registered for deletion: " + pollingRequest);

		requestMap.remove(pollingRequest.getUserName());

		pollingRequestService.deleteRequestByUserName(pollingRequest.getUserName());

		logger.info("Current Requests after deletion: " + requestMap);

	}

	public void pollForLife() {

		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);

		while (true) {
			try {
				requestMap.forEach((key, value) -> {
					executor.submit(() -> {
						value.stream().forEach(pollingRequest -> {
							List<String> messages = cowinService.checkAvailability(pollingRequest);

							if (messages != null && !messages.isEmpty()) {
								messages.forEach(
										message -> vaccineBot.sendMessage(pollingRequest.getChatId(), message));
							}
							messages = null;

						});

					});
				});

				Thread.sleep(Long.parseLong(sleepTime));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public void initializeRequestSet() {

		List<PollingRequest> pollingRequests = pollingRequestService.getAllPollingRequest();
		pollingRequests.forEach(pollingRequest -> {
			Set<PollingRequest> requests = requestMap.get(pollingRequest.getUserName());
			if (requests != null) {
				requests.add(pollingRequest);
			} else {
				requests = ConcurrentHashMap.newKeySet();
				requests.add(pollingRequest);
				requestMap.put(pollingRequest.getUserName(), requests);
			}
		});

	}

}
