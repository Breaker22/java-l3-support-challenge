package com.example.demo.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.exception.NotFoundException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(value = NotFoundException.class)
	public ResponseEntity<Void> handleNotFoundEx(NotFoundException ex) {
		log.error(ex.getMessage(), ex);
		return ResponseEntity.status(ex.getHttpStatus()).build();
	}

}
