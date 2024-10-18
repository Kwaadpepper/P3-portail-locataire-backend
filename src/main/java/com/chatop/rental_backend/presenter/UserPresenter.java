package com.chatop.rental_backend.presenter;

import org.springframework.stereotype.Component;
import com.chatop.rental_backend.dto.UserDto;
import com.chatop.rental_backend.model.User;

@Component
public class UserPresenter implements Presenter<UserDto, User> {

  @Override
  public UserDto present(final User model) {
    return new UserDto(model.getId(), model.getName(), model.getEmail(),
        Presenter.format(model.getCreatedAt()), Presenter.format(model.getUpdatedAt()));
  }
}
