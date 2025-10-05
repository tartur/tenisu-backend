package com.t2.screening.tenisu.ui.rest;

import com.t2.screening.tenisu.domain.exception.PlayerNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(PlayerNotFoundException ex) {
        return  ResponseEntity.notFound().build();
    }
}
