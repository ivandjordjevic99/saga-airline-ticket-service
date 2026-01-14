package com.saga.airlinesystem.reservationservice.exceptions;

import com.saga.airlinesystem.reservationservice.exceptions.customexceptions.AbstractException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AbstractException.class)
    public ResponseEntity<ErrorResponse> handleExceptions(AbstractException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getStatus(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.valueOf(ex.getStatus()));
    }
}
