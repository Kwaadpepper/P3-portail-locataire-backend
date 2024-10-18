package com.chatop.rental_backend.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.chatop.rental_backend.model.Rental;

@Repository
public interface RentalRepository extends CrudRepository<Rental, Long> {

}
