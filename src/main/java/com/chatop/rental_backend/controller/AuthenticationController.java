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

import com.chatop.rental_backend.dto.ApiErrorDetails;
import com.chatop.rental_backend.dto.JwtDto;
import com.chatop.rental_backend.dto.UserDto;
import com.chatop.rental_backend.dto.ValidationErrorDetails;
import com.chatop.rental_backend.exception.exceptions.ValidationException;
import com.chatop.rental_backend.model.User;
import com.chatop.rental_backend.presenter.UserPresenter;
import com.chatop.rental_backend.requests.auth.LoginRequest;
import com.chatop.rental_backend.requests.auth.RegisterRequest;
import com.chatop.rental_backend.service.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Authentication")
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
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved",
          content = @Content(schema = @Schema(implementation = JwtDto.class))),
      @ApiResponse(responseCode = "401", description = "User could not be authenticated",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ApiErrorDetails.class)))})
  @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserDto> getAuthenticatedUserDetails() {
    final var user = getAuthenticatedUser();

    return ResponseEntity.ok().body(userPresenter.present(user));
  }

  /** Authenticate a user */
  @Operation(operationId = "login", summary = "Authentication for a user",
      description = "Authenticate a user and have a JWT token as response")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully authenticated",
          content = @Content(schema = @Schema(implementation = JwtDto.class))),
      @ApiResponse(responseCode = "400",
          description = "Some fields are invalid, the reason will be on 'message'",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ValidationErrorDetails.class))),
      @ApiResponse(responseCode = "401", description = "User could not be authenticated",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ApiErrorDetails.class)))})
  @SecurityRequirements()
  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<JwtDto> login(@Valid @RequestBody final LoginRequest request)
      throws BadCredentialsException {

    final var jwtDto = authenticationService.authenticate(request.email(), request.password());

    return ResponseEntity.ok(jwtDto);
  }

  /** Register a user */
  @Operation(operationId = "register", summary = "Register for a user",
      description = "Register a user and have a JWT token as response",
      responses = {@ApiResponse(responseCode = "200",
          description = "jwt token to be used on secured endpoints")})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully registered",
          content = @Content(schema = @Schema(implementation = JwtDto.class))),
      @ApiResponse(responseCode = "400",
          description = "Some fields are invalid, the reason will be on 'message'",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ValidationErrorDetails.class)))})
  @SecurityRequirements()
  @Transactional
  @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<JwtDto> register(@Valid @RequestBody final RegisterRequest request)
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
