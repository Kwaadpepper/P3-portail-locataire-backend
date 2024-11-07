package com.chatop.rental_backend.requests.rentals;

import java.time.ZonedDateTime;

import com.chatop.rental_backend.model.Rental;
import com.chatop.rental_backend.requests.UpdateRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "Updating rental parameters")
public final class UpdateRentalRequest implements UpdateRequest<Rental> {

  @NotBlank
  @Size(min = 3, max = 255)
  String name;

  @NotNull
  @Min(value = 1)
  Integer surface;

  @NotNull
  @Min(value = 1)
  Integer price;

  @NotNull
  @Size(min = 4, max = 2000)
  String description;

  UpdateRentalRequest(final String name, final Integer surface, final Integer price,
      final String description) {
    this.name = name;
    this.surface = surface;
    this.price = price;
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
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

  @Override
  public void update(final Rental model) {
    model.setName(name);
    model.setPrice(price);
    model.setSurface(surface);
    model.setDescription(description);
    model.setUpdatedAt(ZonedDateTime.now());
  }
}
