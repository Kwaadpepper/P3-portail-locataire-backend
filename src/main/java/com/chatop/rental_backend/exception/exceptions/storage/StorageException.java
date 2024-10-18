package com.chatop.rental_backend.exception.exceptions.storage;

import com.chatop.rental_backend.configuration.AppConfiguration;

public abstract class StorageException extends Exception {
  private static final long serialVersionUID = AppConfiguration.SERIAL_VERSION_UID;

  protected StorageException(final String message) {
    super(message);
  }

  protected StorageException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
