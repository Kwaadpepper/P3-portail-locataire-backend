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
import org.springframework.web.bind.annotation.ResponseStatus;
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

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<ApiErrorDetails> handleException(final BadCredentialsException ex,
      final WebRequest request) {
    return new ResponseEntity<>(toErrorDetails("Bad credentials", request),
        HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(JwtAuthenticationFailureException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ResponseEntity<ApiErrorDetails> handleException(final JwtAuthenticationFailureException ex,
      final WebRequest request) {
    return new ResponseEntity<>(toErrorDetails(ex, request), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(JwtExpiredException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ResponseEntity<ApiErrorDetails> handleException(final JwtExpiredException ex,
      final WebRequest request) {
    return new ResponseEntity<>(toErrorDetails(ex, request), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ApiErrorDetails> handleException(final ResourceNotFoundException ex,
      final WebRequest request) {
    return new ResponseEntity<>(toErrorDetails(ex, request), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Throwable.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleException(final Throwable ex, final WebRequest request) {
    logger.fatal("Error : '%s' on uri '%s'".formatted(ex.getMessage(), getRequestUri(request)), ex);
    return new ResponseEntity<>(toErrorDetails("Server error", request),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ResponseEntity<ApiErrorDetails> handleException(final UsernameNotFoundException ex,
      final WebRequest request) {
    return new ResponseEntity<>(toErrorDetails(ex, request), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ApiErrorDetails> handleException(final ValidationException ex,
      final WebRequest request) {
    return new ResponseEntity<>(toErrorDetails(ex, request), HttpStatus.BAD_REQUEST);
  }

  @Override
  @Nullable
  protected ResponseEntity<Object> handleExceptionInternal(final Exception ex,
      @Nullable final Object body, final HttpHeaders headers, final HttpStatusCode statusCode,
      final WebRequest request) {

    /** Don't handle error if parent did not. */
    if (super.handleExceptionInternal(ex, body, headers, statusCode, request) == null) {
      return null;
    }

    return new ResponseEntity<>(toErrorDetails(ex, request), statusCode);
  }

  @Override
  @Nullable
  protected ResponseEntity<Object> handleHandlerMethodValidationException(
      final HandlerMethodValidationException ex, final HttpHeaders headers,
      final HttpStatusCode status, final WebRequest request) {

    final Map<String, String> errors = new HashMap<>();
    ex.getAllValidationResults().forEach(error -> {
      final var fieldName = toSnakeCase(error.getMethodParameter().getParameterName());
      final var errorMessage = error.getResolvableErrors().stream()
          .map(MessageSourceResolvable::getDefaultMessage).toList().toString();
      errors.put(fieldName, errorMessage);
    });

    return new ResponseEntity<>(
        toValidationErrorDetails("Some fields could not be validated", errors, request),
        HttpStatus.BAD_REQUEST);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
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
