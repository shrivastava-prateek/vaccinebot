package com.debugchaos.vaccinebot.exception;

public class InvalidRegisterRequestException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidRegisterRequestException(String message) {
		super(message);
	}

}
