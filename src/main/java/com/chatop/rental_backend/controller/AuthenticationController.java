package com.chatop.rental_backend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatop.rental_backend.dto.JwtDto;
import com.chatop.rental_backend.dto.UserDto;
import com.chatop.rental_backend.exception.exceptions.ValidationException;
import com.chatop.rental_backend.model.User;
import com.chatop.rental_backend.presenter.UserPresenter;
import com.chatop.rental_backend.requests.auth.LoginRequest;
import com.chatop.rental_backend.requests.auth.RegisterRequest;
import com.chatop.rental_backend.service.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
  private final AuthenticationService authenticationService;
  private final UserPresenter userPresenter;

  public AuthenticationController(final AuthenticationService authenticationService,
      final UserPresenter userPresenter) {
    this.authenticationService = authenticationService;
    this.userPresenter = userPresenter;
  }

  /** Get authenticated user */
  @Operation(summary = "Get current user details",
      description = "Get user info for the user matching the JWT token in use")
  @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserDto> getAuthenticatedUserDetails() {
    final var user = getAuthenticatedUser();

    return ResponseEntity.ok().body(userPresenter.present(user));
  }

  /** Authenticate a user */
  @Operation(summary = "Authentication for a user",
      description = "Authenticate a user and have a JWT token as response")
  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<JwtDto> login(@Valid @RequestBody final LoginRequest request)
      throws BadCredentialsException {

    final var jwtDto = authenticationService.authenticate(request.email(), request.password());

    return ResponseEntity.ok(jwtDto);
  }

  /** Register a user */
  @Operation(summary = "Register for a user",
      description = "Register a user and have a JWT token as response")
  @Transactional
  @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<JwtDto> login(@Valid @RequestBody final RegisterRequest request)
      throws ValidationException {

    final var jwtDto =
        authenticationService.register(request.email(), request.name(), request.password());

    return ResponseEntity.ok(jwtDto);
  }

  private User getAuthenticatedUser() {
    final var authentication = SecurityContextHolder.getContext().getAuthentication();
    return AuthenticationService.toUser(authentication);
  }

}
