package com.chatop.rental_backend.requests.auth;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "Register parameters")
//@formatter:off
public record RegisterRequest(

    @Schema(description = "The user plain text email tht should be a valid email")
    @NotNull
    @NotEmpty
    @Email
    @Parameter(example = "sdsd")
    @Size(min = 4, max = 255)
    String email,

    @Schema(description = "The user plain text name")
    @NotNull
    @NotEmpty
    @Size(min = 4, max = 255)
    String name,

    @Schema(description =
        "The user plain text password, it should be at least 8 chars "
        + "long with mixed case, special and digits chars")
    // Regular expression explanation https://regex101.com/r/dobmk2/1
    @Pattern(message = "password should be at least 8 chars "
        + "long with mixed case, special and digits chars",
        regexp = "^.*"
            // At least 8 char long
            + "(?=.{8,})"
            // Contains letter char
            + "(?=.*\\p{L})"
            // Contains digit char
            + "(?=.*\\p{N})"
            // Contains mixed chars
            + "(?=.*(?=(?=\\p{Ll}+.*\\p{Lu})|(?=\\p{Lu}+.*\\p{Ll})))"
            // Contains special char
            + "(?=.*[\\p{Z}|\\p{S}|\\p{P}])"
            // Anything after
            + ".*$")
    @NotNull
    @NotEmpty
    @Size(min = 8, max = 255)
    String password

) {
}
