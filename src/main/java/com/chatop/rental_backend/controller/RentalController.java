package com.chatop.rental_backend.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatop.rental_backend.dto.RentalDto;
import com.chatop.rental_backend.dto.RentalListDto;
import com.chatop.rental_backend.dto.SimpleMessage;
import com.chatop.rental_backend.exception.exceptions.ResourceNotFoundException;
import com.chatop.rental_backend.exception.exceptions.ValidationException;
import com.chatop.rental_backend.exception.exceptions.storage.StorageIoException;
import com.chatop.rental_backend.model.Rental;
import com.chatop.rental_backend.model.User;
import com.chatop.rental_backend.presenter.RentalPresenter;
import com.chatop.rental_backend.requests.rentals.CreateRentalRequest;
import com.chatop.rental_backend.requests.rentals.UpdateRentalRequest;
import com.chatop.rental_backend.service.MimeTypeService;
import com.chatop.rental_backend.service.models.RentalService;
import com.chatop.rental_backend.service.storage.StorageService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {
  private final RentalService rentalService;
  private final RentalPresenter rentalPresenter;
  private final StorageService storageService;
  private final MimeTypeService mimeTypeService;

  public RentalController(final RentalService rentalService, final RentalPresenter rentalPresenter,
      final StorageService storageService, final MimeTypeService mimeTypeService) {
    this.rentalService = rentalService;
    this.rentalPresenter = rentalPresenter;
    this.storageService = storageService;
    this.mimeTypeService = mimeTypeService;
  }

  /** Create a rental */
  @Operation(summary = "Create a rental", description = "Create a new rental for a specific user, it is immediately visible")
  @Transactional
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SimpleMessage> createRental(final Authentication authentication,
      @Valid @ModelAttribute @RequestBody final CreateRentalRequest request)
      throws IOException, StorageIoException {

    final var authenticated = (User) authentication.getPrincipal();
    final var newRental = request.create(authenticated);

    final var acceptedMimeTypes = List.of("image/gif", "image/jpeg", "image/png");
    final var isValid = mimeTypeService.validMimeTypeBetween(acceptedMimeTypes,
        request.getPicture().getInputStream());

    if (!isValid) {
      throw new ValidationException(
          "picture file has to be either one of these '%s'".formatted(acceptedMimeTypes));
    }

    final var picturePath = storageService.store(request.getPicture(), Rental.class);

    newRental.setPictures(List.of(picturePath.toString()));

    rentalService.saveRental(newRental);
    return ResponseEntity.ok().body(new SimpleMessage("Rental created !"));
  }

  /** Get a the list of rentals */
  @Operation(summary = "Get rentals", description = "Have a non paginated rental list")
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public RentalListDto getAllRentals() {
    return rentalPresenter.presentModelList(rentalService.getRentals());
  }

  /** Get a specific rental */
  @Operation(summary = "Get a rental", description = "Have specific details about a rental")
  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RentalDto> getRentalById(@PathVariable(value = "id") final long rentalId)
      throws ResourceNotFoundException {

    final var rental = rentalService.getRental(rentalId).orElseThrow(
        () -> new ResourceNotFoundException("Rental not found for this id :: " + rentalId));

    return ResponseEntity.ok().body(rentalPresenter.present(rental));
  }

  /** Update a specific rental */
  @Operation(summary = "Update a rental", description = "Update details about a rental, except the picture that cannot be changed for now")
  @Transactional
  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SimpleMessage> updateRental(@PathVariable(value = "id") final Long rentalId,
      @Valid @ModelAttribute @RequestBody final UpdateRentalRequest request)
      throws ResourceNotFoundException {

    final var rental = rentalService.getRental(rentalId)
        .orElseThrow(() -> new ResourceNotFoundException("This rental does not exists"));

    request.update(rental);
    rentalService.saveRental(rental);

    return ResponseEntity.ok().body(new SimpleMessage("Rental updated !"));
  }
}
