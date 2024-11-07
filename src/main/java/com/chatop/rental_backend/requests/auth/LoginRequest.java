package com.chatop.rental_backend.requests.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(name = "Login parameters")
//@formatter:off
public record LoginRequest(

    @Schema(description = "The user plain text email")
    @NotNull
    @NotEmpty
    String email,

    @Schema(description = "The user plain text password")
    @NotNull
    @NotEmpty
    String password

) {
}
