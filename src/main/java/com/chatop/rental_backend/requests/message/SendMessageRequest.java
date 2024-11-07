package com.chatop.rental_backend.requests.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "Sending message parameters")
//@formatter:off
public record SendMessageRequest(

    @Schema(description = "A plain text message",
      example = "Hi,\n I would like to rent your property...")
    @NotNull
    @Size(min = 4, max = 2000)
    String message,

    @Schema(description = "A Chatop user id", example = "1")
    @JsonProperty("user_id")
    @NotNull
    @Min(value = 1)
    Long userId,

    @Schema(description = "A Chatop rental id", example = "1")
    @JsonProperty("rental_id")
    @NotNull()
    @Min(value = 1)
    Long rentalId

) {
}
