package com.chatop.rental_backend.service.models;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.chatop.rental_backend.model.Rental;
import com.chatop.rental_backend.repository.RentalRepository;

@Service
public class RentalService {
  private final RentalRepository rentalRepository;

  public RentalService(final RentalRepository rentalRepository) {
    this.rentalRepository = rentalRepository;
  }

  public void deleteRental(final Long id) {
    rentalRepository.deleteById(id);
  }

  public Optional<Rental> getRental(final Long id) {
    return rentalRepository.findById(id);
  }

  public Iterable<Rental> getRentals() {
    return rentalRepository.findAll();
  }

  public Rental saveRental(final Rental rental) {
    return rentalRepository.save(rental);
  }

}
