package com.chatop.rental_backend.requests;

import org.springframework.lang.Nullable;
import com.chatop.rental_backend.model.Model;
import com.chatop.rental_backend.model.User;

public interface CreateRequest<M extends Model> {
  M create(@Nullable User authenticated);
}
