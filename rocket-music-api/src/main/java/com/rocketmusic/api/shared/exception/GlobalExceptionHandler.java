// src/main/java/com/rocketmusic/api/shared/exception/GlobalExceptionHandler.java
package com.rocketmusic.api.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno nos sistemas da Rocket Music.");
        // TODO: Implementar logger (ex: SLF4J) para registrar ex.getMessage() internamente
        return problem;
    }
}