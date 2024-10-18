package com.chatop.rental_backend.requests.auth;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
    //
    @NotNull @Parameter(in = ParameterIn.HEADER, example = "sdsd") String email,
    @NotNull @Parameter(in = ParameterIn.DEFAULT) String name,
    @NotNull @Parameter(in = ParameterIn.DEFAULT) String password
//
) {
}
