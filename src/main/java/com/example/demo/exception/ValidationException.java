package com.example.demo.exception;

import lombok.Data;

@Data
public class ValidationException extends RuntimeException {
	
	String errRsn;
	String errCode;
	public ValidationException() {
		
	}
	public ValidationException(String message,String errRsn,String errCode) {
		super(message);
		this.errRsn=errRsn;
		this.errCode=errCode;
	}

}
