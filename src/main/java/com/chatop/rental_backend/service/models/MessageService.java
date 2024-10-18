package com.chatop.rental_backend.service.models;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.chatop.rental_backend.model.Message;
import com.chatop.rental_backend.repository.MessageRepository;

@Service
public class MessageService {
  private final MessageRepository messageRepository;

  public MessageService(final MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  public void deleteMessage(final Long id) {
    messageRepository.deleteById(id);
  }

  public Optional<Message> getMessage(final Long id) {
    return messageRepository.findById(id);
  }

  public Iterable<Message> getMessages() {
    return messageRepository.findAll();
  }

  public Message saveMessage(final Message message) {
    return messageRepository.save(message);
  }

}
