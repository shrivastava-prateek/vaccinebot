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
public class Center {

	private int center_id;
    private String name;
    private String address;
    private String state_name;
    private String district_name;
    private String block_name;
    private int pincode;
    private int lat;
    //private int long;
    private String from;
    private String to;
    private String fee_type;
    private List<Session> sessions;
    
}
