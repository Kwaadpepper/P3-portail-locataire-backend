package com.chatop.rental_backend.service;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chatop.rental_backend.dto.JwtDto;
import com.chatop.rental_backend.exception.exceptions.ServerErrorException;
import com.chatop.rental_backend.exception.exceptions.ValidationException;
import com.chatop.rental_backend.lib.auth.ApiAuthenticationToken;
import com.chatop.rental_backend.model.User;
import com.chatop.rental_backend.repository.UserRepository;

/** This is used for authentication operations */
@Service
public class AuthenticationService {
  private static final Logger logger = LogManager.getLogger(AuthenticationService.class);
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationService(final UserRepository userRepository, final JwtService jwtService,
      final AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    passwordEncoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
  }

  public static User toUser(final Authentication authentication) throws ServerErrorException {
    final var principal = authentication.getPrincipal();

    if (!(principal instanceof User)) {
      logger.debug("Given authentication principal is not a User instance.");
      throw new ServerErrorException("Expected principal to be a '%s' instance given is '%s'"
          .formatted(User.class, principal.getClass()));
    }
    return (User) principal;
  }

  /** Authenticate a user using login and password */
  public JwtDto authenticate(final String login, final String password)
      throws BadCredentialsException {
    try {
      final var authentication = authenticationManager
          .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(login, password));
      final var user = toUser(authentication);
      return getJwtTokenFromUser(user);
    } catch (LockedException | DisabledException e) {
      logger.debug("The use account is '%s'".formatted(e.getClass().getSimpleName()));
      throw new BadCredentialsException("Account cannot be used for the moment", e);
    }
  }

  /** Authenticate a user using ApiToken */
  public User authenticate(final UUID apiToken) throws BadCredentialsException {
    try {
      final var authentication =
          authenticationManager.authenticate(ApiAuthenticationToken.unauthenticated(apiToken));
      return toUser(authentication);
    } catch (LockedException | DisabledException e) {
      logger.debug("The use account is '%s'".formatted(e.getClass().getSimpleName()));
      throw new BadCredentialsException("Account cannot be used for the moment", e);
    }
  }

  /** Register user and get a new JwtToken */
  public JwtDto register(final String email, final String name, final String password)
      throws ValidationException {
    final User user;

    if (userRepository.findByEmail(email).isPresent()) {
      logger.debug("The email is already used so we cannot create an account with it.");
      throw new ValidationException("There is already an account with this email");
    }

    user = new User(email, name, passwordEncoder.encode(password), UUID.randomUUID());
    userRepository.save(user);
    return getJwtTokenFromUser(user);
  }

  private JwtDto getJwtTokenFromUser(final User user) {
    final var jwtToken = jwtService.generateToken(user.getApiToken());
    return JwtDto.of(jwtToken);
  }
}
