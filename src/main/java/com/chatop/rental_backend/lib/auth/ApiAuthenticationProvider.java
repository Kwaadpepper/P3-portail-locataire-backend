package com.chatop.rental_backend.lib.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.chatop.rental_backend.service.user_details.ApiUserDetailsService;

/** Authentication provider for user api token */
public final class ApiAuthenticationProvider implements AuthenticationProvider {
  private static final Log logger = LogFactory.getLog(ApiAuthenticationProvider.class);

  private final ApiUserDetailsService userDetailsService;
  private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

  public ApiAuthenticationProvider(final ApiUserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    try {
      final var apiToken = (ApiAuthenticationToken) authentication;
      final var userDetails = userDetailsService.loadUserByUsername(apiToken.getToken());

      return toUserAuthentication(userDetails, apiToken);
    } catch (final UsernameNotFoundException e) {
      logger.debug("Api token not found '%s'".formatted(authentication.getName()));
      throw e;
    }
  }

  @Override
  public boolean supports(final Class<?> authentication) {
    return ApiAuthenticationToken.class.isAssignableFrom(authentication);
  }

  /** To User Authentication */
  protected Authentication toUserAuthentication(final UserDetails userDetails,
      final Authentication authentication) {
    final var result = UsernamePasswordAuthenticationToken.authenticated(userDetails,
        authentication.getCredentials(),
        authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));
    result.setDetails(authentication.getDetails());
    return result;
  }
}
