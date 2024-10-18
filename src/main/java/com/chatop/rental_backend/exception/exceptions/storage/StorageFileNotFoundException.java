package com.chatop.rental_backend.exception.exceptions.storage;

import com.chatop.rental_backend.configuration.AppConfiguration;

public class StorageFileNotFoundException extends StorageException {
  private static final long serialVersionUID = AppConfiguration.SERIAL_VERSION_UID;

  public StorageFileNotFoundException(final String message) {
    super(message);
  }

  public StorageFileNotFoundException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
