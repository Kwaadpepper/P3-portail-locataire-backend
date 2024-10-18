package com.chatop.rental_backend.requests.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(
    //
    @NotNull @Size(min = 4, max = 2000) String message,

    @JsonProperty("user_id") @NotNull @Min(value = 1) Long userId,

    @JsonProperty("rental_id") @NotNull() @Min(value = 1) Long rentalId
//
) {
}
