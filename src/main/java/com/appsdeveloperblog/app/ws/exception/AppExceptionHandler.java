/**
 * 
 */
package com.appsdeveloperblog.app.ws.exception;

import java.util.Date;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessage;

/**
 * @author saba
 *
 */

@ControllerAdvice
public class AppExceptionHandler {
	
	@ExceptionHandler(value = {UserServiceException.class})
	public ResponseEntity<Object> handleUserServiceException(UserServiceException ex, WebRequest request){
		
		ErrorMessage errorMessages = new ErrorMessage(new Date(), ex.getMessage());
		return new ResponseEntity<>(errorMessages, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		//first we were passing the error message as string but now we have created our own custom exception class for message 
		//return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(value = {Exception.class})
	public ResponseEntity<Object> handleOtherException(UserServiceException ex, WebRequest request){
		
		ErrorMessage errorMessages = new ErrorMessage(new Date(), ex.getMessage());
		return new ResponseEntity<>(errorMessages, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
