package com.example.mfb_ussd_process_flow.controller;

import com.example.mfb_ussd_process_flow.dto.response.ExceptionResponse;
import com.example.mfb_ussd_process_flow.exceptions.MFBException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController extends RuntimeException{

    @ExceptionHandler(MFBException.class)
    public ResponseEntity<Object> FMBException(MFBException exception) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

}
