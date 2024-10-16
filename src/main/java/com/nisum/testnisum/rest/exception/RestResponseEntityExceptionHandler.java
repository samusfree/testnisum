package com.nisum.testnisum.rest.exception;

import com.nisum.testnisum.dto.ResponseError;
import com.nisum.testnisum.exception.EmailExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class RestResponseEntityExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseError handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);
        return mapErrorFromException(ex, ex.getAllErrors());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseError handleValidationExceptions(
            WebExchangeBindException ex) {
        log.error(ex.getMessage(), ex);
        return mapErrorFromException(ex, ex.getAllErrors());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({EmailExistsException.class})
    public ResponseError validEmail(
            EmailExistsException ex) {
        log.error(ex.getMessage(), ex);
        return mapErrorFromException(ex);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ServerWebInputException.class})
    public ResponseError notBody(
            ServerWebInputException ex) {
        log.error(ex.getMessage(), ex);
        return mapErrorFromException(ex);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseError handleException(
            Exception ex) {
        log.error(ex.getMessage(), ex);
        return mapErrorFromException(ex);
    }

    private ResponseError mapErrorFromException(Exception ex,
                                                java.util.List<org.springframework.validation.ObjectError> errors) {
        Map<String, String> mapErrors = new HashMap<>();
        errors.forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            mapErrors.put(fieldName, errorMessage);
        });
        return new ResponseError("Validation exception", mapErrors);
    }

    private ResponseError mapErrorFromException(Exception ex) {
        return new ResponseError(ex.getMessage(), Map.of());
    }

    private ResponseError mapErrorFromException(ResponseStatusException ex) {
        return new ResponseError(ex.getReason(), Map.of());
    }
}
