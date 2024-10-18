package com.chatop.rental_backend.dto;

import java.util.List;

public record RentalDto(Long id, String name, Integer surface, Integer price, List<String> picture,
    String description, Long owner_id, String created_at, String updated_at) {
}
