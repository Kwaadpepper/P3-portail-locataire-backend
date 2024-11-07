package com.chatop.rental_backend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatop.rental_backend.dto.ApiErrorDetails;
import com.chatop.rental_backend.dto.UserDto;
import com.chatop.rental_backend.exception.exceptions.ResourceNotFoundException;
import com.chatop.rental_backend.presenter.UserPresenter;
import com.chatop.rental_backend.service.models.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Users")
@RestController
@RequestMapping("/api/user")
public class UserController {
  private final UserService userService;
  private final UserPresenter userPresenter;

  public UserController(final UserService userService, final UserPresenter userPresenter) {
    this.userService = userService;
    this.userPresenter = userPresenter;
  }

  /** Get a specific user */
  @Operation(summary = "Get a user", description = "Get some details about a spcific user")
  @Parameter(required = true, in = ParameterIn.PATH, name = "id", description = "A Chatop user id",
      example = "2")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved",
          content = @Content(schema = @Schema(implementation = UserDto.class))),
      @ApiResponse(responseCode = "404", description = "The wanted resource could not be found",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ApiErrorDetails.class))),
      @ApiResponse(responseCode = "401", description = "User could not be authenticated",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ApiErrorDetails.class)))})
  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserDto> getRentalById(@PathVariable(value = "id") final long userId) {

    final var user = userService.getUser(userId).orElseThrow(
        () -> new ResourceNotFoundException("Rental not found for this id :: " + userId));

    return ResponseEntity.ok().body(userPresenter.present(user));
  }
}
