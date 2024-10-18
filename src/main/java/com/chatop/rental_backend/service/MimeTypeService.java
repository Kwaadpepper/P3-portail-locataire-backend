package com.chatop.rental_backend.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

/** This is used to determine files mime types */
@Service
public class MimeTypeService {
  private final Tika tika;

  public MimeTypeService() {
    tika = new Tika();
  }

  public String getMimeType(final InputStream data) throws IOException {
    return tika.detect(data);
  }

  public boolean validMimeTypeBetween(final List<String> mimeTypes, final InputStream data)
      throws IOException {
    final var detected = getMimeType(data);

    return mimeTypes.contains(detected);
  }

}
