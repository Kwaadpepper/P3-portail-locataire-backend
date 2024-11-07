package com.chatop.rental_backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chatop.rental_backend.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  /**
   * Retrieves an entity by its apiToken.
   *
   * @param email must not be {@literal null}.
   * @return the entity with the given id or {@literal Optional#empty()} if none found.
   * @throws IllegalArgumentException if {@literal apiToken} is {@literal null}.
   */
  Optional<User> findByApiToken(UUID apiToken);

  /**
   * Retrieves an entity by its email.
   *
   * @param email must not be {@literal null} or blank.
   * @return the entity with the given id or {@literal Optional#empty()} if none found.
   * @throws IllegalArgumentException if {@literal email} is {@literal null}.
   */
  Optional<User> findByEmail(String email);
}
