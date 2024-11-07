package com.chatop.rental_backend.dto;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Api error details")
//@formatter:off
public record ApiErrorDetails(

    @Schema(description = "The timestamp for this error", example= "2024-11-07T09:50:00.291+00:00")
    Date timestamp,

    @Schema(description = "A descriptive message for the error")
    String message,

    @Schema(description = "The uri used that was used", example = "/api/users/jean")
    String uri

) {
}
