package com.chatop.rental_backend.presenter;

import org.springframework.stereotype.Component;

import com.chatop.rental_backend.dto.JwtDto;

@Component
public class JwtPresenter implements Presenter<JwtDto, String> {

  @Override
  public JwtDto present(final String token) {
    return new JwtDto(token);
  }
}
