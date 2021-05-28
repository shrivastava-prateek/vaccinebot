package com.debugchaos.vaccinebot.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CowinCalendarResponse {
	
	@JsonProperty
	private List<Center> centers;

	 
	public CowinCalendarResponse() {
	}

	public List<Center> getCenters() {
		return centers;
	}

	public void setCenters(List<Center> centers) {
		this.centers = centers;
	}

	@Override
	public String toString() {
		return "CowinCalendarResponse [centers=" + centers + "]";
	}
	 
	 

}
