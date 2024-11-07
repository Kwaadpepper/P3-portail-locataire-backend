package com.chatop.rental_backend.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Rental list")
//@formatter:off
public record RentalListDto(

    @Schema(description = "A non paginated list of rentals")
    List<RentalDto> rentals

) {

  @Schema(name = "Rental with only 1 picture")
  public record RentalDto(

      @Schema(description = "A chatop rental id", example = "3")
      Long id,
      String name,

      @Schema(description = "Surface in square meters", example = "120")
      Integer surface,

      @Schema(description = "Price per night in euros", example = "80")
      Integer price,

      @Schema(description = "A picture for the rental",
        example = "http://localhost:3001/public/rental/24/11/05/de/picture1.png")
      String picture,

      @Schema(description = "A complete description of the property",
        example = "Well equiped house with a swimming pool...")
      String description,

      @Schema(description = "A chatop user id", example = "6")
      Long owner_id,

      @Schema(description = "The creation date for this rental'",
        example = "2024/11/06", format = "YYYY/MM/DD")
      String created_at,

      @Schema(description = "The updated date for this rental",
        example = "2024/11/07", format = "YYYY/MM/DD")
      String updated_at
    ) {
    }
}
