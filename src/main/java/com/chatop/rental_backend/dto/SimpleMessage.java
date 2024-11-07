package com.chatop.rental_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Message")
//@formatter:off
public record SimpleMessage(

    @Schema(description = "A descriptive message as result for this action",
      example = "The rental was created!")
    String message

) {
}
