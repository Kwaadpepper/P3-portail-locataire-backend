package com.chatop.rental_backend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/** Hold our application configuration */
@Configuration
public class AppConfiguration {
  public static final long SERIAL_VERSION_UID = 1L;

  public static final String PUBLIC_DIR_NAME = "public";
  public static final String PRIVATE_DIR_NAME = "private";

  @Value("${jwt.signing.secret_key}")
  public String jwtSigningSecretKey;

  @Value("${application.upload.storage_path}")
  public String storageDirectoryName = "storage";

  @Value("${springdoc.api-docs.enabled}")
  public boolean apiDocEnabled;

  @Value("${springdoc.swagger-ui.enabled}")
  public boolean swaggerUiEnabled;
}
