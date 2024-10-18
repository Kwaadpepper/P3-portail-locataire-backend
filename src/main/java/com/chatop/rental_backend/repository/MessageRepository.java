package com.chatop.rental_backend.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.chatop.rental_backend.model.Message;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {

}
