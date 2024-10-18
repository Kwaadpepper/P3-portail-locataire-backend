package com.chatop.rental_backend.requests;

import com.chatop.rental_backend.model.Model;

public interface UpdateRequest<M extends Model> {
  void update(M model);
}
