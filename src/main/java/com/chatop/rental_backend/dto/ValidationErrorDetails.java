package com.chatop.rental_backend.dto;

import java.util.Date;
import java.util.Map;

public record ValidationErrorDetails(Date timestamp, String message, Map<String, String> errors,
    String uri) {
}
