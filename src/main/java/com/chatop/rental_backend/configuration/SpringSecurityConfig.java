package com.chatop.rental_backend.configuration;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.chatop.rental_backend.component.JwtAuthenticationFilter;
import com.chatop.rental_backend.lib.auth.ApiAuthenticationProvider;
import com.chatop.rental_backend.repository.UserRepository;
import com.chatop.rental_backend.service.user_details.ApiUserDetailsService;
import com.chatop.rental_backend.service.user_details.LoginUserDetailsService;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
  private SecurityScheme createAPIKeyScheme() {
    return new SecurityScheme().type(SecurityScheme.Type.HTTP).bearerFormat("JWT").scheme("bearer");
  }

  /** UserDetails service that allow login, password authentication */
  @Bean
  LoginUserDetailsService loginUserDetailsService(final UserRepository userRepository) {
    return new LoginUserDetailsService(userRepository);
  }

  @Bean
  OpenAPI openAPI() {
    return new OpenAPI().addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
        .components(
            new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
        .info(new Info().title("Chatop REST API")
            .description("Chatop Api written as an OpenClassRoom project.").version("1.0")
            .contact(new Contact().name("Munsch Jeremy").email("github@jeremydev.ovh")
                .url("https://jeremydev.ovh"))
            .license(new License().name("MIT").url(
                "https://github.com/Kwaadpepper/P3-portail-locataire-backend/blob/main/LICENCE.md")));
  }

  /** User Argon2 instead of BCrypt. */
  @Bean
  Argon2PasswordEncoder passwordEncoder() {
    return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
  }

  /**
   * Configure Spring Security to disable Web pages features and protect all routes using JWT
   * Filter.
   */
  @Bean
  SecurityFilterChain securityFilterChain(final HttpSecurity http,
      final JwtAuthenticationFilter jwtAuthenticationFilter,
      final AppConfiguration appConfiguration) throws Exception {

    return http.csrf(AbstractHttpConfigurer::disable).cors(AbstractHttpConfigurer::disable)
        .exceptionHandling(handling -> handling
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .authorizeHttpRequests(request -> {
          // Login and Register are not protected.
          request.requestMatchers("/api/auth/login", "/api/auth/register").permitAll();

          // Allow Open api pathes
          if (appConfiguration.apiDocEnabled) {
            request.requestMatchers("/api-docs").permitAll();
            request.requestMatchers("/api-docs/swagger-config").permitAll();
          }
          // Allow Swagger ui pathes
          if (appConfiguration.swaggerUiEnabled) {
            request.requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll();
          }

          // Public serving files.
          request.requestMatchers(new AntPathRequestMatcher("/public/**")).permitAll();

          // Any other routes are.
          request.anyRequest().fullyAuthenticated();
        })
        // No cookie session, just state less API.
        .sessionManagement(
            manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Filter requests to check JWT and assert it matches an actual user.
        .addFilterAt(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).build();
  }

  /**
   * Create an authentication manager with login,password and jwt login providers
   */
  @Bean
  AuthenticationManager usersAuthenticationManager(final HttpSecurity http,
      final AuthenticationConfiguration config, final UserRepository userRepository)
      throws Exception {

    final var authenticationManagerBuilder =
        http.getSharedObject(AuthenticationManagerBuilder.class);

    // UserDetailsServices to Provider wrappers
    final Function<UserDetailsService, DaoAuthenticationProvider> toDaoAuthProvider =
        (final var userDetailsService) -> {
          final var daoAuthProvider = new DaoAuthenticationProvider();
          daoAuthProvider.setUserDetailsService(userDetailsService);
          daoAuthProvider.setPasswordEncoder(passwordEncoder());
          return daoAuthProvider;
        };
    final Function<ApiUserDetailsService, ApiAuthenticationProvider> toApiAuthProvider =
        ApiAuthenticationProvider::new;

    // Add User login provider to the manager
    authenticationManagerBuilder.authenticationProvider(
        toDaoAuthProvider.apply(new LoginUserDetailsService(userRepository)));

    // Add Jwt login provider to the manager
    authenticationManagerBuilder
        .authenticationProvider(toApiAuthProvider.apply(new ApiUserDetailsService(userRepository)));

    return authenticationManagerBuilder.build();
  }
}
