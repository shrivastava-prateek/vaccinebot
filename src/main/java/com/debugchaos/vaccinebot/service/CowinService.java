package com.debugchaos.vaccinebot.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.debugchaos.vaccinebot.vo.Center;
import com.debugchaos.vaccinebot.vo.CowinCalendarResponse;
import com.debugchaos.vaccinebot.vo.PollingRequest;

@Component
public class CowinService {

	@Autowired
	RestTemplate restTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(CowinService.class);

	final static String datePattern = "dd-MM-yyyy";
	final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
	
	@Value("${cowin.url}")
	String cowinURI;
	
	public CowinCalendarResponse cowinFindCalendarByPin(PollingRequest pollingRequest) {
		
		Date date = new Date();
		HttpHeaders headers = new HttpHeaders();
		headers.set("user-agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36");

		String url = cowinURI + "calendarByPin?pincode=" + pollingRequest.getPinCode() + "&date=" + simpleDateFormat.format(date);

		logger.debug(url);
		
		HttpEntity<CowinCalendarResponse> res = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null, headers),
				CowinCalendarResponse.class);

		logger.debug(res+"");
		logger.debug(res.getBody().toString());
		
		return res.getBody()!=null?res.getBody():null;
	}
	

	private CowinCalendarResponse getBelow45Slots(CowinCalendarResponse cowinResponse) {

		CowinCalendarResponse cowinResponseFiltered = new CowinCalendarResponse();
		
		List<Center> centers =  cowinResponse.getCenters().stream().filter(center -> center.getSessions().stream().
				filter(session -> session.getMin_age_limit() == 18 && session.getAvailable_capacity() > 0?true:false)
				.collect(Collectors.toList()).isEmpty()?false:true).collect(Collectors.toList());
		
		cowinResponseFiltered.setCenters(centers);
		
		logger.debug("filtered responses: " + cowinResponseFiltered);

		return cowinResponseFiltered;

	}
	
	
	private CowinCalendarResponse get45andAboveSlots(CowinCalendarResponse cowinResponse) {

		CowinCalendarResponse cowinResponseFiltered = new CowinCalendarResponse();
		
		List<Center> centers =  cowinResponse.getCenters().stream().filter(center -> center.getSessions().stream().
				filter(session -> session.getMin_age_limit() == 45 && session.getAvailable_capacity() > 0?true:false)
				.collect(Collectors.toList()).isEmpty()?false:true).collect(Collectors.toList());
		
		cowinResponseFiltered.setCenters(centers);

		return cowinResponseFiltered;

	}
	
	
	
	public List<String> formatMessageCowinResponse(CowinCalendarResponse cowinResponse) {
		List<String> messages = new ArrayList<>();
		
		if (cowinResponse != null && !cowinResponse.getCenters().isEmpty()) {
			cowinResponse.getCenters().forEach(center ->
						{
							String message = "*Vaccine is available!, Please find below the details:*\n"
												+ "*Name: "+center.getName()+"*\n"
												+ "Address: "+center.getAddress()+"\n"
												+ "District Name: "+center.getDistrict_name()+"\n"
												+ "State Name: "+center.getState_name()+"\n";
							List<String> sessionMessages = center.getSessions().stream().filter(session -> session.getAvailable_capacity()>0?true:false).map(session -> {
								String centerMessage = "*Available Capacity Dose 1: "+session.getAvailable_capacity_dose1()+"*\n"
										+ "*Available Capacity Dose 2: "+session.getAvailable_capacity_dose2()+"*\n"
										+ "*Total available capacity: "+session.getAvailable_capacity()+"*\n"
										+ "Vaccine: "+session.getVaccine()+"\n"
										+ "*Min Age Limit: "+session.getMin_age_limit()+"*\n"
										+ "Date: "+session.getDate()+"";
								return centerMessage;
							}).collect(Collectors.toList());
							
							sessionMessages.forEach(messageStr -> messages.add(message+messageStr));
						
						}
						
			);
		}
		
		logger.debug("messages: " + messages);
		
		return messages;
		
	}
	
	public List<String> checkAvailability(PollingRequest pollingRequest){
		logger.debug("going to poll service: ");
		List<String> messages = null;
		CowinCalendarResponse filteredResponse = null;
		CowinCalendarResponse cowinResponse =  cowinFindCalendarByPin(pollingRequest);
		
		int age = pollingRequest.getAge() != null ? Integer.parseInt(pollingRequest.getAge()) : 18;

		if (age < 45 && cowinResponse != null) {
			filteredResponse = getBelow45Slots(cowinResponse);
		}
		else if(age >= 45 && cowinResponse != null) {
			filteredResponse = get45andAboveSlots(cowinResponse);
		}
		if(filteredResponse != null) {
			messages = formatMessageCowinResponse(filteredResponse);	
		}
		
		return messages;
	}


}
