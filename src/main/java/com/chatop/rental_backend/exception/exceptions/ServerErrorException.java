package com.chatop.rental_backend.exception.exceptions;

import com.chatop.rental_backend.configuration.AppConfiguration;

public class ServerErrorException extends RuntimeException {
  private static final long serialVersionUID = AppConfiguration.SERIAL_VERSION_UID;

  public ServerErrorException(final String message) {
    super(message);
  }

  public ServerErrorException(final String message, final Throwable previous) {
    super(message, previous);
  }
}
