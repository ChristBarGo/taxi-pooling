package com.challenge.taxi.pooling.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class TaxiPoolingExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleContraintViolationException(ConstraintViolationException e) {
        StringBuilder constraintViolationsStringBuilder = new StringBuilder();

        for (ConstraintViolation constraintViolation : e.getConstraintViolations()) {
            constraintViolationsStringBuilder.append(constraintViolation.getPropertyPath() + " " + constraintViolation.getMessage());
        }

        return ResponseEntity.badRequest().body(constraintViolationsStringBuilder.toString());
    }
}
