package com.debugchaos.vaccinebot.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.debugchaos.vaccinebot.vo.CowinResponse;
import com.debugchaos.vaccinebot.vo.PollingRequest;
import com.debugchaos.vaccinebot.vo.Session;

@Component
public class CowinService {

	@Autowired
	RestTemplate restTemplate;

	final static String datePattern = "dd-MM-yyyy";
	final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
	private static String cowinURI = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/findByPin";
	//private static String cowinURI = "https://7fd3165d-22f3-492d-a468-5897ffbf78bc.mock.pstmn.io/api/v2/appointment/sessions/public/findByPin";

	public CowinResponse callCowin(PollingRequest pollingRequest) {
		Date date = new Date();
		HttpHeaders headers = new HttpHeaders();
		headers.set("user-agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36");

		cowinURI = cowinURI + "?pincode=" + pollingRequest.getPinCode() + "&date=" + simpleDateFormat.format(date);

		HttpEntity<CowinResponse> res = restTemplate.exchange(cowinURI, HttpMethod.GET, new HttpEntity<>(null, headers),
				CowinResponse.class);

		
		return res.getBody()!=null?res.getBody():null;
	}

	private CowinResponse getBelow45Slots(CowinResponse cowinResponse) {

		CowinResponse cowinResponseFiltered = new CowinResponse();

		List<Session> availableSessions = cowinResponse.getSessions().stream()
				.filter(session -> session.getMin_age_limit() == 18 && session.getAvailable_capacity() > 0? true : false).collect(Collectors.toList());

		cowinResponseFiltered.setSessions(availableSessions);

		return cowinResponseFiltered;

	}
	
	private CowinResponse get45andAboveSlots(CowinResponse cowinResponse) {

		CowinResponse cowinResponseFiltered = new CowinResponse();

		List<Session> availableSessions = cowinResponse.getSessions().stream()
				.filter(session -> session.getMin_age_limit() == 45 && session.getAvailable_capacity() > 0? true : false).collect(Collectors.toList());

		cowinResponseFiltered.setSessions(availableSessions);

		return cowinResponseFiltered;

	}
	
	

	public List<String> formatMessageCowinResponse(CowinResponse cowinResponse) {
		List<String> messages = null;
		if (cowinResponse != null && !cowinResponse.getSessions().isEmpty()) {
			messages = cowinResponse.getSessions().stream().map(session ->
						"*Vaccine is available!, Please find below the details:*\n"
						+ "*Name: "+session.getName()+"*\n"
						+ "Address: "+session.getAddress()+"\n"
						+ "District Name: "+session.getDistrict_name()+"\n"
						+ "State Name: "+session.getState_name()+"\n"
						+ "*Available Capacity Dose 1: "+session.getAvailable_capacity_dose1()+"*\n"
						+ "*Available Capacity Dose 2: "+session.getAvailable_capacity_dose2()+"*\n"
						+ "*Total available capacity: "+session.getAvailable_capacity()+"*\n"
						+ "Vaccine: "+session.getVaccine()+"\n"
						+ "*Min Age Limit: "+session.getMin_age_limit()+"*\n"
						+ "Date: "+session.getDate()+""
						
			).collect(Collectors.toList());
		}
		
		return messages;
		
	}
	
	public List<String> checkAvailability(PollingRequest pollingRequest){
		List<String> messages = null;
		CowinResponse filteredResponse = null;
		CowinResponse cowinResponse =  callCowin(pollingRequest);
		
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
