package com.amir.system.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.amir.artifact.ArtifactNotFoundException;
import com.amir.system.Result;
import com.amir.system.StatusCode;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
	
	@ExceptionHandler(ArtifactNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	Result handleArtifactNotFoundException(ArtifactNotFoundException ex) {
		return new Result(false, StatusCode.NOT_FOUND, ex.getMessage());
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	Result handleValidationException(MethodArgumentNotValidException ex) {
		List<ObjectError> errors = ex.getBindingResult().getAllErrors();
		Map<String, String> map = new HashMap<>(errors.size());
		errors.forEach(error -> {
			String key = ((FieldError) error).getField();
			String value = error.getDefaultMessage();
			map.put(key, value);
		});
		return new Result(false, StatusCode.INVALID_ARGUMENT, "Provided arguments are invalid, see data for details.", map);
	}
}