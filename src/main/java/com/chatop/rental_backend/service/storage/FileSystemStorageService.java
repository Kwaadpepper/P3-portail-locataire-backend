package com.chatop.rental_backend.service.storage;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chatop.rental_backend.component.StorageProperties;
import com.chatop.rental_backend.exception.exceptions.ServerErrorException;
import com.chatop.rental_backend.exception.exceptions.storage.StorageFileNotFoundException;
import com.chatop.rental_backend.exception.exceptions.storage.StorageIoException;
import com.chatop.rental_backend.lib.FileStorageHelper;
import com.chatop.rental_backend.model.Model;

@Service
public class FileSystemStorageService implements StorageService {
  private final Path publicLocation;
  private final Path privateLocation;

  public FileSystemStorageService(final StorageProperties properties) {
    publicLocation = properties.getPublicDirectoryPath();
    privateLocation = properties.getPrivateDirectoryPath();
  }

  @Override
  public boolean exists(final String filePath, final boolean inPublic) throws StorageIoException {
    try {
      final var file = toPathObject(filePath, inPublic);
      final Resource resource = new UrlResource(file.toUri());

      return resource.exists();
    } catch (final MalformedURLException | IOError e) {
      return false;
    }
  }

  @Override
  public Resource loadAsResource(final String filePath, final boolean fromPublic)
      throws StorageFileNotFoundException, StorageIoException {
    final var errorMessage = "Could not read file '%s'".formatted(filePath);
    try {

      final var file = toPathObject(filePath, fromPublic);
      final Resource resource = new UrlResource(file.toUri());

      if (Files.notExists(file)) {
        throw new StorageFileNotFoundException("File '%s' could not be found".formatted(filePath));
      }
      if (!Files.isRegularFile(file)) {
        throw new StorageIoException("This is not a file '%s'".formatted(filePath));
      }
      if (!Files.isReadable(file)) {
        throw new StorageIoException(errorMessage);
      }
      return resource;
    } catch (final MalformedURLException e) {
      throw new StorageFileNotFoundException(errorMessage, e);
    } catch (final IOError e) {
      throw new StorageIoException(errorMessage, e);
    }
  }

  @Override
  public boolean remove(final String filePath, final boolean fromPublic) throws StorageIoException {
    try {
      final var file = toPathObject(filePath, fromPublic);

      if (!Files.isRegularFile(file)) {
        throw new StorageIoException("This is not a file '%s'".formatted(filePath));
      }
      return Files.deleteIfExists(file);
    } catch (final IOException e) {
      throw new StorageIoException("Could not remove file '%s'".formatted(filePath), e);
    }
  }

  @Override
  public <M extends Model> Path store(final MultipartFile file, final Class<M> modelClass,
      final boolean inPublic) throws StorageIoException {
    try {
      var fileName = file.getOriginalFilename();
      fileName = fileName != null ? fileName : file.getName();

      /** Where the file will be stored. */
      final Path destinationDirectory;
      /** Where the file will be stored with the appended filename. */
      final Path destinationPath;
      /** Relative path that can be served by our path. */
      final Path outputPath;
      final InputStream inputStream;
      /** The public or private directory */
      final var storageDirectory = inPublic ? publicLocation : privateLocation;

      assertFileIsNotEmpty(file);

      fileName = FileStorageHelper.sanitizeFileName(fileName);

      destinationDirectory = storageDirectory.resolve(FileStorageHelper.getStoragePath(modelClass, fileName))
          .normalize();
      outputPath = destinationDirectory.resolve(Path.of(fileName));
      destinationPath = outputPath.toAbsolutePath();

      fileName = getFileUniqueName(destinationPath, fileName);

      /** Check that destination directory is in */
      assertDestinationIsInDirectory(destinationDirectory, storageDirectory);

      inputStream = file.getInputStream();

      /** Recursively create destination directories */
      Files.createDirectories(destinationDirectory);

      /** Create file and write all data in it. */
      Files.createFile(destinationPath);
      Files.write(destinationPath, inputStream.readAllBytes());

      return outputPath;
    } catch (final IOException e) {
      throw new StorageIoException("Failed to store file.", e);
    }
  }

  /** A */
  private void assertDestinationIsInDirectory(final Path destinationPath, final Path target)
      throws StorageIoException {
    if (!destinationPath.toAbsolutePath().startsWith(target.toAbsolutePath())) {
      throw new StorageIoException("Cannot store file '%s' outside current directory '%s'."
          .formatted(destinationPath, target));
    }
  }

  /** Assert the file is not empty */
  private void assertFileIsNotEmpty(final MultipartFile file) throws StorageIoException {
    if (file.isEmpty()) {
      throw new StorageIoException("Failed to store empty file.");
    }
  }

  /** Try to get a unique file name */
  private static String getFileUniqueName(Path folderPath, String fileName) {
    var newFileName = fileName;
    var attempts = 0;
    final var maxAttempts = 20;
    while (Files.exists(folderPath.resolve(newFileName))) {
      if (attempts++ >= maxAttempts) {
        throw new ServerErrorException("Failed to find a unique filename");
      }
      newFileName = "%s-%s.%s".formatted(newFileName, uniqId(), FilenameUtils.getExtension(newFileName));
    }
    return newFileName;
  }

  /** Converts a string file path to a Path object */
  private Path toPathObject(final String filePath, final boolean inPublic)
      throws InvalidPathException {
    final var location = inPublic ? publicLocation : privateLocation;
    return location.resolve(filePath);
  }

  /** Create a new unique id using UUID */
  private static String uniqId() {
    return List.of(UUID.randomUUID().toString().split("-"))
        .stream().map(value -> Long.parseLong(value, 16))
        .map(String::valueOf)
        .collect(Collectors.joining(""));
  }
}
