package br.com.uptimeowlet.backend.exceptions;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.stream.Collectors;

public class DataValidationException extends RuntimeException {

    public DataValidationException(Errors errors) {
        super(errors.getAllErrors().stream().map(DataValidationException::formatMessageError)
                .collect(Collectors.joining("\n")));
    }

    public DataValidationException(String message) {
        super(message);
    }

    private static String formatMessageError(ObjectError error) {
        return String.format("%s %s", (error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName()), error.getDefaultMessage());
    }
}
