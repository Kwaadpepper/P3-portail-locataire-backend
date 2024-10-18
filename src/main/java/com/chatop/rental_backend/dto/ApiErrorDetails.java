package com.chatop.rental_backend.dto;

import java.util.Date;

public record ApiErrorDetails(Date timestamp, String message, String uri) {
}
