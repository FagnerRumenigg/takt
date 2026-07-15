package org.fr.controller;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;

import org.fr.dto.ApiResponse;
import org.fr.dto.ErrorDto;
import org.fr.exception.CategoryNotFoundException;
import org.fr.exception.ForbiddenCategoryOperationException;
import org.fr.exception.UnauthorizedException;
import org.fr.exception.TimeEntryNotFoundException;
import org.fr.exception.TimeEntryValidationException;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorDto>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        log.warn("Validation error on {} {}", req.getMethod(), req.getRequestURI(), ex);
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ErrorDto err = new ErrorDto("Validation failed", details, HttpStatus.BAD_REQUEST.value(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(err));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<ErrorDto>> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        log.warn("Constraint violation on {} {}", req.getMethod(), req.getRequestURI(), ex);
        String details = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining("; "));
        ErrorDto err = new ErrorDto("Validation failed", details, HttpStatus.BAD_REQUEST.value(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(err));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<ErrorDto>> handleRuntime(RuntimeException ex, HttpServletRequest req) {
        log.error("Runtime exception on {} {}", req.getMethod(), req.getRequestURI(), ex);
        ErrorDto err = new ErrorDto(ex.getMessage() == null ? "Internal error" : ex.getMessage(), null, HttpStatus.BAD_REQUEST.value(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(err));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<ErrorDto>> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        log.warn("Illegal argument on {} {}", req.getMethod(), req.getRequestURI(), ex);
        String message = ex.getMessage() == null ? "Requisição inválida" : ex.getMessage();
        HttpStatus status = message.contains("não encontrado") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        if (message.contains("credenciais inválidas") || message.contains("Refresh token inválido") || message.contains("não confirmado")) {
            status = HttpStatus.UNAUTHORIZED;
        }
        ErrorDto err = new ErrorDto(message, null, status.value(), req.getRequestURI());
        return ResponseEntity.status(status).body(new ApiResponse<>(err));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<ErrorDto>> handleUnauthorized(UnauthorizedException ex, HttpServletRequest req) {
        log.warn("Unauthorized on {} {}", req.getMethod(), req.getRequestURI(), ex);
        ErrorDto err = new ErrorDto(ex.getMessage(), null, HttpStatus.UNAUTHORIZED.value(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(err));
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorDto>> handleCategoryNotFound(CategoryNotFoundException ex, HttpServletRequest req) {
        log.warn("Category not found on {} {}", req.getMethod(), req.getRequestURI(), ex);
        ErrorDto err = new ErrorDto(ex.getMessage(), null, HttpStatus.NOT_FOUND.value(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(err));
    }

    @ExceptionHandler(ForbiddenCategoryOperationException.class)
    public ResponseEntity<ApiResponse<ErrorDto>> handleForbiddenCategory(ForbiddenCategoryOperationException ex, HttpServletRequest req) {
        log.warn("Forbidden category operation on {} {}", req.getMethod(), req.getRequestURI(), ex);
        ErrorDto err = new ErrorDto(ex.getMessage(), null, HttpStatus.FORBIDDEN.value(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(err));
    }

    @ExceptionHandler(TimeEntryNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorDto>> handleTimeEntryNotFound(TimeEntryNotFoundException ex, HttpServletRequest req) {
        log.warn("Time entry not found on {} {}", req.getMethod(), req.getRequestURI(), ex);
        ErrorDto err = new ErrorDto(ex.getMessage(), null, HttpStatus.NOT_FOUND.value(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(err));
    }

    @ExceptionHandler(TimeEntryValidationException.class)
    public ResponseEntity<ApiResponse<ErrorDto>> handleTimeEntryValidation(TimeEntryValidationException ex, HttpServletRequest req) {
        log.warn("Time entry validation on {} {}", req.getMethod(), req.getRequestURI(), ex);
        ErrorDto err = new ErrorDto(ex.getMessage(), null, HttpStatus.BAD_REQUEST.value(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(err));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorDto>> handleException(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception on {} {}", req.getMethod(), req.getRequestURI(), ex);
        ErrorDto err = new ErrorDto("Internal server error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(err));
    }
}
