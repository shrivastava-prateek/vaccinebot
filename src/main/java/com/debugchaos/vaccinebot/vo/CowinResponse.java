package com.debugchaos.vaccinebot.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CowinResponse {

	@JsonProperty
	private List<Session> sessions;

	public List<Session> getSessions() {
		return sessions;
	}

	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
	}

	@Override
	public String toString() {
		return "Availability=" + sessions;
	}

}
