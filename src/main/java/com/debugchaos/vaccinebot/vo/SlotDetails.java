package com.debugchaos.vaccinebot.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
public class SlotDetails {
	
	private int center_id;
    private String name;
    private String address;
    private String state_name;
    private String district_name;
    private String block_name;
    private int pincode;
    private int lat;
    private String from;
    private String to;
    private String fee_type;
	private String session_id;
	private String date;
	private int available_capacity_dose1;
	private int available_capacity_dose2;
	private int available_capacity;
	private String fee;
	private int min_age_limit;
	private String vaccine;
	private List<String> slotTimings;
	
	public String getFormattedMessage() {
		return "*Vaccine is available!, Please find below the details:*\n"
				+ "*Name: "+this.name+"*\n"
				+ "Address: "+this.address+"\n"
				+ "District Name: "+this.district_name+"\n"
				+ "State Name: "+this.state_name+"\n"
				+ "*Available Capacity Dose 1: "+this.available_capacity_dose1+"*\n"
				+ "*Available Capacity Dose 2: "+this.available_capacity_dose2+"*\n"
				+ "*Total available capacity: "+this.available_capacity+"*\n"
				+ "Vaccine: "+this.vaccine+"\n"
				+ "*Min Age Limit: "+this.min_age_limit+"*\n"
				+ "Date: "+this.date+"\n"
				+ "Fee Type: "+this.fee_type+"";

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + center_id;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((district_name == null) ? 0 : district_name.hashCode());
		result = prime * result + ((fee_type == null) ? 0 : fee_type.hashCode());
		result = prime * result + min_age_limit;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + pincode;
		result = prime * result + ((session_id == null) ? 0 : session_id.hashCode());
		result = prime * result + ((state_name == null) ? 0 : state_name.hashCode());
		result = prime * result + ((vaccine == null) ? 0 : vaccine.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SlotDetails other = (SlotDetails) obj;
		if (center_id != other.center_id)
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (district_name == null) {
			if (other.district_name != null)
				return false;
		} else if (!district_name.equals(other.district_name))
			return false;
		if (fee_type == null) {
			if (other.fee_type != null)
				return false;
		} else if (!fee_type.equals(other.fee_type))
			return false;
		if (min_age_limit != other.min_age_limit)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pincode != other.pincode)
			return false;
		if (session_id == null) {
			if (other.session_id != null)
				return false;
		} else if (!session_id.equals(other.session_id))
			return false;
		if (state_name == null) {
			if (other.state_name != null)
				return false;
		} else if (!state_name.equals(other.state_name))
			return false;
		if (vaccine == null) {
			if (other.vaccine != null)
				return false;
		} else if (!vaccine.equals(other.vaccine))
			return false;
		return true;
	}



	
	
	

}
