package com.chatop.rental_backend.exception.exceptions;

import org.springframework.security.core.AuthenticationException;
import com.chatop.rental_backend.configuration.AppConfiguration;

public class JwtExpiredException extends AuthenticationException {
  private static final long serialVersionUID = AppConfiguration.SERIAL_VERSION_UID;

  public JwtExpiredException() {
    super("JWT token is exired");
  }
}
