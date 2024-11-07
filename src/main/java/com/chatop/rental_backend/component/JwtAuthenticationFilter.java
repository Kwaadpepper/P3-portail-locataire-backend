package com.chatop.rental_backend.component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.chatop.rental_backend.exception.exceptions.JwtAuthenticationFailureException;
import com.chatop.rental_backend.exception.exceptions.JwtExpiredException;
import com.chatop.rental_backend.service.AuthenticationService;
import com.chatop.rental_backend.service.JwtService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Check if an Authorization header is set and that it contains a valid JwtToken and that it matches
 * an actual User.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private static final String BEARER_PREFIX = "Bearer ";

  private final AuthenticationService authenticationService;
  private final JwtService jwtService;
  private final HandlerExceptionResolver exceptionResolver;

  public JwtAuthenticationFilter(final AuthenticationService authenticationService,
      final JwtService jwtService,
      @Qualifier("handlerExceptionResolver") final HandlerExceptionResolver exceptionResolver) {

    this.authenticationService = authenticationService;
    this.jwtService = jwtService;
    this.exceptionResolver = exceptionResolver;
  }

  @Override
  protected void doFilterInternal(final HttpServletRequest request,
      final HttpServletResponse response, final FilterChain filterChain)
      throws IOException, ServletException {
    try {
      final String jwtToken;
      final UserDetails userDetails;

      if (!hasAuthorizeHeader(request)) {
        logger.debug("Request does not have 'Authorize' header, skipping authentication.");
        filterChain.doFilter(request, response);
        return;
      }

      jwtToken = extractTokenFromAuthorizeHeader(request);
      if (checkIfTokenIsExpired(jwtToken)) {
        logger.debug("JwtToken is expired");
        throw new JwtExpiredException();
      }

      userDetails = getUserDetailsFromJwtToken(jwtToken).orElseThrow(() -> {
        logger.debug("No user with the provided JwtToken");
        throw new JwtAuthenticationFailureException("JwtToken does not match any user");
      });

      logger
          .debug("User authenticated via JwtTokenFilter '%s'".formatted(userDetails.getUsername()));

      /**
       * Set a security context so user is authenticated on controllers and beyond
       */
      final var securityContext = getNewSecurityContext(request, userDetails, jwtToken);
      SecurityContextHolder.setContext(securityContext);

      // Pursue the filter chain.
      filterChain.doFilter(request, response);
    } catch (final Exception e) {
      // Dispatch the exception to our Global handler.
      exceptionResolver.resolveException(request, response, null, e);
    }
  }

  private boolean checkIfTokenIsExpired(final String jwtToken) {
    try {
      return jwtService.hasTokenExpired(jwtToken);
    } catch (final JwtException e) {
      logger.debug(e.getMessage(), e);
      return false;
    }
  }

  private String extractTokenFromAuthorizeHeader(final HttpServletRequest request) {
    final var authHeader = request.getHeader("Authorization");
    return authHeader.substring(BEARER_PREFIX.length());
  }

  private Optional<UUID> getApiTokenFromJwtToken(final String jwtToken) {
    try {
      return Optional.of(UUID.fromString(jwtToken));
    } catch (final IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  private SecurityContext getNewSecurityContext(final HttpServletRequest request,
      final UserDetails userDetails, final String jwtToken) {
    final var context = SecurityContextHolder.createEmptyContext();
    final var authToken = new UsernamePasswordAuthenticationToken(userDetails, jwtToken,
        userDetails.getAuthorities());
    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    context.setAuthentication(authToken);
    return context;
  }

  private Optional<UserDetails> getUserDetailsFromJwtToken(final String jwtToken) {
    try {
      final var extractedApiToken = jwtService.extractApiToken(jwtToken);
      final var userApiToken = getApiTokenFromJwtToken(extractedApiToken)
          .orElseThrow(() -> new UsernameNotFoundException("Api token is not valid"));
      final var user = authenticationService.authenticate(userApiToken);
      return Optional.of(user);
    } catch (final BadCredentialsException e) {
      logger.debug("Wrong api token provided in the jwt token");
      return Optional.empty();
    } catch (final JwtException e) {
      logger
          .debug("Failed to get ApiToken from the JwtToken reason '%s'".formatted(e.getMessage()));
      return Optional.empty();
    }
  }

  private boolean hasAuthorizeHeader(final HttpServletRequest request) {
    final var authHeader = request.getHeader("Authorization");

    return authHeader != null && !authHeader.isBlank() && authHeader.startsWith(BEARER_PREFIX);
  }
}
