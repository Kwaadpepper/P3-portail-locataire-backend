package com.chatop.rental_backend.lib.auth;

import java.util.Collection;
import java.util.UUID;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import com.chatop.rental_backend.configuration.AppConfiguration;

/**
 * Inspired from
 * {@link org.springframework.security.authentication.UsernamePasswordAuthenticationToken}.
 * This
 * implementation is designed for a simple presentation of an API token.
 *
 */
public class ApiAuthenticationToken extends AbstractAuthenticationToken {
  private static final long serialVersionUID = AppConfiguration.SERIAL_VERSION_UID;

  private final Object principal;
  private final @Nullable Object credentials;

  /**
   * This constructor should only be used by <code>AuthenticationManager</code> or
   * <code>AuthenticationProvider</code> implementations that are satisfied with
   * producing a trusted
   * (i.e. {@link #isAuthenticated()} = <code>true</code>) authentication token.
   *
   * @param principal
   * @param credentials
   * @param authorities
   */
  public ApiAuthenticationToken(final Object principal, final Object credentials,
      final Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.principal = principal;
    this.credentials = credentials;
    super.setAuthenticated(true); // must use super, as we override
  }

  /**
   * This constructor can be safely used by any code that wishes to create a
   * <code>ApiAuthentificationToken</code>, as the {@link #isAuthenticated()} will
   * return
   * <code>false</code>.
   *
   */
  public ApiAuthenticationToken(final UUID apiToken) {
    super(null);
    principal = apiToken;
    credentials = null;
    setAuthenticated(false);
  }

  public static ApiAuthenticationToken unauthenticated(final UUID principal) {
    return new ApiAuthenticationToken(principal);
  }

  @Override
  public Object getCredentials() {
    return credentials;
  }

  @Override
  public String getPrincipal() {
    return getToken();
  }

  public String getToken() {
    return principal.toString();
  }

  @Override
  public void setAuthenticated(final boolean isAuthenticated) throws IllegalArgumentException {
    Assert.isTrue(!isAuthenticated,
        "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
    super.setAuthenticated(false);
  }

}
