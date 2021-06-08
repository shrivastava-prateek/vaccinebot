package com.debugchaos.vaccinebot.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter
@NoArgsConstructor
@ToString
public class Session {
	private int center_id;
	private String name;
	private String address;
	private String state_name;
	private String district_name;
	private String block_name;
	private int pincode;
	private String from;
	private String to;
	private int lat;
	// private int long;
	private String fee_type;
	private String session_id;
	private String date;
	private int available_capacity_dose1;
	private int available_capacity_dose2;
	private int available_capacity;
	private String fee;
	private int min_age_limit;
	private String vaccine;
	private List<String> slots;
	
}
