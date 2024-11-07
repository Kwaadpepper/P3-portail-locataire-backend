package com.chatop.rental_backend.model;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.chatop.rental_backend.configuration.AppConfiguration;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(schema = "chatop-oc", name = "users")
public class User implements UserDetails, Model {

  private static final long serialVersionUID = AppConfiguration.SERIAL_VERSION_UID;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false, length = 255, unique = true)
  private String email;

  @Column(nullable = false, length = 255)
  private String name;

  @Column(nullable = false, length = 255)
  private String password;

  @Column(nullable = false)
  private final UUID apiToken;

  @Column(nullable = false)
  private final ZonedDateTime createdAt;

  @Column(nullable = false)
  private ZonedDateTime updatedAt;

  public User(final String email, final String name, final String password, final UUID apiToken) {
    this.email = email;
    this.name = name;
    this.password = password;
    this.apiToken = apiToken;
    createdAt = ZonedDateTime.now();
    updatedAt = ZonedDateTime.now();
  }

  // Required by JPA
  protected User() {
    id = 0;
    email = "";
    name = "";
    password = "";
    apiToken = null;
    createdAt = null;
    updatedAt = null;
  }

  public UUID getApiToken() {
    return apiToken;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public String getEmail() {
    return email;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  @Override
  public String getPassword() {
    return password;
  }

  public ZonedDateTime getUpdatedAt() {
    return updatedAt;
  }

  @Override
  public String getUsername() {
    return apiToken.toString();
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public void setUpdatedAt(final ZonedDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public String toString() {
    return this.getClass().getName() + " [id=" + id + ", email=" + email + ", name=" + name
        + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
  }
}
