package com.zettamine.mi.exceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> returnFailureCause(MethodArgumentNotValidException ex){
		List<String> errors = new ArrayList<>();

	    ex.getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));

	    Map<String, List<String>> result = new HashMap<>();
	    result.put("errors", errors);

	    return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<?> failureJSONConverter(HttpMessageNotReadableException ex){
		List<String> errors = new ArrayList<>();
		
		errors.add(ex.getMessage());

	    Map<String, List<String>> result = new HashMap<>();
	    result.put("errors", errors);

	    return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
	}

}
