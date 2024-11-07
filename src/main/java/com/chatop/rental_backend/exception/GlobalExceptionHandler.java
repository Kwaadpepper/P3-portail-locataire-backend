package com.chatop.rental_backend.exception;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.chatop.rental_backend.dto.ApiErrorDetails;
import com.chatop.rental_backend.dto.ValidationErrorDetails;
import com.chatop.rental_backend.exception.exceptions.JwtAuthenticationFailureException;
import com.chatop.rental_backend.exception.exceptions.JwtExpiredException;
import com.chatop.rental_backend.exception.exceptions.ResourceNotFoundException;
import com.chatop.rental_backend.exception.exceptions.ValidationException;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BadCredentialsException.class)
  @ApiResponse(responseCode = "401", description = "User could not be authenticated",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ApiErrorDetails.class)))
  public ResponseEntity<ApiErrorDetails> handleException(final BadCredentialsException ex,
      final WebRequest request) {
    return new ResponseEntity<>(toErrorDetails("Bad credentials", request),
        HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(JwtAuthenticationFailureException.class)
  @ApiResponse(responseCode = "401", description = "Jwt token could not be authenticated",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ApiErrorDetails.class)))
  public ResponseEntity<ApiErrorDetails> handleException(final JwtAuthenticationFailureException ex,
      final WebRequest request) {
    return new ResponseEntity<>(toErrorDetails(ex, request), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(JwtExpiredException.class)
  @ApiResponse(responseCode = "401", description = "Jwt token is expired",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ApiErrorDetails.class)))
  public ResponseEntity<ApiErrorDetails> handleException(final JwtExpiredException ex,
      final WebRequest request) {
    return new ResponseEntity<>(toErrorDetails(ex, request), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ApiResponse(responseCode = "404", description = "The wanted resource could not be found",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ApiErrorDetails.class)))
  public ResponseEntity<ApiErrorDetails> handleException(final ResourceNotFoundException ex,
      final WebRequest request) {
    return new ResponseEntity<>(toErrorDetails(ex, request), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Throwable.class)
  @ApiResponse(responseCode = "500", description = "An unmanageable error occurred",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ApiErrorDetails.class)))
  public ResponseEntity<ApiErrorDetails> handleException(final Throwable ex,
      final WebRequest request) {
    logger.fatal("Error : '%s' on uri '%s'".formatted(ex.getMessage(), getRequestUri(request)), ex);
    return new ResponseEntity<>(toErrorDetails("Server error", request),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  @ApiResponse(responseCode = "401", description = "The given credential are invalid",
      content = @Content(mediaType = "application/json"))
  public ResponseEntity<ApiErrorDetails> handleException(final UsernameNotFoundException ex,
      final WebRequest request) {
    return new ResponseEntity<>(toErrorDetails(ex, request), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(ValidationException.class)
  @ApiResponse(responseCode = "400",
      description = "Some fields are invalid, the reason will be on 'message'",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ApiErrorDetails.class)))
  public ResponseEntity<ApiErrorDetails> handleException(final ValidationException ex,
      final WebRequest request) {
    return new ResponseEntity<>(toErrorDetails(ex, request), HttpStatus.BAD_REQUEST);
  }

  @ApiResponse(responseCode = "422",
      description = "Some fields are invalid, the reason will be on 'message'",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ValidationErrorDetails.class)))
  @Override
  @Nullable
  protected ResponseEntity<Object> handleHandlerMethodValidationException(
      final HandlerMethodValidationException ex, final HttpHeaders headers,
      final HttpStatusCode status, final WebRequest request) {

    final Map<String, String> errors = new HashMap<>();
    ex.getAllValidationResults().forEach(error -> {
      final var parameterName = error.getMethodParameter().getParameterName();
      final var fieldName = toSnakeCase(parameterName != null ? parameterName : "unknown");
      final var errorMessage = error.getResolvableErrors().stream()
          .map(MessageSourceResolvable::getDefaultMessage).toList().toString();
      errors.put(fieldName, errorMessage);
    });

    return new ResponseEntity<>(
        toValidationErrorDetails("Some fields could not be validated", errors, request),
        HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ApiResponse(responseCode = "422",
      description = "Some fields are invalid, the reason will be on 'message'",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ValidationErrorDetails.class)))
  @Nullable
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      final MethodArgumentNotValidException ex, final HttpHeaders headers,
      final HttpStatusCode status, final WebRequest request) {

    final Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      final var fieldName = toSnakeCase(((FieldError) error).getField());
      final var errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    return new ResponseEntity<>(
        toValidationErrorDetails("Some fields could not be validated", errors, request),
        HttpStatus.BAD_REQUEST);
  }

  private String getRequestUri(final WebRequest request) {
    return ((ServletWebRequest) request).getRequest().getRequestURI();
  }

  private ApiErrorDetails toErrorDetails(final String message, final WebRequest request) {
    return new ApiErrorDetails(new Date(), message, getRequestUri(request));
  }

  private ApiErrorDetails toErrorDetails(final Throwable e, final WebRequest request) {
    return new ApiErrorDetails(new Date(), e.getMessage(), getRequestUri(request));
  }

  private String toSnakeCase(final String value) {
    return value.replaceAll("(.)(\\p{Upper}+|\\d+)", "$1_$2").toLowerCase();
  }

  private ValidationErrorDetails toValidationErrorDetails(final String message,
      final Map<String, String> errors, final WebRequest request) {
    return new ValidationErrorDetails(new Date(), message, errors, getRequestUri(request));
  }

}
