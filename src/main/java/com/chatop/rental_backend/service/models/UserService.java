package com.chatop.rental_backend.service.models;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.chatop.rental_backend.model.User;
import com.chatop.rental_backend.repository.UserRepository;

@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(final UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void deleteUser(final Long id) {
    userRepository.deleteById(id);
  }

  public Optional<User> getUser(final Long id) {
    return userRepository.findById(id);
  }

  public Optional<User> getUserFromApiToken(final UUID apiToken) {
    return userRepository.findByApiToken(apiToken);
  }

  public Optional<User> getUserFromEmail(final String email) {
    return userRepository.findByEmail(email);
  }

  public Iterable<User> getUsers() {
    return userRepository.findAll();
  }

  public User saveUser(final User user) {
    return userRepository.save(user);
  }

}
