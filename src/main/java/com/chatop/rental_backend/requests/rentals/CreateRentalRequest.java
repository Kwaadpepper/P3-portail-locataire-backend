package com.chatop.rental_backend.requests.rentals;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import com.chatop.rental_backend.model.Rental;
import com.chatop.rental_backend.model.User;
import com.chatop.rental_backend.requests.CreateRequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class CreateRentalRequest implements CreateRequest<Rental> {

  @NotBlank
  @Size(min = 3, max = 255)
  final String name;

  @NotNull
  @Min(value = 1)
  Integer surface;

  @NotNull
  @Min(value = 1)
  Integer price;

  @NotNull
  @Size(min = 4, max = 2000)
  String description;

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
