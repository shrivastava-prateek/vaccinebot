package com.debugchaos.vaccinebot.service;

import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.ALREADY_REGISTERED_MESSAGE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.DDOS_MESSAGE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.NOT_REGISTERED_MESSAGE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.QUEUE_FACTORY;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.REGISTERATIONDETAILS_QUEUE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.REGISTERED_MESSAGE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.REGISTRATION_QUEUE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.UNREGISTERATION_QUEUE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.UNREGISTERED_MESSAGE;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.debugchaos.vaccinebot.VaccineBot;
import com.debugchaos.vaccinebot.constant.APP_CONSTANT;
import com.debugchaos.vaccinebot.util.DateTimeUtil;
import com.debugchaos.vaccinebot.vo.PollingRequest;
import com.debugchaos.vaccinebot.vo.SlotDetails;

@Component
public class MessageReceiverService {

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
		} else if (userRequests == null) {
			userRequests = ConcurrentHashMap.newKeySet();
			userRequests.add(pollingRequest);
			userRequestMap.put(pollingRequest.getUserId(), userRequests);

		} else if (userRequests != null && !userRequests.contains(pollingRequest)) {
			userRequests.add(pollingRequest);
		} else {
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
		if (pollingRequests != null && !pollingRequests.isEmpty())
			formattedMessage = pollingRequests.stream().map(p -> p.getFormattedMessage()).reduce((p1, p2) -> p1 + p2)
					.get();
		else
			formattedMessage = NOT_REGISTERED_MESSAGE;

		vaccineBot.sendMessage(pollingRequest.getChatId(), formattedMessage);

	}

	public void pollCowinForEachPincode() {

		pincodeRequestMap.forEach((pincode, pollingRequests) -> {
			if (!pollingRequests.isEmpty()) {

				cowinService.checkAvailabilityAndSendMessage(pincode, pollingRequests);

			}

		});
	}

	@PostConstruct
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

	public void removeOldSlotsFromPollingRequests() {
		logger.info("Going to clean up old slots");
		String currentDateTime = DateTimeUtil.getFormattedISTCurrentDate();
		LocalDate currentDate = LocalDate.parse(currentDateTime, APP_CONSTANT.ddMMyyyyFormatter);

		pincodeRequestMap.forEach((pincode, pollingRequests) -> {
			if (!pollingRequests.isEmpty()) {
				pollingRequests.forEach(pr -> {
					List<String> oldSlots = pr.getSlotDetails().stream().filter(slot -> {
						SlotDetails slotDetails = cowinService.getSessionSlotMap().get(slot);
						if(slotDetails !=null) {
							LocalDate slotDate = LocalDate.parse(slotDetails.getDate(), APP_CONSTANT.ddMMyyyyFormatter);
							if (slotDate.isBefore(currentDate))
								return true;
							else
								return false;	
						}
						else
							return true;
						
					}).collect(Collectors.toList());
					pr.getSlotDetails().removeAll(oldSlots);
					cowinService.getSessionSlotMap().keySet().removeAll(oldSlots);
					logger.debug("slots to be deleted: " + oldSlots);
					logger.debug("size after deletion: " + pr.getSlotDetails().size());
					logger.debug("polling request after deletion: " + pr);
					logger.debug("session slot map after deletion: " + cowinService.getSessionSlotMap().size());
				});
			}
		});
	}

}
