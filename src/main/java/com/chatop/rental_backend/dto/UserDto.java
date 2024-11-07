package com.chatop.rental_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "User")
//@formatter:off
public record UserDto(

    @Schema(description = "A chatop rental id", example = "3")
    Long id,

    @Schema(description = "The firstname and lastname of the user", example = "Jean Petit")
    String name,

    @Schema(description = "The user email", example = "user@example.com")
    String email,

    @Schema(description = "The creation date for this user",
      example = "2024/11/07", format = "YYYY/MM/DD")
    String created_at,

    @Schema(description = "The updated date for this user",
      example = "2024/11/07", format = "YYYY/MM/DD")
    String updated_at

) {
}
