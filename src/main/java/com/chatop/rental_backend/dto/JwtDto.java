package com.chatop.rental_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "JWT Token")
//@formatter:off
public record JwtDto(

    @Schema(description = "A jwt token you may provide in Authorization header for your future requests, Authorization: Bearer ....")
    String token
) {
  public static JwtDto of(final String token) {
    return new JwtDto(token);
  }
}
