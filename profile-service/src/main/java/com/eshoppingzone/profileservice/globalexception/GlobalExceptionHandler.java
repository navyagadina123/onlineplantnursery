package com.eshoppingzone.profileservice.globalexception;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException (){
        return new  ResponseEntity<String>("userName not present",HttpStatus.NOT_FOUND);
    }
	
	@ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> NoSuchElementException(){
        return new  ResponseEntity<String>("userName not present",HttpStatus.NOT_FOUND);
    }
	
}
