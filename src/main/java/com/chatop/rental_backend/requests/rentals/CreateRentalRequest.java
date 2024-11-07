package com.chatop.rental_backend.requests.rentals;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import com.chatop.rental_backend.model.Rental;
import com.chatop.rental_backend.model.User;
import com.chatop.rental_backend.requests.CreateRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "Creating rental parameters")
public final class CreateRentalRequest implements CreateRequest<Rental> {

  @Schema(description = "The name for the rental", example = "Super flat nearby NewYork")
  @NotBlank
  @Size(min = 3, max = 255)
  final String name;

  @Schema(description = "Surface in square meters", example = "120")
  @NotNull
  @Min(value = 1)
  Integer surface;

  @Schema(description = "Price per night in euros", example = "80")
  @NotNull
  @Min(value = 1)
  Integer price;

  @Schema(description = "A complete description of the property",
      example = "Well equiped house with a swimming pool...")
  @NotNull
  @Size(min = 4, max = 2000)
  String description;

  @Schema(
      description = "A nice picture of the rental, it should be jpeg, png or gif, max size is ${spring.servlet.multipart.max-file-size}")
  @NotNull
  MultipartFile picture;

  CreateRentalRequest(final String name, final Integer surface, final Integer price,
      final String description, final MultipartFile picture) {
    this.name = name;
    this.surface = surface;
    this.price = price;
    this.description = description;
    this.picture = picture;
  }

  @Override
  public Rental create(@NonNull final User authenticated) {
    return new Rental(name, surface, price, List.of(), description, authenticated);
  }

  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
  }

  public MultipartFile getPicture() {
    return picture;
  }

  public Integer getPrice() {
    return price;
  }

  public Integer getSurface() {
    return surface;
  }

  @Override
  public String toString() {
    return "SendMessageRequest(name: " + name + ", surface: " + surface + ", price: " + price
        + ", description: " + description + ")";
  }
}
