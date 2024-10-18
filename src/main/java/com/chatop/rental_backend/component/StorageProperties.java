package com.chatop.rental_backend.component;

import java.nio.file.Path;
import org.springframework.stereotype.Component;

import com.chatop.rental_backend.configuration.AppConfiguration;

/** Holds properties of current app storage */
@Component
public class StorageProperties {
  private final String storageDirectoryName;
  private final String publicDirectoryName;
  private final String privateDirectoryName;

  private final Path publicDirectoryPath;
  private final Path privateDirectoryPath;

  public StorageProperties(final AppConfiguration appConfiguration) {
    storageDirectoryName = appConfiguration.storageDirectoryName;
    publicDirectoryName = AppConfiguration.PUBLIC_DIR_NAME;
    privateDirectoryName = AppConfiguration.PRIVATE_DIR_NAME;

    publicDirectoryPath = getPublicPath();
    privateDirectoryPath = getPrivatePath();
  }

  public Path getPrivateDirectoryPath() {
    return privateDirectoryPath;
  }

  public Path getPublicDirectoryPath() {
    return publicDirectoryPath;
  }

  private Path getPrivatePath() {
    return Path.of(storageDirectoryName, privateDirectoryName);
  }

  private Path getPublicPath() {
    return Path.of(storageDirectoryName, publicDirectoryName);
  }
}
