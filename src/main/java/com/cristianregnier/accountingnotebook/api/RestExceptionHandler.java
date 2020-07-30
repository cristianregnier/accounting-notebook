package com.cristianregnier.accountingnotebook.api;

import com.cristianregnier.accountingnotebook.api.exception.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

   private static final ObjectMapper objectMapper = new ObjectMapper();

   @ExceptionHandler(NotFoundException.class)
   protected ResponseEntity<Object> handleNotFoundException(NotFoundException ex)
           throws JsonProcessingException {
      return buildResponse(HttpStatus.NOT_FOUND, ex);
   }

   @ExceptionHandler(IllegalArgumentException.class)
   protected ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex)
           throws JsonProcessingException {
      return buildResponse(HttpStatus.BAD_REQUEST, ex);
   }

   @ExceptionHandler(value = {IllegalStateException.class})
   protected ResponseEntity<Object> handleIllegalState(IllegalStateException ex)
           throws JsonProcessingException {
      return buildResponse(HttpStatus.CONFLICT, ex);
   }

   private ResponseEntity<Object> buildResponse(HttpStatus status, Throwable throwable) {
      Map<String, Object> body = new HashMap<>();
      body.put("timestamp", Calendar.getInstance().getTime());
      body.put("status", status.value());
      body.put("error", status.getReasonPhrase());
      body.put("message", throwable.getMessage());

      return ResponseEntity.status(status)
              .contentType(MediaType.APPLICATION_JSON)
              .header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
              .body(body);
   }
}