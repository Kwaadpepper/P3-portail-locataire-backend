package com.chatop.rental_backend.service.user_details;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.chatop.rental_backend.repository.UserRepository;

public class LoginUserDetailsService implements UserDetailsService {
  private static final Logger logger = LogManager.getLogger(LoginUserDetailsService.class);
  private final UserRepository userRepository;

  public LoginUserDetailsService(final UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(final String userEmail) throws UsernameNotFoundException {
    final var notFoundMessage = "Wrong username '%s'".formatted(userEmail);
    try {
      return userRepository.findByEmail(userEmail).orElseThrow(() -> {
        logger.debug(notFoundMessage);
        throw new UsernameNotFoundException("Wrong username");
      });
    } catch (final IllegalArgumentException e) {
      logger.debug(notFoundMessage);
      throw new UsernameNotFoundException(notFoundMessage);
    }
  }
}
