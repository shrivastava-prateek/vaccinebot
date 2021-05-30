package com.debugchaos.vaccinebot.vo;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class PollingRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	@Column(name = "USERID")
	private Long userId;
	@Column(name = "USERNAME")
	private String userName;
	@Column(name = "PINCODE")
	private int pincode;
	@Column(name = "AGE")
	private int age;
	@Column(name = "CHATID")
	private Long chatId;
	@Column(name = "CREATEDDATE")
	private Instant createdDate;
	
	
	@Transient
	private Set<SlotDetails> slotDetails;

	public PollingRequest() {
	}

	public PollingRequest(Long userId, String userName, int pincode, int age, Long chatId,
			Instant createdDate) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.pincode = pincode;
		this.age = age;
		this.chatId = chatId;
		this.createdDate = createdDate;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getPincode() {
		return pincode;
	}

	public void setPincode(int pincode) {
		this.pincode = pincode;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Long getChatId() {
		return chatId;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}

	public Instant getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Instant createdDate) {
		this.createdDate = createdDate;
	}




	public Set<SlotDetails> getSlotDetails() {
		if(slotDetails == null) {
			slotDetails =  new HashSet<>();
		}
		return slotDetails;
	}

	public void setSlotDetails(Set<SlotDetails> slotDetails) {
		this.slotDetails = slotDetails;
	}
	
	public String getFormattedMessage() {
		
		return "User Name: " + userName + ", Pincode: " + pincode+"\n"
				+ ", age criteria: " + age + ", Chat Id: " + chatId +"\n"+ ", Registration Date: " + createdDate+"\n\n";
	}

	@Override
	public String toString() {
		return "PollingRequest [id=" + id + ", userId=" + userId + ", userName=" + userName + ", pincode=" + pincode
				+ ", age=" + age + ", chatId=" + chatId + ", createdDate=" + createdDate + ", slotDetails="
				+ slotDetails + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + age;
		result = prime * result + ((chatId == null) ? 0 : chatId.hashCode());
		result = prime * result + pincode;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		PollingRequest other = (PollingRequest) obj;
		if (age != other.age)
			return false;
		if (chatId == null) {
			if (other.chatId != null)
				return false;
		} else if (!chatId.equals(other.chatId))
			return false;
		if (pincode != other.pincode)
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	


}
