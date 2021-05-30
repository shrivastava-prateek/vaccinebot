package com.debugchaos.vaccinebot.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.debugchaos.vaccinebot.VaccineBot;
import com.debugchaos.vaccinebot.vo.PollingRequest;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.*;

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

	private Map<Long, Set<PollingRequest>> userRequestMap = new ConcurrentHashMap<>();
	private Map<Integer, Set<PollingRequest>> pincodeRequestMap = new ConcurrentHashMap<>();

	@JmsListener(destination = REGISTRATION_QUEUE, containerFactory = QUEUE_FACTORY)
	public void receiveMessage(PollingRequest pollingRequest) {
		logger.info("request registered for details: " + pollingRequest);

		Set<PollingRequest> userRequests = userRequestMap.get(pollingRequest.getUserId());
		if (userRequests != null && userRequests.size() >= 10) {
			vaccineBot.sendMessage(pollingRequest.getChatId(), DDOS_MESSAGE);
			return;
		} else if (userRequests == null ) {
			userRequests = ConcurrentHashMap.newKeySet();
			userRequests.add(pollingRequest);
			userRequestMap.put(pollingRequest.getUserId(), userRequests);
			
		} else if(userRequests != null && !userRequests.contains(pollingRequest)) {
			userRequests.add(pollingRequest);
		}
		else {
			vaccineBot.sendMessage(pollingRequest.getChatId(), ALREADY_REGISTERED_MESSAGE);
			return;
		}

		pollingRequestService.saveRequest(pollingRequest);

		logger.info("Current User Requests Map: " + userRequestMap);

		Set<PollingRequest> pincodeRequests = pincodeRequestMap.get(pollingRequest.getPincode());
		if (pincodeRequests != null) {
			pincodeRequests.add(pollingRequest);
		} else {
			pincodeRequests = ConcurrentHashMap.newKeySet();
			pincodeRequests.add(pollingRequest);
			pincodeRequestMap.put(pollingRequest.getPincode(), pincodeRequests);
		}

		logger.info("Current Pincode Requests Map: " + pincodeRequestMap);
		
		vaccineBot.sendMessage(pollingRequest.getChatId(), REGISTERED_MESSAGE);

	}

	@JmsListener(destination = UNREGISTERATION_QUEUE, containerFactory = QUEUE_FACTORY)
	public void receiveMessageDeletion(PollingRequest pollingRequest) {
		logger.info("request registered for deletion: " + pollingRequest);

		userRequestMap.remove(pollingRequest.getUserId());

		pollingRequestService.deleteRequestByUserId(pollingRequest.getUserId());

		logger.info("Current user Requests after deletion: " + userRequestMap);

		pincodeRequestMap.forEach((pincode, pollingRequests) -> {
			List<PollingRequest> toBeRemovedList = pollingRequests.stream()
					.filter(pollingReq -> pollingReq.getUserId().equals(pollingRequest.getUserId()))
					.collect(Collectors.toList());
			pollingRequests.removeAll(toBeRemovedList);
		});

		logger.info("Current Pincode Requests Map after deletion: " + pincodeRequestMap);
		
		vaccineBot.sendMessage(pollingRequest.getChatId(), UNREGISTERED_MESSAGE);

	}
	
	@JmsListener(destination = REGISTERATIONDETAILS_QUEUE, containerFactory = QUEUE_FACTORY)
	public void receiveMessageFetchRegistrations(PollingRequest pollingRequest) {
		logger.info("request received for fetching registration details: " + pollingRequest);
		String formattedMessage = "";
		Set<PollingRequest> pollingRequests = userRequestMap.get(pollingRequest.getUserId());
		if(pollingRequests != null && !pollingRequests.isEmpty())
			formattedMessage = pollingRequests.stream().map(p -> p.getFormattedMessage()).reduce((p1,p2) -> p1+p2).get();
		else
			formattedMessage = NOT_REGISTERED_MESSAGE;
		
		vaccineBot.sendMessage(pollingRequest.getChatId(), formattedMessage);

	}

	public void pollCowinForLife() {

		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POOL_EXECUTOR_SIZE);

		while (Boolean.TRUE) {
			try {
				pincodeRequestMap.forEach((pincode, pollingRequests) -> {
					if(!pollingRequests.isEmpty()) {
						executor.submit(() -> {

							cowinService.checkAvailabilityAndSendMessage(pincode, pollingRequests);

						});
	
					}
				});

				Thread.sleep(Long.parseLong(sleepTime));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public void initializeRequestsMaps() {

		List<PollingRequest> savedPollingRequests = pollingRequestService.getAllPollingRequest();

		savedPollingRequests.stream().collect(Collectors.groupingBy(PollingRequest::getPincode))
				.forEach((pincode, pollingRequests) -> {
					Set<PollingRequest> pollingRequestSet = ConcurrentHashMap.newKeySet();
					pollingRequestSet.addAll(pollingRequests);
					pincodeRequestMap.put(pincode, pollingRequestSet);
				});

		savedPollingRequests.stream().collect(Collectors.groupingBy(PollingRequest::getUserId))
				.forEach((userId, pollingRequests) -> {
					Set<PollingRequest> pollingRequestSet = ConcurrentHashMap.newKeySet();
					pollingRequestSet.addAll(pollingRequests);
					userRequestMap.put(userId, pollingRequestSet);
				});

		logger.info("Current User Requests Map: " + userRequestMap);
		logger.info("Current Pincode Requests Map: " + pincodeRequestMap);

	}

}
