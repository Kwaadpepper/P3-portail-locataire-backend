package com.chatop.rental_backend.exception.exceptions;

import com.chatop.rental_backend.configuration.AppConfiguration;

public class ResourceNotFoundException extends RuntimeException {
  private static final long serialVersionUID = AppConfiguration.SERIAL_VERSION_UID;

  public ResourceNotFoundException(final String message) {
    super(message);
  }

  public ResourceNotFoundException(final String message, final Throwable previous) {
    super(message, previous);
  }
}
