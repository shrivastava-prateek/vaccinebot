package com.debugchaos.vaccinebot.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@NoArgsConstructor
@ToString
public class CowinCalendarResponse {
	
	@JsonProperty
	private List<Center> centers;	 

}
