package com.debugchaos.vaccinebot.vo;

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
	
	public SlotDetails() {
		super();
	}

	public SlotDetails(int center_id, String name, String address, String state_name, String district_name,
			String block_name, int pincode, int lat, String from, String to, String fee_type, String session_id,
			String date, int available_capacity_dose1, int available_capacity_dose2, int available_capacity, String fee,
			int min_age_limit, String vaccine) {
		super();
		this.center_id = center_id;
		this.name = name;
		this.address = address;
		this.state_name = state_name;
		this.district_name = district_name;
		this.block_name = block_name;
		this.pincode = pincode;
		this.lat = lat;
		this.from = from;
		this.to = to;
		this.fee_type = fee_type;
		this.session_id = session_id;
		this.date = date;
		this.available_capacity_dose1 = available_capacity_dose1;
		this.available_capacity_dose2 = available_capacity_dose2;
		this.available_capacity = available_capacity;
		this.fee = fee;
		this.min_age_limit = min_age_limit;
		this.vaccine = vaccine;
	}

	public int getCenter_id() {
		return center_id;
	}

	public void setCenter_id(int center_id) {
		this.center_id = center_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getState_name() {
		return state_name;
	}

	public void setState_name(String state_name) {
		this.state_name = state_name;
	}

	public String getDistrict_name() {
		return district_name;
	}

	public void setDistrict_name(String district_name) {
		this.district_name = district_name;
	}

	public String getBlock_name() {
		return block_name;
	}

	public void setBlock_name(String block_name) {
		this.block_name = block_name;
	}

	public int getPincode() {
		return pincode;
	}

	public void setPincode(int pincode) {
		this.pincode = pincode;
	}

	public int getLat() {
		return lat;
	}

	public void setLat(int lat) {
		this.lat = lat;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFee_type() {
		return fee_type;
	}

	public void setFee_type(String fee_type) {
		this.fee_type = fee_type;
	}

	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getAvailable_capacity_dose1() {
		return available_capacity_dose1;
	}

	public void setAvailable_capacity_dose1(int available_capacity_dose1) {
		this.available_capacity_dose1 = available_capacity_dose1;
	}

	public int getAvailable_capacity_dose2() {
		return available_capacity_dose2;
	}

	public void setAvailable_capacity_dose2(int available_capacity_dose2) {
		this.available_capacity_dose2 = available_capacity_dose2;
	}

	public int getAvailable_capacity() {
		return available_capacity;
	}

	public void setAvailable_capacity(int available_capacity) {
		this.available_capacity = available_capacity;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public int getMin_age_limit() {
		return min_age_limit;
	}

	public void setMin_age_limit(int min_age_limit) {
		this.min_age_limit = min_age_limit;
	}

	public String getVaccine() {
		return vaccine;
	}

	public void setVaccine(String vaccine) {
		this.vaccine = vaccine;
	}
	
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
	public String toString() {
		return "SlotDetails [center_id=" + center_id + ", name=" + name + ", address=" + address + ", state_name="
				+ state_name + ", district_name=" + district_name + ", block_name=" + block_name + ", pincode="
				+ pincode + ", lat=" + lat + ", from=" + from + ", to=" + to + ", fee_type=" + fee_type
				+ ", session_id=" + session_id + ", date=" + date + ", available_capacity_dose1="
				+ available_capacity_dose1 + ", available_capacity_dose2=" + available_capacity_dose2
				+ ", available_capacity=" + available_capacity + ", fee=" + fee + ", min_age_limit=" + min_age_limit
				+ ", vaccine=" + vaccine + "]";
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
