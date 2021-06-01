package com.debugchaos.vaccinebot.service;

import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.MIN_18_AGE;
import static com.debugchaos.vaccinebot.constant.APP_CONSTANT.MIN_45_AGE;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.debugchaos.vaccinebot.VaccineBot;
import com.debugchaos.vaccinebot.util.DateTimeUtil;
import com.debugchaos.vaccinebot.vo.CowinCalendarResponse;
import com.debugchaos.vaccinebot.vo.PollingRequest;
import com.debugchaos.vaccinebot.vo.SlotDetails;

@Component
public class CowinService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	VaccineBot vaccineBot;

	@Value("${cowin.url}")
	String cowinURI;

	private static final Logger logger = LoggerFactory.getLogger(CowinService.class);

	public CowinCalendarResponse cowinFindCalendarByPin(Integer pinCode) {

		HttpHeaders headers = new HttpHeaders();
		headers.set("user-agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36");

		String url = cowinURI + "calendarByPin?pincode=" + pinCode + "&date="
				+ DateTimeUtil.getFormattedISTCurrentDate();

		logger.debug(url);

		HttpEntity<CowinCalendarResponse> res = restTemplate.exchange(url, HttpMethod.GET,
				new HttpEntity<>(null, headers), CowinCalendarResponse.class);

		logger.debug(res + "");
		// logger.debug(res.getBody().toString());

		return res.getBody() != null ? res.getBody() : null;
	}

	private Set<SlotDetails> filterOnlyAvailableSlots(CowinCalendarResponse cowinResponse) {

		Set<SlotDetails> slotDetails = new HashSet<>();

		cowinResponse.getCenters().forEach(center -> {
			List<SlotDetails> slots = center.getSessions().stream()
					.filter(session -> session.getAvailable_capacity() > 0 ? true : false).map(session -> {
						return new SlotDetails(center.getCenter_id(), center.getName(), center.getAddress(),
								center.getState_name(), center.getDistrict_name(), center.getBlock_name(),
								center.getPincode(), center.getLat(), center.getFrom(), center.getTo(),
								center.getFee_type(), session.getSession_id(), session.getDate(),
								session.getAvailable_capacity_dose1(), session.getAvailable_capacity_dose2(),
								session.getAvailable_capacity(), session.getFee(), session.getMin_age_limit(),
								session.getVaccine());
					}).collect(Collectors.toList());

			slotDetails.addAll(slots);
		});

		logger.debug("filterOnlyAvailableSlots responses Size: " + slotDetails.size());
		logger.debug("filterOnlyAvailableSlots responses: " + slotDetails);

		return slotDetails;

	}

	@Async
	public void checkAvailabilityAndSendMessage(Integer pincode, Set<PollingRequest> pollingRequests) {

		logger.debug("For Pincode: " + pincode + " going to poll service, Total polling requests: "
				+ pollingRequests.size());

		// Segregate requests by age (above or below 45)
		Map<Boolean, List<PollingRequest>> ageWisePollingRequests = pollingRequests.stream()
				.collect(Collectors.partitioningBy(pollingRequest -> pollingRequest.getAge() >= MIN_45_AGE));

		logger.debug("For Pincode: " + pincode + " ageWisePollingRequests: " + ageWisePollingRequests);

		// call cowin API
		CowinCalendarResponse cowinResponse = cowinFindCalendarByPin(pincode);

		if (cowinResponse == null || cowinResponse.getCenters().isEmpty())
			return;

		// get only available slots
		Set<SlotDetails> availableSlots = filterOnlyAvailableSlots(cowinResponse);

		availableSlots.stream().collect(Collectors.groupingBy(SlotDetails::getMin_age_limit))
				.forEach((minAge, slots) -> {
					if (minAge == MIN_18_AGE) {
						logger.debug("For Pincode: " + pincode + " min age 18 slots Size: " + slots.size());
						// logger.debug("min age 18 slots: " + slots);
						ageWisePollingRequests.get(Boolean.FALSE).forEach(pollingRequest -> {
							logger.debug("For Pincode: " + pincode + " min age 18 polling request: " + pollingRequest);
							slots.forEach(slot -> {
								if (!pollingRequest.getSlotDetails().contains(slot)) {
									vaccineBot.sendMessage(pollingRequest.getChatId(), slot.getFormattedMessage());
									pollingRequest.getSlotDetails().add(slot);
									logger.debug("For Pincode: " + pincode
											+ " min age 18 requet added slot for tracking: " + pollingRequest);
								}
							});

						});
					} else {
						logger.debug("For Pincode: " + pincode + "min age 45 slots Size: " + slots.size());
						// logger.debug("min age 45 slots: " + slots);
						ageWisePollingRequests.get(Boolean.TRUE).forEach(pollingRequest -> {
							logger.debug("For Pincode: " + pincode + " min age 45 polling request: " + pollingRequest);
							slots.forEach(slot -> {
								if (!pollingRequest.getSlotDetails().contains(slot)) {
									vaccineBot.sendMessage(pollingRequest.getChatId(), slot.getFormattedMessage());
									pollingRequest.getSlotDetails().add(slot);
									logger.debug("For Pincode: " + pincode
											+ " min age 45 requet added slot for tracking: " + pollingRequest);
								}
							});
						});
					}
				});

	}

}
