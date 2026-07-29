package com.example.demo.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 7860691238040502302L;
	
	private final HttpStatus httpStatus;
	
	public NotFoundException(String message) { 
		super(message);
		httpStatus = HttpStatus.NOT_FOUND;
	}
}
