package com.chatop.rental_backend.requests.auth;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(
    //
    @NotNull String email, //
    @NotNull String password) {
}
