package com.chatop.rental_backend.dto;

import java.util.Date;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Validation error details")
//@formatter:off
public record ValidationErrorDetails(

    @Schema(description = "The timestamp for this error", example= "2024-11-07T09:50:00.291+00:00")
    Date timestamp,

    @Schema(description = "A descriptive message for the error", example= "Some fields could not be validated")
    String message,

    @Schema(description = "A descriptive message for the error")
    Map<String, String> errors,

    @Schema(description = "The uri used that was used", example = "/api/users/jean")
    String uri

) {
}
