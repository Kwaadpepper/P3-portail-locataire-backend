package com.chatop.rental_backend.controller;

import java.io.IOException;
import java.util.regex.Pattern;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatop.rental_backend.exception.exceptions.ResourceNotFoundException;
import com.chatop.rental_backend.exception.exceptions.storage.StorageFileNotFoundException;
import com.chatop.rental_backend.exception.exceptions.storage.StorageIoException;
import com.chatop.rental_backend.service.MimeTypeService;
import com.chatop.rental_backend.service.storage.StorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class StorageController {
  private final StorageService storageService;
  private final MimeTypeService mimeTypeService;

  public StorageController(final StorageService storageService,
      final MimeTypeService mimeTypeService) {
    this.storageService = storageService;
    this.mimeTypeService = mimeTypeService;
  }

  /** Serve storage/public files that were stored using our FileStoragehelper */
  @Operation(summary = "Get a file from public storage", description = "Output a file that was stored internally")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(schema = @Schema(implementation = Resource.class))),
      @ApiResponse(responseCode = "404", description = "Not found - The file was not found")
  })
  @GetMapping(value = "/public/**", produces = MediaType.ALL_VALUE)
  public ResponseEntity<Resource> servePublicFile(final HttpServletRequest request)
      throws IOException, StorageIoException {
    try {
      final Resource file;
      final var filePath = request.getRequestURI().split(request.getContextPath() + "/public/")[1];

      // Check the path is valid storage file path to prevent server exploring from
      // outside.
      if (!Pattern.matches("[a-zA-Z0-9_~\\/\\-]+\\.[a-z]+$", filePath)) {
        throw new ResourceNotFoundException("File not found");
      }

      file = storageService.loadAsResource(filePath);

      return ResponseEntity.ok()
          // formatter:off
          .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.contentLength()))
          .header(HttpHeaders.CONTENT_TYPE, mimeTypeService.getMimeType(file.getInputStream()))
          .header(HttpHeaders.CONTENT_DISPOSITION,
              "inline; filename=\"" + file.getFilename() + "\"")
          .body(file);
      // formatter:on
    } catch (final StorageFileNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }
}
