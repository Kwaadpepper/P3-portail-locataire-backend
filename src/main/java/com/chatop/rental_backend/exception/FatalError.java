package com.chatop.rental_backend.exception;

import com.chatop.rental_backend.configuration.AppConfiguration;

public class FatalError extends Error {
  private static final long serialVersionUID = AppConfiguration.SERIAL_VERSION_UID;

  public FatalError(final String message) {
    super(message);
  }

  public FatalError(final String message, final Throwable previous) {
    super(message, previous);
  }
}
