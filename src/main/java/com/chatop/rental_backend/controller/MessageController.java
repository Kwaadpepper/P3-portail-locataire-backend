package com.chatop.rental_backend.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatop.rental_backend.dto.SimpleMessage;
import com.chatop.rental_backend.exception.exceptions.ValidationException;
import com.chatop.rental_backend.model.Message;
import com.chatop.rental_backend.requests.message.SendMessageRequest;
import com.chatop.rental_backend.service.models.MessageService;
import com.chatop.rental_backend.service.models.RentalService;
import com.chatop.rental_backend.service.models.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Messages")
@RestController
@RequestMapping("/api/messages")
public class MessageController {
  private static final Logger logger = LogManager.getLogger(MessageController.class);
  private final MessageService messageService;
  private final UserService userService;
  private final RentalService rentalService;

  public MessageController(final MessageService messageService, final UserService userService,
      final RentalService rentalService) {
    this.messageService = messageService;
    this.userService = userService;
    this.rentalService = rentalService;
  }

  /** Save a new message about a rental */
  @Operation(summary = "Save a message",
      description = "Send a message form a user to another for a specific rental")
  @Transactional
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SimpleMessage> saveNewMessage(
      @Validated @RequestBody final SendMessageRequest request) {

    logger.debug("Saving a new message");

    final var rental = rentalService.getRental(request.rentalId()).orElseThrow(() -> {
      final var errorMessage = "Rental '%s' does not exists".formatted(request.rentalId());
      logger.debug(errorMessage);
      return new ValidationException(errorMessage);
    });

    final var user = userService.getUser(request.userId()).orElseThrow(() -> {
      final var errorMessage = "User '%s' does not exists".formatted(request.userId());
      logger.debug(errorMessage);
      return new ValidationException(errorMessage);
    });

    final var message = new Message(rental, user, request.message());
    messageService.saveMessage(message);
    logger.debug("Message is saved");

    return ResponseEntity.ok(new SimpleMessage("Message send with success"));
  }
}
