package com.chatop.rental_backend.exception.exceptions.storage;

import com.chatop.rental_backend.configuration.AppConfiguration;

public class StorageIoException extends StorageException {
  private static final long serialVersionUID = AppConfiguration.SERIAL_VERSION_UID;

  public StorageIoException(final String message) {
    super(message);
  }

  public StorageIoException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
