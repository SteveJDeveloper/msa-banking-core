package com.pichincha.dm.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleGeneralException(Exception ex) {
    log.error("Error genérico", ex);
    Map<String, Object> body = new HashMap<>();
    body.put("error", "Error interno del servidor");
    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex) {
    log.error("Error en validación de clases", ex);
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage()));
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {
    log.error("Error en búsqueda de recursos", ex);
    Map<String, Object> body = new HashMap<>();
    body.put("error", ex.getMessage());
    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({DuplicateResourceException.class, DisabledClientException.class})
  public ResponseEntity<Object> handleConflictedRequest(Exception ex) {
    log.error("Error en transacción por dato del usuario", ex);
    Map<String, Object> body = new HashMap<>();
    body.put("error", ex.getMessage());
    return new ResponseEntity<>(body, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(MovementCreationException.class)
  public ResponseEntity<Object> handleMovementCreation(MovementCreationException ex) {
    log.error("Error en transacción de movimientos", ex);
    Map<String, Object> body = new HashMap<>();
    body.put("error", ex.getMessage());
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

}
