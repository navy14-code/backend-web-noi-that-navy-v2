package com.example.doan.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorDetails> sellerExceptionHandle(UserException ue, WebRequest req) {

        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setError(ue.getMessage());
        errorDetails.setDetails(req.getDescription(false));
        errorDetails.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<ErrorDetails> productExceptionHandle(ProductException pe, WebRequest req) {

        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setError(pe.getMessage());
        errorDetails.setDetails(req.getDescription(false));
        errorDetails.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);

    }
}
