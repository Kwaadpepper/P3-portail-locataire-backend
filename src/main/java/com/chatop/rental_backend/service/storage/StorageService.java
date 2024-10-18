package com.chatop.rental_backend.service.storage;

import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.chatop.rental_backend.exception.exceptions.storage.StorageFileNotFoundException;
import com.chatop.rental_backend.exception.exceptions.storage.StorageIoException;
import com.chatop.rental_backend.model.Model;

/** This is used to manage files */
public interface StorageService {

  default boolean exists(final String filePath) throws StorageIoException {
    return exists(filePath, true);
  }

  boolean exists(final String filePath, final boolean inPublic) throws StorageIoException;

  default Resource loadAsResource(final String filePath)
      throws StorageFileNotFoundException, StorageIoException {
    return loadAsResource(filePath, true);
  }

  Resource loadAsResource(final String filename, boolean fromPublic)
      throws StorageFileNotFoundException, StorageIoException;

  default boolean remove(final String filePath) throws StorageIoException {
    return remove(filePath, true);
  }

  boolean remove(final String filePath, final boolean fromPublic) throws StorageIoException;

  default <M extends Model> Path store(final MultipartFile file, final Class<M> modelClass)
      throws StorageIoException {
    return store(file, modelClass, true);
  }

  <M extends Model> Path store(final MultipartFile file, final Class<M> modelClass,
      final boolean inPublic) throws StorageIoException;
}
