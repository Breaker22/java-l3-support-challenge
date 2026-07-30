package com.example.demo.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class DuplicateDataException extends RuntimeException {

	private static final long serialVersionUID = -5808168063797218814L;
	
	private final HttpStatus httpStatus;
	
	public DuplicateDataException(String message) { 
		super(message);
		httpStatus = HttpStatus.CONFLICT;
	}

}
