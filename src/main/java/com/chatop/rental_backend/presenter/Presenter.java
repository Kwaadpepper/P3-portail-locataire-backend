package com.chatop.rental_backend.presenter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public interface Presenter<U extends Record, M> {
  public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd");

  public static String format(final ZonedDateTime dateTime) {
    return dateTime.format(dateFormat);
  }

  U present(M model);
}
