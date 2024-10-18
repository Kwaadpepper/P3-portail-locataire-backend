package com.chatop.rental_backend.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.chatop.rental_backend.configuration.AppConfiguration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
  private final String jwtSigningSecretKey;

  public JwtService(AppConfiguration appConfiguration) {
    this.jwtSigningSecretKey = appConfiguration.jwtSigningSecretKey;
  }

  /** Extract the apiToken from a JwtToken that was set in the subject claim. */
  public String extractApiToken(final String jwtToken) throws JwtException {
    return extractClaim(jwtToken, Claims::getSubject);
  }

  /** Generate a JwtToken with an apiToken in the subject claim. */
  public String generateToken(final UUID apiToken) {
    return generateToken(Map.of(), apiToken);
  }

  /** Check if a JwtToken is expired. */
  public boolean hasTokenExpired(final String jwtToken) throws JwtException {
    try {
      final var expirationDate = extractExpiration(jwtToken);
      return expirationDate.before(new Date());
    } catch (final ExpiredJwtException e) {
      return true;
    }
  }

  /** Attempt to extract the pay load from JWT token. */
  private Claims extractAllClaims(final String jwtToken) throws JwtException {
    return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(jwtToken)
        .getBody();
  }

  /** Get a claim value form the token using a resolve */
  private <T> T extractClaim(final String jwtToken, final Function<Claims, T> claimsResolver)
      throws JwtException {
    final var claims = extractAllClaims(jwtToken);
    return claimsResolver.apply(claims);
  }

  /** Get expiration date from the token. */
  private Date extractExpiration(final String jwtToken) throws JwtException {
    return extractClaim(jwtToken, Claims::getExpiration);
  }

  /** Generate and sign the actual JWT token. */
  private String generateToken(final Map<String, Object> extraClaims, final UUID apiToken) {
    return Jwts.builder().setHeaderParam("type", "JWT").setClaims(extraClaims)
        .setSubject(apiToken.toString())
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .setIssuedAt(new Date(System.currentTimeMillis())).compact();
  }

  /** Get signing key from configuration. */
  private SecretKey getSigningKey() {
    final var keyBytes = jwtSigningSecretKey.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
