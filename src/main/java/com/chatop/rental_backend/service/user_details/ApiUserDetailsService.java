package com.chatop.rental_backend.service.user_details;

import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.chatop.rental_backend.repository.UserRepository;

public class ApiUserDetailsService implements UserDetailsService {
  private static final Logger logger = LogManager.getLogger(ApiUserDetailsService.class);
  private final UserRepository userRepository;

  public ApiUserDetailsService(final UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(final String apiToken) throws UsernameNotFoundException {
    final var notFoundMessage = "Wrong api token '%s'".formatted(apiToken);
    try {
      return userRepository.findByApiToken(UUID.fromString(apiToken)).orElseThrow(() -> {
        logger.debug(notFoundMessage);
        throw new UsernameNotFoundException(notFoundMessage);
      });
    } catch (final IllegalArgumentException e) {
      logger.debug(notFoundMessage);
      throw new UsernameNotFoundException(notFoundMessage);
    }
  }
}
