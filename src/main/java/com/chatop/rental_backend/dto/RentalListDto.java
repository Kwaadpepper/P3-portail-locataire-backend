package com.chatop.rental_backend.dto;

import java.util.List;

public record RentalListDto(List<RentalDto> rentals) {
  public record RentalDto(Long id, String name, Integer surface, Integer price, String picture,
      String description, Long owner_id, String created_at, String updated_at) {
  }
}
